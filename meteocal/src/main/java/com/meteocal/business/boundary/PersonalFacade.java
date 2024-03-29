package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.control.OpenWeatherMapController;
import com.meteocal.business.control.UserCalendarManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.Location;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.Stateful;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONException;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Manuel
 */
@Stateful
public class PersonalFacade {

    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;

    @Inject
    LogInManager lm;

    @Inject
    EventCreationManager ev_cm;

    @Inject
    EventManager ev_m;

    @Inject
    UserCalendarManager ucm;

    @Inject
    OpenWeatherMapController owmc;

    /**
     *
     * @return the username of the current user logged in.
     */
    public String getLoggedUser() {
        return lm.getLoggedUserName();
    }

    /**
     * NEW EVENT Creates a new event, given the required parameters and returns
     * true if the event is created. Requires valid name, start and end.
     *
     * @param name
     * @param location
     * @param geoname
     * @param dateTime
     * @param duration
     * @param invited_users
     * @param event_private
     * @param constraint
     * @param description
     * @return true if the event is created
     */
    public boolean createEvent(String name, String location, Integer geoname, Date dateTime, double duration, String invited_users, boolean event_private, String constraint, String description) {
        boolean result = false;
        System.out.println("START createEvent PersonalFacade ---------------");

        //verify mandatory fields (name, dateTime, duration)
        if (name != null && dateTime != null) {

            //calculate date end
            Date end_date = ev_cm.calcDateEnd(dateTime, duration);

            //check constraint
            Integer constr = Integer.parseInt(constraint);

            //generate invited users list
            List<User> invited_users_list = null;
            if (invited_users != null) {
                invited_users_list = new ArrayList<>();
                StringTokenizer people = new StringTokenizer(invited_users, ",");
                User u = null;
                while (people.hasMoreElements()) {
                    u = getUser(people.nextToken().trim());
                    if (u != null && !u.getUserName().equals(getLoggedUser())) {
                        invited_users_list.add(u);
                        System.out.println("-- User " + u.getUserName() + " added to invited_user_list");
                    }
                }
            }

            //retirve creator
            User creator = getUser(getLoggedUser());

            //generate location string
            String event_location = location;
            if (geoname != null) {
                //location with geoname
                event_location = em.createNamedQuery("Location.findByGeonameid", Location.class).setParameter("geonameid", geoname).getSingleResult().toString();
            }

            System.out.println("-- Event name=" + name + " starts=" + dateTime.toString() + " ends=" + end_date.toString() + " location=" + event_location + " private=" + event_private + " constr=" + constr.toString());

            //try to create the event       
            Event event = ev_cm.newEvent(creator, name, dateTime, end_date, event_location, invited_users_list, event_private, constr, description, getNumOverlappingEvents(creator, dateTime, end_date));

            //save event if it have been created     
            if (event != null) {
                System.out.println("-- runtime event created");
                //save in db
                em.flush();
                event = em.merge(event);
                em.flush();
                System.out.println("-- runtime event saved into the DB : event_id = " + event.getEventId());
                result = true;
                if (geoname == null) {
                    //no weather condition is given
                    System.out.println("-- weather conditions : " + constraint);
                } else {
                    //create weather constraint and bind it to the event
                    Weather weather;
                    weather = new Weather(event.getEventId(), geoname);

                    if (constraint != null) {
                        weather.setConstraint(Integer.parseInt(constraint));
                    }

                    //calc desired forecast day
                    Calendar cal_today = Calendar.getInstance();
                    cal_today.setTime(new Date());
                    Calendar cal_day = Calendar.getInstance();
                    cal_day.setTime(event.getStart());
                    int day = cal_day.get(Calendar.DAY_OF_YEAR) - cal_today.get(Calendar.DAY_OF_YEAR) + 1;
                    if (day < 17) {
                        try {
                            weather.setForecast(owmc.getForecast(geoname, day));
                            weather.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                        } catch (JSONException ex) {
                            //Logger.getLogger(PersonalFacade.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    em.persist(weather);
                    em.flush();
                }

                //check forecast
                checkBadWeather(event);

                //send invitations
                if (invited_users_list != null && !invited_users_list.isEmpty()) {
                    ev_m.sendInvitations(invited_users_list, event);
                    //em.merge(event);
                    for (User u : invited_users_list) {
                        em.flush();
                        em.merge(u);
                    }
                }
            }

        }
        return result;

    }

    /**
     *
     * @param username the username of the user you are looking for
     * @return the User you are looking for
     */
    public User getUser(String username) {

        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    /**
     *
     * @param creator
     * @param start
     * @param end
     * @return
     */
    public int getNumOverlappingEvents(User creator, Date start, Date end) {

        em.flush();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        try {
            String query = "SELECT COUNT(e.event_id) FROM events e LEFT JOIN answers a ON e.event_id = a.event_id "
                    + "WHERE (((e.creator = ?) OR ( a.answer_value = 1 AND a.user_id = ?)) "
                    + "AND ((e.start_date <= ? AND e.end_date >= ? ) "
                    + "OR (e.start_date >= ? AND e.end_date >= ? AND e.start_date < ? ) "
                    + "OR (e.start_date <= ? AND e.end_date <= ? AND e.end_date > ? ) "
                    + "OR (e.start_date > ? AND e.end_date < ? ) ) )";
            Long l = (Long) em.createNativeQuery(query)
                    .setParameter(1, creator.getUserId()).setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start)).setParameter(4, formatter.format(end))
                    .setParameter(5, formatter.format(start)).setParameter(6, formatter.format(end)).setParameter(7, formatter.format(end))
                    .setParameter(8, formatter.format(start)).setParameter(9, formatter.format(end)).setParameter(10, formatter.format(start))
                    .setParameter(11, formatter.format(start)).setParameter(12, formatter.format(end))
                    .getSingleResult();
            count = l.intValue();
        } catch (NoResultException ex) {

        }
        return count;

    }

    /**
     *
     * @param creator
     * @param start
     * @param end
     * @param eventId
     * @return
     */
    private int getNumOverlappingEvents(User creator, Date start, Date end, int eventId) {

        em.flush();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;

        try {

            String query = "SELECT COUNT(e.event_id) FROM events e LEFT JOIN answers a ON e.event_id = a.event_id "
                    + "WHERE (((e.creator = ?) OR ( a.answer_value = 1 AND a.user_id = ?)) "
                    + "AND ((e.start_date <= ? AND e.end_date >= ? ) "
                    + "OR (e.start_date >= ? AND e.end_date >= ? AND e.start_date < ? ) "
                    + "OR (e.start_date <= ? AND e.end_date <= ? AND e.end_date > ? ) "
                    + "OR (e.start_date > ? AND e.end_date < ? )))"
                    + "AND e.event_id <> ?";
            Long l = (Long) em.createNativeQuery(query)
                    .setParameter(1, creator.getUserId()).setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start)).setParameter(4, formatter.format(end))
                    .setParameter(5, formatter.format(start)).setParameter(6, formatter.format(end)).setParameter(7, formatter.format(end))
                    .setParameter(8, formatter.format(start)).setParameter(9, formatter.format(end)).setParameter(10, formatter.format(start))
                    .setParameter(11, formatter.format(start)).setParameter(12, formatter.format(end))
                    .setParameter(13, eventId)
                    .getSingleResult();
            count = l.intValue();

        } catch (NoResultException ex) {

        }
        return count;

    }

    /**
     * Returns a list of all the possible countries
     *
     * @return a lost of strings of distinct available countries
     */
    public List<String> getCountries() {

        return em.createNativeQuery("SELECT DISTINCT country FROM locations").getResultList();

    }

    /**
     * Returns a list of all the capable provinces
     *
     * @param c the country to be filtered
     * @return a list of provinces that are in the given country
     */
    public List<String> getProvinces(String c) {

        return em.createNativeQuery("SELECT DISTINCT admin2 FROM locations l WHERE l.country = ? ORDER BY admin2").setParameter(1, c).getResultList();

    }

    /**
     * Returns a list of all the capable cities
     *
     * @param c the country to filter the province
     * @param p the province to be filtered
     * @return a list of cities that are in the given province
     */
    public List<String> getCities(String c, String p) {

        return em.createNativeQuery("SELECT name FROM locations l WHERE l.country = ? AND l.admin2 = ? ORDER BY name").setParameter(1, c).setParameter(2, p).getResultList();

    }

    /**
     * Returns the location id
     *
     * @param c the country
     * @param p the province
     * @param n the city
     * @return location id (geonameid)
     */
    public Integer getGeoname(String c, String p, String n) {

        return (Integer) em.createNativeQuery("SELECT geonameid FROM locations l WHERE l.country = ? AND l.admin2 = ? AND l.name = ?").setParameter(1, c).setParameter(2, p).setParameter(3, n).getSingleResult();

    }

    /**
     * Says how we can change the calendar status
     *
     * @return "Set calendar as public" id the calendar is private -- "Set
     * calendar as private" otherwise
     */
    public String getCalendarString() {

        String out = "Set calendar as public";

        if (getUser(getLoggedUser()).isPublicCalendar()) {
            out = "Set calendar as private";
        }

        return out;

    }

    /**
     * It changes the privacy setting of the given user (from public to private
     * or vice versa).
     *
     */
    public void togglePrivacy() {

        User u = getUser(getLoggedUser());
        u.setPublicCalendar(!u.isPublicCalendar());

    }

    /**
     * Start the download of the user's calendar
     *
     * @return a link to the file to be downloaded
     */
    public void startDownload() {

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType("application/octet-stream"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        //ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + getLoggedUser() + "_meteocal.dat" + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        try {
            InputStream input = ucm.startDownload(getUser(getLoggedUser()));
            OutputStream output = ec.getResponseOutputStream();
            // Now you can write the InputStream of the file to the above OutputStream the usual way.
            // ...
            int numRead;
            byte[] buf = new byte[8192];
            while ((numRead = input.read(buf)) >= 0) {
                output.write(buf, 0, numRead);
            }
            input.close();
            output.close();
        } catch (IOException ex) {
            //Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.

    }

    /**
     * Gets all the events the user is/was attending;
     *
     * @return ScheduleModel contains all events
     */
    public ScheduleModel getAllEvents() {

        ScheduleModel eventModel = new DefaultScheduleModel();
        User user = getUser(getLoggedUser());

        List<Event> list_events = user.getEvents();

        list_events.stream().forEach((ev) -> {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd()));
            } else {
                eventModel.addEvent(new DefaultScheduleEvent(Character.toString((char) 254) + ev.getName(), ev.getStart(), ev.getEnd()));
            }
        });

        return eventModel;

    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public ScheduleModel getEvents(Date from, Date to) {

        ScheduleModel eventModel = new DefaultScheduleModel();
        User user = getUser(getLoggedUser());
        em.refresh(user);
        List<Event> list_events = user.getEvents(from, to);

        list_events.stream().forEach((ev) -> {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
            } else {
                eventModel.addEvent(new DefaultScheduleEvent("(P) " + ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
            }
        });

        return eventModel;
    }

    /**
     *
     * @param param
     * @return
     */
    public List<String> searchUser(String param) {

        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM users WHERE (UPPER(users.user_name) LIKE UPPER(?) OR UPPER(users.first_name) LIKE UPPER(?) OR UPPER(users.last_name) LIKE UPPER(?)) AND users.user_name <> ?";
        List<User> users_founded = em.createNativeQuery(query, User.class)
                .setParameter(1, param + "%").setParameter(2, param + "%").setParameter(3, param + "%").setParameter(4, getLoggedUser())
                .getResultList();

        if (users_founded != null) {
            users_founded.stream().forEach((u) -> {
                result.add(u.toString());
            });
        }

        return result;

    }

    /**
     *
     * @param username
     * @return
     */
    public String getUserId(String username) {

        return getUser(username).getUserId().toString();

    }

    /**
     *
     * @param eventId
     * @param name
     * @param location
     * @param geoname
     * @param dateTime
     * @param duration
     * @param invited_users
     * @param event_private
     * @param constraint
     * @param description
     * @return
     */
    public boolean updateEvent(int eventId, String name, String location, Integer geoname, Date dateTime, double duration, String invited_users, boolean event_private, String constraint, String description) {

        boolean result = false;
        Date end = ev_cm.calcDateEnd(dateTime, duration);

        //get the original event
        Event event = em.find(Event.class, eventId);

        //update the event if not overlap  
        if (name != null && dateTime != null && getNumOverlappingEvents(event.getCreator(), dateTime, end, eventId) < 1) {

            //verify mandatory fields (name, dateTime, duration)
            event.setName(name);
            event.setStart(dateTime);
            //calculate date end
            event.setEnd(end);
            //generate invited users list
            List<User> invited_users_list = new ArrayList<>();
            if (invited_users != null) {

                StringTokenizer people = new StringTokenizer(invited_users, ",");
                User u;
                while (people.hasMoreElements()) {
                    u = getUser(people.nextToken().trim());

                    if (u != null && !u.getUserName().equals(getLoggedUser())) {
                        invited_users_list.add(u);
                    }
                }
            }

            event.setDescription(description);
            em.flush();
            em.refresh(event);

            //set constraint
            if (event.getWeather() != null) {
                em.remove(event.getWeather());
                em.flush();
            }

            if (geoname == null) {

                event.setLocation(location);
                event.setWeather(null);

            } else {
                event.setLocation(em.createNamedQuery("Location.findByGeonameid", Location.class).setParameter("geonameid", geoname).getSingleResult().toString());
                event.setWeather(new Weather(eventId, geoname));
                Weather w = event.getWeather();
                w.setConstraint(Integer.parseInt(constraint));

                //calc desired forecast day
                Calendar cal_today = Calendar.getInstance();
                cal_today.setTime(new Date());
                Calendar cal_day = Calendar.getInstance();
                cal_day.setTime(event.getStart());
                int day = cal_day.get(Calendar.DAY_OF_YEAR) - cal_today.get(Calendar.DAY_OF_YEAR) + 1;
                if (day < 17) {
                    try {
                        w.setForecast(owmc.getForecast(geoname, day));
                        w.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                    } catch (JSONException ex) {
                        //Logger.getLogger(PersonalFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //save in db
                em.flush();
                event.setWeather(w);
                em.merge(w);
                em.merge(event);
                em.flush();
                em.refresh(event);

            }

            //save in db
            if (invited_users_list == null || invited_users_list.isEmpty()) {
                event.setPersonal(true);
            }
            event.setPublicEvent(event_private);
            event = em.merge(event);
            em.flush();
            result = true;

            //check forecast
            checkBadWeather(event);

            //revoke or send new invitations
            //check revoke invitations | if the user was an attendee then info+mail
            for (User u : event.getAttendee()) {
                if (!invited_users_list.contains(u)) {
                    //revoke+mail
                    Answer ans = (Answer) em.createNativeQuery("SELECT * FROM answers WHERE answers.event_id = ? AND answers.user_id = ?", Answer.class).setParameter(1, event.getEventId()).setParameter(2, u.getUserId()).getSingleResult();
                    ev_m.revokeParticipation(u, event, ans);
                    em.remove(ans);
                    Information info = new Information(event, u, "Your participation has been revoked.");
                    em.merge(info);
                    em.merge(event);
                    em.flush();
                }
            }
            for (User u : event.getMaybeGoing()) {
                if (!invited_users_list.contains(u)) {
                    //revoke
                    ev_m.revokeInvitation(u, event);
                    em.merge(event);
                    em.merge(u);
                }
            }
            //create list new invitation
            List<User> temp = new ArrayList<>(invited_users_list);
            for (User u : temp) {
                if (event.getMaybeGoing().contains(u) || event.getAttendee().contains(u)) {
                    //no invitation
                    invited_users_list.remove(u);
                }
            }

            //delete answers
            for (User u : invited_users_list) {
                if (event.getDeclined().contains(u)) {
                    //delete old answer
                    Answer a = (Answer) em.createNativeQuery("SELECT * FROM answers a WHERE a.event_id = ? AND a.user_id = ?", Answer.class).setParameter(1, event.getEventId()).setParameter(2, u.getUserId()).getSingleResult();
                    event.getAnswerCollection().remove(a);
                    u.getAnswerCollection().remove(a);
                    em.remove(a);
                    em.flush();
                }
            }

            //invite people
            ev_m.sendInvitations(invited_users_list, event);
            for (User u : invited_users_list) {
                em.flush();
                em.merge(u);
            }

            //send info to old attendees
            for (User u : event.getAttendee()) {
                Information info = new Information(event, u, "Event details have been updated.");
                em.merge(info);
            }

        }

        return result;

    }

    /**
     *
     * @param eventId
     * @return
     */
    public boolean isFinished(int eventId) {

        boolean out = false;
        Event event = em.find(Event.class, eventId);

        if (event.getEnd().before(new Date())) {
            out = true;
        }

        return out;

    }

    /**
     *
     * @return
     */
    public boolean haveGotNotifications() {

        boolean out = false;
        User u = getUser(getLoggedUser());

        //check info
        if (u.getInformations() != null && !u.getInformations().isEmpty() || u.getInvitations() != null && !u.getInvitations().isEmpty()) {
            out = true;
        }

        return out;

    }
    
    /**
     * check if the constraint has been violated (works for events that start within 24h)
     * 
     * @param event 
     */
    private void checkBadWeather(Event event) {
        
        //upper boud
        Calendar by24h = Calendar.getInstance();
        by24h.setTime(new Date());
        by24h.add(Calendar.DAY_OF_YEAR, 1);
        by24h.add(Calendar.SECOND, -1);
        
        if (event.getStart().before(by24h.getTime())) {
            
            em.flush();
            
            if (!ev_m.checkWeather(event)) {
                System.out.println("----| constraint violated");
                //code for tomorrow events
                System.out.println("----| event start in one days");
                //new information for the creator
                Information info = ev_m.newInformation(event.getCreator(), "The forecast does not respect your constraints. Please reschedule such event.", event);
                em.merge(info);
                em.flush();
            }
            
        }
        
    }

    
    
}

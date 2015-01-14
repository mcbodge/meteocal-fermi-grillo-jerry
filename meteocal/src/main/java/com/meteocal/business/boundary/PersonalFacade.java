/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.control.UserCalendarManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Location;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "START createEvent PersonalFacade ---------------");

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
                    if (u != null) {
                        invited_users_list.add(u);
                        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- User {0} added to invited_user_list", u.getUserName());
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

            Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- Event name={0} starts={1} ends={2} location={3} invited_users={4} private={5} constr={6} ", new Object[]{name, dateTime.toString(), end_date.toString(), event_location, invited_users_list != null, event_private, constr.toString()});

            //try to create the event       
            Event event = ev_cm.newEvent(creator, name, dateTime, end_date, event_location, invited_users_list, event_private, constr, description, getNumOverlappingEvents(creator, dateTime, end_date));

            //save event if it have been created     
            if (event != null) {
                Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- runtime event created: event_name = {0} start = {1} end = {2} location = {3}",
                        new Object[]{event.getName(), event.getStart(), event.getEnd(), event.getLocation()});
                //save in db
                em.flush();
                event = em.merge(event);
                em.flush();
                Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- runtime event saved into the DB : event_id = {0}", event.getEventId());
                result = true;
                if (geoname == null) {
                    //no weather condition is given
                    Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- weather conditions : {0}", constraint);
                } else {
                    //create weather constraint and bind it to the event
                    Weather weather;
                    weather = new Weather(event.getEventId(), geoname);

                    if (constraint != null) {
                        weather.setConstraint(Integer.parseInt(constraint));
                    }

                    //weather.setLocationCode(geoname);
                    em.persist(weather);
                    em.flush();
                }

                //send invitations
                if (invited_users_list != null && !invited_users_list.isEmpty()) {
                    Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- call sendInvitations(..) ");
                    ev_m.sendInvitations(invited_users_list, event);
                    Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- try to save invitations for the event");
                    //em.merge(event);
                    for (User u : invited_users_list) {
                        em.flush();
                        em.merge(u);
                        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- invitation to {0} stored", u.getUserName());
                    }
                    Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- all invitations have been stored");
                }
            }

        }
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "STOP createEvent PersonalFacade ---------------");
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

    private int getNumOverlappingEvents(User creator, Date start, Date end) {
        em.flush();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        try {
            String query = "SELECT COUNT(e.event_id) FROM events e LEFT JOIN answers a ON e.event_id = a.event_id "
                    + "WHERE (((e.creator = ?) OR ( a.answer_value = 1 AND a.user_id = ?)) "
                    + "AND("
                    + "(e.start_date <= ? AND e.end_date >= ? ) OR"
                    + "(e.start_date >= ? AND e.end_date >= ? AND e.start_date < ? ) OR"
                    + "(e.start_date <= ? AND e.end_date <= ? AND e.end_date > ? ) OR"
                    + "(e.start_date > ? AND e.end_date < ? ) "
                    + ")"
                    + ")";
            Long l = (Long) em.createNativeQuery(query)
                    .setParameter(1, creator.getUserId())
                    .setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start))
                    .setParameter(4, formatter.format(end))
                    .setParameter(5, formatter.format(start))
                    .setParameter(6, formatter.format(end))
                    .setParameter(7, formatter.format(end))
                    .setParameter(8, formatter.format(start))
                    .setParameter(9, formatter.format(end))
                    .setParameter(10, formatter.format(start))
                    .setParameter(11, formatter.format(start))
                    .setParameter(12, formatter.format(end))
                    .getSingleResult();
            Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- query = {0}", query);
            Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- (long)num overlapping events = {0}", l);
            count = l.intValue();
        } catch (NoResultException ex) {

        }
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- num overlapping events = {0}", count);
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
    public String startDownload() {
        return ucm.startDownload(getUser(getLoggedUser()));
    }

    /**
     * Gets all the events the user is/was attending;
     *
     * @return ScheduleModel contains all events
     */
    public ScheduleModel getAllEvents() {
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "START getAllEvents() -------------------");
        ScheduleModel eventModel = new DefaultScheduleModel();
        User user = getUser(getLoggedUser());
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- user = {0}", user.getUserName());

        List<Event> list_events = user.getEvents();

        list_events.stream().forEach((ev) -> {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd()));
            } else {
                eventModel.addEvent(new DefaultScheduleEvent(Character.toString((char) 254) + ev.getName(), ev.getStart(), ev.getEnd()));
            }
        });
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "STOP getAllEvents() -------------------");
        return eventModel;
    }

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

    public List<String> searchUser(String param) {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM users WHERE (UPPER(users.user_name) LIKE UPPER(?) OR UPPER(users.first_name) LIKE UPPER(?) OR UPPER(users.last_name) LIKE UPPER(?)) AND users.user_name <> ?";
        List<User> users_founded = em.createNativeQuery(query, User.class)
                .setParameter(1, param + "%")
                .setParameter(2, param + "%")
                .setParameter(3, param + "%")
                .setParameter(4, getLoggedUser())
                .getResultList();
           Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- query = {0}", query);
        if (users_founded != null) {
            users_founded.stream().forEach((u) -> {
                result.add(u.toString());
            });
        }
        return result;
    }

    
    public String getUserId(String username) {
        return getUser(username).getUserId().toString();
    }


    public boolean updateEvent(int eventId, String name, String location, Integer geoname, Date dateTime, double duration, String invited_users, boolean event_private, String constraint, String description) {
        boolean result = false;

        //get the original event
        Event event = em.find(Event.class, eventId);
        if (!isFinished(eventId)) {

            //verify mandatory fields (name, dateTime, duration)
            if (name != null && dateTime != null) {

                //calculate date end
                event.setEnd(ev_cm.calcDateEnd(dateTime, duration));

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
                        if (u != null) {
                            invited_users_list.add(u);
                        }
                    }
                }

                //generate location string
                String event_location = location;
                if (geoname != null) {
                    //location with geoname
                    event_location = em.createNamedQuery("Location.findByGeonameid", Location.class).setParameter("geonameid", geoname).getSingleResult().toString();
                }
                event.setLocation(event_location);

                //update the event if not overlap    
                if (getNumOverlappingEvents(event.getCreator(), event.getStart(), event.getEnd()) == 0) {
                    //save in db
                    event = em.merge(event);
                    em.flush();
                    result = true;
                    Weather weather = em.find(Weather.class, eventId);
                    if (geoname == null) {
                        //no weather condition is given, delete old
                        if (weather != null) {
                            em.remove(weather);
                            em.flush();
                        }
                    } else {
                        //create weather constraint and bind it to the event
                        if (constraint != null) {
                            weather.setConstraint(Integer.parseInt(constraint));

                            //weather.setLocationCode(geoname);
                            em.persist(weather);
                            em.flush();
                        }
                    }
                    //revoke or send invitations
                    if (invited_users_list != null && !invited_users_list.isEmpty()) {

                        //check revoke invitations | if the user was an attendee then info+mail
                        for (User u : event.getAttendee()) {
                            if (!invited_users_list.contains(u)) {
                                //revoke+mail
                                Answer ans = (Answer) em.createNativeQuery("SELECT * FROM answers WHERE answers.event_id = ? AND answers.user_id = ?", Answer.class).setParameter(1, event.getEventId()).setParameter(2, u.getUserId()).getSingleResult();
                                ev_m.revokeParticipation(u, event, ans);
                                em.remove(ans);
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

                        //invite only new users;
                        for (User u : invited_users_list) {
                            if (event.getMaybeGoing().contains(u) || event.getAttendee().contains(u)) {
                                //no invitation 
                                invited_users_list.remove(u);
                            }
                        }

                        ev_m.sendInvitations(invited_users_list, event);
                        for (User u : invited_users_list) {
                            em.flush();
                            em.merge(u);
                        }
                    }
                }

            }
            Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "STOP createEvent PersonalFacade ---------------");
        }
        return result;
    }

    public boolean isFinished(int eventId) {
        boolean out = false;
        Event event = em.find(Event.class, eventId);
        if (event.getEnd().before(new Date())) {
            out = true;
        }
        return out;
    }


}

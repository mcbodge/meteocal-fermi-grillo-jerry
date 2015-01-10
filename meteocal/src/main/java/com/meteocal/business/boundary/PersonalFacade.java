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
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
     * @param dateTime
     * @param duration
     * @param invited_users
     * @param event_private
     * @param constraint
     * @param description
     * @return true if the event is created
     */
    public boolean createEvent(String name, String location, Date dateTime, double duration, String invited_users, boolean event_private, String constraint, String description) {
        boolean result = false;
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "START createEvent PersonalFacade ---------------");

        //verify mandatory fields (name, dateTime, duration)
        if (name != null && dateTime != null) {
            
            //calculate date end
            Date end_date = ev_cm.calcDateEnd(dateTime, duration);

            //check constraint
            Integer constr = Integer.parseInt(constraint);
            Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- Event name={0} starts={1} ends={2} constr={3} location={4}", new Object[]{name, dateTime.toString(),end_date.toString(), constr.toString(), location});
            
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

            //save event if it have been created
            User creator = getUser(getLoggedUser());
            Event event = ev_cm.newEvent(creator, name, dateTime, end_date, location, invited_users_list, event_private, constr, description, getNumOverlappingEvents(creator, dateTime, end_date));
            
            if (event != null) {
                Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- runtime event created: event_name = {0} start = {1} end = {2} location = {3}",
                        new Object[]{event.getName(), event.getStart(), event.getEnd(), event.getLocation()});
                //save in db
                event = em.merge(event);
                em.flush();
                Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- runtime event saved into the DB : event_id = {0}", event.getEventId());
                
                //no weather condition is given
                Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- weather conditions : {0}", constraint);

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
    private User getUser(String username) {
        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    

    //TODO fix the query 
    private int getNumOverlappingEvents(User creator, Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        try {
            Long l = (Long) em.createNativeQuery("SELECT COUNT(*) FROM events e INNER JOIN answers a ON e.event_id = a.event_id "
                    + "WHERE ((e.creator = ?) OR ( a.answer_value = 1 AND a.user_id = ?)) AND (e.start_date <= '?' AND e.end_date >= '?')")
                    .setParameter(1, creator.getUserId())
                    .setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start))
                    .setParameter(4, formatter.format(end))
                    .getSingleResult();
            count = l.intValue();
        } catch (NoResultException ex) {

        }
        return count;
    }

    

    /**
     * Returns a list of all the possible countries
     * @return a lost of strings of distinct available countries
     */
    public List<String> getCountries() {
        return em.createNativeQuery("SELECT DISTINCT country FROM locations").getResultList();
    }
    

    /**
     * Returns a list of all the capable provinces
     * @param c the country to be filtered
     * @return a list of provinces that are in the given country
     */
    public List<String> getProvinces(String c) {
        return em.createNativeQuery("SELECT DISTINCT admin2 FROM locations l WHERE l.country = ? ORDER BY admin2").setParameter(1, c).getResultList();
    }
    

    /**
     * Returns a list of all the capable cities
     * @param c the country to filter the province
     * @param p the province to be filtered
     * @return a list of cities that are in the given province
     */
    public List<String> getCities(String c, String p) {
        return em.createNativeQuery("SELECT name FROM locations l WHERE l.country = ? AND l.admin2 = ? ORDER BY name").setParameter(1, c).setParameter(2, p).getResultList();
    }

    
    /**
     * Returns the location id
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
     * @return "Set calendar as public" id the calendar is private -- "Set calendar as private" otherwise
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
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- user = {0}",user.getUserName());
        
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- call User.getEvents");
        List<Event> list_events = user.getEvents();
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- list_events = {0}",list_events.toString());
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- populate eventModel");
        
        for (Iterator<Event> it = list_events.iterator(); it.hasNext();) {
             Event ev = it.next();
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd()));
            } else {
                eventModel.addEvent(new DefaultScheduleEvent(Character.toString((char)254) + ev.getName(), ev.getStart(), ev.getEnd()));
            }
        }
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- eventModel ready");
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "STOP getAllEvents() -------------------");
        return eventModel;
    }
    
    public ScheduleModel getEvents(Date from, Date to){
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "START getAllEvents() -------------------");
        
        ScheduleModel eventModel = new DefaultScheduleModel();
        User user = getUser(getLoggedUser());
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- user = {0}",user.getUserName());
        
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- call User.getEvents");
        List<Event> list_events = user.getEvents(from, to);
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- list_events = {0}",list_events.toString());
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- populate eventModel");
        for (Event ev : list_events) {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd()));
            } else {
                eventModel.addEvent(new DefaultScheduleEvent("? " + ev.getName(), ev.getStart(), ev.getEnd()));
            }
        }
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- eventModel ready");
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "STOP getAllEvents() -------------------");
        return eventModel;
    }

}

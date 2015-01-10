/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * FROM/TO - B:PersonalPage
 * TO - E:User, E:Event, C:EventManager
 * 
 * @author Manuel
 */
public class EventCreationManager {
    
    @Inject
    EventManager ev_m;

    /**
     * Calculate when the event will be over
     * @param dateStart 
     * @param duration
     * @return Date end date, when the event will be over.
     */
    public Date calcDateEnd(Date dateStart, double duration) {
        //get hours and minutes to add
        int hours = new Double(duration).intValue();
        int minutes = 0;
        if (duration - hours > 0) {
            minutes = 30;
        }

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(dateStart); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, hours); // adds hours
        cal.add(Calendar.MINUTE, minutes); // adds minutes

        return cal.getTime(); // returns new date object, in the future
    }
    
    /**
     * NEW EVENT, GIVING THE LOCATION normal-language STRING
     * Creates a new event, given the required parameters and returns the Event Obj.
     * Requires valid creator, name, start and end.
     * 
     * @param creator the creator (User).
     * @param name the name of the event.
     * @param start the start datetime of the event (w/ year, month, day, hour and minute).
     * @param end the end datetime of the event (w/ year, month, day, hour and minute).
     * @param location the location of the event, generated or given by the user, or null.
     * @param invited the list of the users invited to the event, or null.
     * @param p the privacy value 0 for public, 1 for private.
     * @param constraint the generated constraint value of the event, or null.
     * @param description the description of the event, or null.
     * @return null if no event is created.
     */
    public Event newEvent(User creator, String name, Date start, Date end, String location, List<User> invited, boolean p, Integer constraint, String description, int numOverlappingEvents){
        Event event_result = null;
        Logger.getLogger(EventCreationManager.class.getName()).log(Level.INFO, "---- START newEvent in EventCreationManager ---------------");
        
        if(name!= null && ev_m.verifyConsistency(creator, start, end, numOverlappingEvents)){
            //consistency ok
            event_result = new Event(creator, name, location, start, end, p);
           
            if(description != null && !description.isEmpty()){
                event_result.setDescription(description);
            }
            
            //personal event if and only if the creator is the only attender.
            if(invited == null || invited.isEmpty()) {
                event_result.setPersonal(true); 
            } else {
                event_result.setPersonal(false);
            }
            
        }
        
        boolean param = (event_result != null);
        
        Logger.getLogger(EventCreationManager.class.getName()).log(Level.INFO, "------ result: {0}", param);
        Logger.getLogger(EventCreationManager.class.getName()).log(Level.INFO, "---- END newEvent in EventCreationManager ---------------");
        
        return event_result;
    }
    
    /*
    /**
     * NEW EVENT, GIVING THE LOCATION ID
     * The name of the location is automatically generated***
     * Creates a new event, given the required parameters and returns its id.
     * Requires valid creator, name, start and end.
     * 
     * @param creator the creator's userId.
     * @param name the name of the event.
     * @param start the start datetime of the event (w/ year, month, day, hour and minute).
     * @param end the end datetime of the event (w/ year, month, day, hour and minute).
     * @param geoname the generated location of the event (if it's null no Event is created).
     * @param invited the list of the users invited to the event, or null.
     * @param p the privacy value 0 for public, 1 for private.
     * @param constraint the generated constraint value of the event, or null.
     * @param description the description of the event, or null.
     * @return the eventId of the created event. Null if no event is created.
     */
    /*
    public Integer newEvent(User creator, String name, Date start, Date end, Integer geoname, List<User> invited, boolean p, Integer constraint, String description){
        if(geoname != null && start != null && end != null && name!= null && EventManager.getInstance().verifyConsistency(creator, start, end)){
            String location = em.createNamedQuery("Location.findByGeonameid",Location.class).setParameter("geonameid", geoname).getSingleResult().toString();
            Event event = new Event(creator, name, location, start, end, p);
            
            if(!description.isEmpty())
                event.setDescription(description);
            
            //personal event if and only if the creator is the only attender.
            if( invited == null || invited.isEmpty()) {
                event.setPersonal(true);
            } else {
                event.setPersonal(false);
            }
            
            //save in db
            em.persist(event);
            
            //create weather constraint and bind it to the event
            Weather weather;
            weather = new Weather(event.getEventId(), geoname);
            
            if(constraint != null)
                weather.setConstraint(constraint);
            
            //weather.setLocationCode(geoname);
            
            em.persist(weather);
            
            //send invitations
            if(!invited.isEmpty())
                EventManager.getInstance().sendInvitations(invited, event);

            return event.getEventId();
        }
        //no consistency
        return null;
    }
    */
}

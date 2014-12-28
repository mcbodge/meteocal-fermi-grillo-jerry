/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * FROM/TO - B:PersonalPage
 * TO - E:User, E:Event, C:EventManager
 * 
 * @author Manuel
 */
public class EventCreationManager {
    @PersistenceContext
    EntityManager em;
    
    //TODO verify :)
    /**
     * NEW EVENT, GIVING THE LOCATION normal-language STRING
     * Creates a new event, given the required parameters and returns its id.
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
     * @return the eventId of the created event. Null if no event is created.
     */
    public Integer newEvent(User creator, String name, Date start, Date end, String location, List<User> invited, boolean p, Integer constraint, String description){
        
        if(start != null && end != null && name!= null && verifyConsistency(creator, start, end)){
            //consistency ok
            Event event = new Event(creator, name, location, start, end, p);
            
            if(description.isEmpty()){
                event.setDescription(description);
            }
            
            if(invited.isEmpty()) {
                //add nobody
                event.setPersonal(true); //personal event if and only if the creator is the only attender.
            } else {
                event.setInvitedUserCollection(invited);
                event.setPersonal(false);
            }
            
            //save in db
            em.persist(event);
            
            //no weather condition is given
          
            //send invitations
            if(!invited.isEmpty())
                sendInvitations(invited, event);
            
            return event.getEventId();
        }
        return null;
    }
    
        //TODO create location string
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
    public Integer newEvent(User creator, String name, Date start, Date end, Integer geoname, List<User> invited, boolean p, Integer constraint, String description){
        if(geoname != null && start != null && end != null && name!= null && verifyConsistency(creator, start, end)){
            String location = ""; //TODO in Locations.toString() -> <getName(geoname)>+" ("+<getAdmin2(geoname)>+") - "+getCountry(geoname) //***
            Event event = new Event(creator, name, location, start, end, p);
            
            if(description.isEmpty())
                event.setDescription(description);
            
            
            if(invited.isEmpty()) {
                //add noone
                event.setPersonal(true); //personal event if and only if the creator is the only attender.
            } else {
                event.setInvitedUserCollection(invited);
                event.setPersonal(false);
            }
            
            //save in db
            em.persist(event);
            
            //create weather constraint and bind it to the event
            Weather weather = new Weather(event.getEventId());
            
            if(constraint!=null)
                weather.setConstraint(constraint);
            
            //weather.setLocationCode(geoname);
            
            em.persist(weather);
            
            //send invitations
            if(!invited.isEmpty())
                sendInvitations(invited, event);
            
            
            return event.getEventId();
        }
        //no consistency
        return null;
    }
    
    //TODO test
    /**
     * Allows to verify the time consistency of an event given its creator.
     * It returns true if no other event of the given user is in the db after start and before end.
     * 
     * @param creator the creator (User).
     * @param start the start datetime of the event (w/ year, month, day, hour and minute).
     * @param end the end datetime of the event (w/ year, month, day, hour and minute).
     * @return true if no events overlaps -- false otherwise.
     */    
    public boolean verifyConsistency(User creator, Date start, Date end){
        
        //check if end is > then start
        if(end.before(start))
            return false;
        
        //load event created by the user and events that user attends 
        TypedQuery<Event> query;
        query = (TypedQuery<Event>) em.createNativeQuery(
                "SELECT e FROM Event e, Answer a"
                        + "WHERE (e.start <= :start AND e.end >= :end) "
                            + "AND ((e.creator = :creator) "
                            + "OR (a.event_id = e.event_id AND a.value = 1 AND :creator = a.user_id))")
                .setParameter("creator", creator.getUserId())
                .setParameter("start", start)
                .setParameter("end", end);
        
        return start.after(Date.from(Calendar.getInstance().toInstant())) && query.getResultList().isEmpty();
    }
    
    
    //TODO for-each of newInvitation()
    /**
     * Invites the given users to the given event.
     * 
     * @param ul list of users that have to receive the invitations.
     * @param event the event the users have to be invited to.
     */
    public void sendInvitations(List<User> ul, Event event){ 
        //richiamato anche quando modifichi...quindi prima check se sono già stati invitati.
    }
    
    
}

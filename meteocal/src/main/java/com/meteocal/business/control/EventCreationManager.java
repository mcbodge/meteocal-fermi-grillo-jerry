/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.util.Date;
import java.util.List;

/**
 * FROM/TO - B:PersonalPage
 * TO - E:User, E:Event, C:EventManager
 * 
 * @author Manuel
 */
public class EventCreationManager {
    
    //TODO
    /**
     * Creates a new event, given the required parameters and returns its id. If no valid creator or name or start or end 
     * 
     * @param creator the creator's userId.
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
    public Integer newEvent(Integer creator, String name, Date start, Date end, String location, List<User> invited, boolean p, Integer constraint, String description){
        return null;
    }
    
    //TODO
    /**
     * Allows to verify the time consistency of an event given its creator.
     * It returns true if no other event of the given user is in the db after start and before end.
     * 
     * @param creator the creator's userId.
     * @param start the start datetime of the event (w/ year, month, day, hour and minute).
     * @param end the end datetime of the event (w/ year, month, day, hour and minute).
     * @return true if no events overlaps -- false otherwise.
     */    
    public boolean verifyConsistency(Integer creator, Date start, Date end){
        return false;
        
    }
    
    //TODO for-each of newInvitation()
    /**
     * Invites the given users to the given event.
     * 
     * @param ul list of users that have to receive the invitations.
     * @param event the event the users have to be invited to.
     */
    public void sendInvitations(List<User> ul, Event event){        
    }
    
    
}

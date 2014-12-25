package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;

/**
 * FROM - C:EventCreationManager
 * FROM/TO - B: NotificationP
 * TO - E:Event, E:Notification(Invitation & Answer), E:User
 * @author Manuel
 */
public class EventManager {
    
    //TODO
    /**
     * Creates a new invitation.
     * 
     * @param u the receiver.
     * @param e the involved event.
     */
    public void newInvitation(User u, Event e){
        
    }
    
    //TODO event field in the db is set as null.
    /**
     * Creates a new Information, not necessarily related to an event.
     * 
     * @param u the receiver.
     * @param message the actual information.
     */
    public void newInformation(User u, String message){
        
    }
    
    //TODO
    /**
     * Creates a new Information, related to an event.
     * 
     * @param u the receiver.
     * @param message the actual information.
     * @param e the related event.
     */
    public void newInformation(User u, String message, Event e){
        
    }
    
    //TODO
    /**
     * Remove a previously created invitation.
     * 
     * @param u the former-receiver.
     * @param e the former-related-event.
     */
    public void revokeInvitation(User u, Event e){
        
    }
    
    //TODO remember to check if it exists
    /**
     * Accept a received invitation.
     * 
     * @param u the receiver.
     * @param e the event.
     */
    public void acceptInvitation(User u, Event e){
        
    }
    
    //TODO remember to check if it exists 
    /**
     * Decline a received invitation.
     * 
     * @param u
     * @param e 
     */
    public void declineInvitation(User u, Event e){
        
    }
    
    //TODO *any other condition*
    /**
     * Given an event it says if it has adverse weather conditions.
     * If there are adverse weather conditions, it returns false. In any other condition it returns true.
     * 
     * @param e the event w/ the weather conditions we want to check.
     * @return false if there are adverse weather conditions -- true otherwise.
     */
    public boolean checkWeather(Event e){
        return false;
        
    }
}

package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.WeatherCondition;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * FROM - C:EventCreationManager FROM/TO - B: NotificationP TO - E:Event,
 * E:Notification(Invitation & Answer), E:User
 *
 * @author Manuel
 */
public class EventManager {
    @Inject
    EmailManager email_mng;
    
    /**
     * Creates a new invitation.
     *
     * @param u the receiver.
     * @param e the involved event.
     */
    public void newInvitation(User u, Event e) {
        
        //check user has not been already invited
        if (e.getMaybeGoing() != null && e.getMaybeGoing().contains(u)) {
            //user has been already invited
            Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "--user has been already invited");            
        } else {
            //check user not already answered "yes"
            if (e.getAttendee().contains(u)) {
                //user has already answered -> do nothing
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- user has already answered");
            } else {                
                //OK, create new invitation    
                e.addInvitation(u);
                
                //send email notification
                String email = u.getEmail();
                String subject = "METEOCAL: new invitation";
                String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\nYou have been invited to the event: " + e.getName() + ".\nCheck your invitation request on Meteocal.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                email_mng.sendEmail(email, subject, body);
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- invitation email sent");
            }
        }
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- END invitation ");
        
    }

    /**
     * Creates a new Information, not necessarily related to an event.
     *
     * @return the new information instance.
     */
    public Information newInformation(User u, String message) {
        
        return new Information(u, message);
        
    }

    /**
     * Creates a new Information, related to an event.
     *
     * @param u the receiver.
     * @param message the actual information.
     * @param e the related event.
     * @return the new information instance.
     */
    public Information newInformation(User u, String message, Event e) {
        
        return new Information(e, u, message);
        
    }

    
    /**
     * Remove a previously created invitation.
     *
     * @param u the former-receiver.
     * @param e the former-related-event.
     */
    public void revokeInvitation(User u, Event e) {
        //revoke invitation
        if (e.getMaybeGoing().contains(u)) {
            //revoke invitaiton
            e.getMaybeGoing().remove(u);
            u.getInvitations().remove(e);
            e.getInvitedUserCollection().remove(u);
            u.getEventInvitationCollection().remove(e);
        }
        
    }

    public void revokeParticipation(User u, Event e, Answer a) {
        
        //if user have already accepted,  
        if (e.getAttendee().contains(u)) {
            //remove from the event
            e.getAttendee().remove(u);
            u.getEvents().remove(e);
            e.getAnswerCollection().remove(a);
            //email text parts
            String subject = "METEOCAL: participation revoked";
            String body = "Dear " + u.toString() + ",\nWe are sorry to inform you that your invitation to the event \"" + e.getName() + "\"has been revoked.\n\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            email_mng.sendEmail(u.getEmail(), subject, body);
        }
        //if user declined or he is not in the invited list do nothing.
        
    }

    /**
     * Accept a received invitation.
     *
     * @param u the receiver.
     * @param e the event.
     */
    public void acceptInvitation(User u, Event e) {
        
        if (e.getMaybeGoing().contains(u)) {
            //delete invitation
            e.getInvitedUserCollection().remove(u);
            u.getEventInvitationCollection().remove(e);            
        }
        //if the user have not been invited 
        
    }

    /**
     * Decline a received invitation.
     *
     * @param u
     * @param e
     * @return
     */
    public boolean declineInvitation(User u, Event e) {
        
        boolean out = false;
        
        if (e.getMaybeGoing().contains(u)) {
            e.getInvitedUserCollection().remove(u);
            u.getEventInvitationCollection().remove(e);
            out = true;
        }
        
        return out;
        
    }

    /**
     * Given an event it says if it has adverse weather conditions. If there are
     * adverse weather conditions, it returns false. In any other condition it
     * returns true.
     *
     * @param e the event w/ the weather conditions we want to check.
     * @return false if there are adverse weather conditions -- true otherwise.
     */
    public boolean checkWeather(Event e) {

        return e.getConstraint() == null || canBeDone(OpenWeatherMapController.getValueFromCode(e.getForecast()), e.getConstraint());

    }

    /**
     *
     * @param wc
     * @param constraint
     * @return
     */
    public boolean canBeDone(WeatherCondition wc, Integer constraint) {
        
        boolean out = true;

        switch (constraint) {
            case 1: //Requires clear sky
                break;
            case 2: //Requires no precipitation
                switch (wc) {
                    case DRIZZLE: //P
                    case THUNDERSTORM: //P
                    case SNOW: //P
                    case STORM:
                    case HURRICANE:
                    case TORNADO:
                    case TROPICAL_STORM:
                    case EXTREME_HAIL: //P
                        break;
                    default:
                        out = !wc.isRain();
                }
                break;
            case 3: //Requires snow
                out = wc.equals(WeatherCondition.SNOW);
                break;
            case 4: //No extreme conditions
                out = !wc.isExtreme();
                break;
            default:
        }

        return out;
        
    }

   
    /**
     * Invites the given users to the given event.
     *
     * @param ul list of users that have to receive the invitations.
     * @param event the event the users have to be invited to.
     */
    public void sendInvitations(List<User> ul, Event event) {
        
        for (Iterator<User> it = ul.iterator(); it.hasNext();) {
            User user = it.next();
            newInvitation(user, event);
        }
        
    }

    /**
     * Allows to verify the time consistency of an event given its creator. It
     * returns true if no other event of the given user is in the db after start
     * and before end.
     *
     * @param creator the creator (User).
     * @param start the start datetime of the event (w/ year, month, day, hour
     * and minute).
     * @param end the end datetime of the event (w/ year, month, day, hour and
     * minute).
     * @return true if no events overlaps -- false otherwise.
     */
    public boolean verifyConsistency(User creator, Date start, Date end, int numOverlappingEvents) {
       
        boolean result = false;
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "------ START Verify consistency in EventManager ---------- ");

        //check if end is > then start
        if (start != null && end != null && !end.before(start) && numOverlappingEvents == 0 && start.after(Calendar.getInstance().getTime())) {
            result = true;
        }

        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-------- result: {0}", result);
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "------ END Verify consistency in EventManager ---------- ");
        return result;
        
    }

    /**
     * The user can see the event's details
     *
     * @param event
     * @param user
     * @return
     */
    public boolean showable(Event event, User user) {
        
        boolean out = false;
        
        if (event.isPublicEvent() || event.getRelated().contains(user)) {
            out = true;
        }
        
        return out;
        
    }

    public void sendEmail(String to,String subject,String body) {
        
        email_mng.sendEmail(to, subject, body);
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- invitation email sent");
        
    }

}

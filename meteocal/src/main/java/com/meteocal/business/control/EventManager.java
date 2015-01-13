package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
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

/**
 * FROM - C:EventCreationManager FROM/TO - B: NotificationP TO - E:Event,
 * E:Notification(Invitation & Answer), E:User
 *
 * @author Manuel
 */
public class EventManager {

    //*** use the new method w/ List<User> (don't use the collections***).
    /**
     * Creates a new invitation.
     *
     * @param u the receiver.
     * @param e the involved event.
     */
    public void newInvitation(User u, Event e) {
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- START invitation");
        //check user has not been already invited
        if (e.getMaybeGoing() != null && e.getMaybeGoing().contains(u)) {
            //if(e.getMaybeGoing().contains(u)){
            //user has been already invited
            Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "--user has been already invited");
        } else {
            //check user not already answered
            if (e.getAttendee().contains(u) || e.getDeclined().contains(u)) {
                //user has already answered -> do nothing
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- user has already answered");
            } else {
                //OK, create new invitation
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- save invitation (in collection)");

                //saveInvitation(u, e);
                //e.getInvitedUserCollection().add(u);
                //u.getEventInvitationCollection().add(e);
                e.addInvitation(u);

                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "-- Load email parts");
                //send email notification
                String email = u.getEmail();
                String subject = "METEOCAL: new invitation";
                String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\nYou have been invited to the event: " + e.getName() + ".\nCheck your invitation request on Meteocal.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                EmailManager.getInstance().sendEmail(email, subject, body);
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
            /*
            em.merge(e);
            em.merge(u);
            */
        } else {
            //if user have already accepted,  
            if(e.getAttendee().contains(u)){
                //remove from the event
                e.getAttendee().remove(u);
                u.getEvents().remove(e);
                /*
                em.merge(e);
                em.merge(u);
                //and send him an info
                newInformation(u, "You have been removed from the event: " + e.getName() + ".");
                */
            }
            //if user declined or he is not in the invited list do nothing.
        }
    }

    //TODO added TODOs
    /**
     * Accept a received invitation.
     *
     * @param u the receiver.
     * @param e the event.
     */
    public void acceptInvitation(User u, Event e, int numOverlappingEvents) {
        if (e.getMaybeGoing().contains(u)) {
            //Check overlap 
            if (verifyConsistency(u, e.getStart(), e.getEnd(), numOverlappingEvents)) {
                //no overlap
                //delete invitation
                u.getInvitations().remove(e);
                e.getMaybeGoing().remove(u);
                /*
                
                 //save into the db
                 em.merge(e);
                 em.merge(u);
                
                 //add answer
                 Answer answer = new Answer(e.getEventId(), u.getUserId(), true);
                 em.persist(answer);
                
                 //create info for the creator
                 newInformation(e.getCreator(), u.getUserName() + " is attending the event: " + e.getName() + ".", e);
                 //send email notification for the creator
                 String subject = "METEOCAL: " + e.getName() + ", new attender";
                 String body = "Dear " + e.getCreator().getFirstName() + " " + e.getCreator().getLastName() + ",\n" + u.getUserName() + " is attending the event: " + e.getName() + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                 EmailManager.getInstance().sendEmail(u.getEmail(), subject, body);
                
                 */
            } else {
                //overlap
                declineInvitation(u, e);
                //newInformation(u, "The event: " + e.getName() + " overlaps your other events.", e);
            }
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
            u.getInvitations().remove(e); //TODO (later)
            e.getMaybeGoing().remove(u);
            out = true;
            /*
             em.merge(e);
             em.merge(u);
             Answer answer = new Answer(e.getEventId(), u.getUserId(), false);
             em.persist(answer);
             */
        }
        return out;
    }

    //*any other condition*
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

    //TODO jDocs
    /**
     *
     * @param wc
     * @param constraint
     * @return
     */
    private boolean canBeDone(WeatherCondition wc, Integer constraint) {
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
                        out = wc.isRain();
                }
                break;
            case 3: //Requires snow
                out = wc.equals(WeatherCondition.SNOW);
                break;
            case 4: //No extreme conditions
                out = wc.isExtreme();
                break;
            default:
        }

        return out;
    }

    //for-each (or functional similar) of newInvitation()
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
     * @param event
     * @param user
     * @return 
     */
    public boolean showable(Event event, User user){
        boolean out = false;
        if(event.isPublicEvent() || event.getRelated().contains(user))
            out = true;
        return out;
    }

}

package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.WeatherCondition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * FROM - C:EventCreationManager FROM/TO - B: NotificationP TO - E:Event,
 * E:Notification(Invitation & Answer), E:User
 *
 * @author Manuel
 */
public class EventManager {

    @PersistenceContext(unitName = "meteocal_PU")
    private EntityManager em;
    
    private static EventManager instance = null;

    protected EventManager() {
        // Exists only to defeat instantiation.
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();                
        }
        return instance;
    }

    
    //TODO added TODOs
    /**
     * Creates a new invitation.
     *
     * @param u the receiver.
     * @param e the involved event.
     */
    public void newInvitation(User u, Event e) {
        
        //check user has not been already invited
        if (e.getMaybeGoing() != null && e.getMaybeGoing().contains(u)) { //TODO *** use the new method w/ List<User> (don't use the collections***).
            //if(e.getMaybeGoing().contains(u)){
            //user has been already invited
            Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "user has been already invited");
        } else {
            //check user not already answered
            Answer ans = (Answer) em.createNativeQuery("SELECT a FROM Answer a WHERE a.answerPK.userId = :userId AND a.answerPK.eventId = :eventId")
                    .setParameter("userId", u.getUserId())
                    .setParameter("eventId", e.getEventId())
                    .getSingleResult();
            if (ans != null) {
                //user has already answered -> do nothing
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "user has already answered");
            } else {
                //OK, create new invitation
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "call save invitation");
                saveInvitation(u, e);
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "Load email parts");
                //send email notification
                String subject = "METEOCAL: new invitation";
                String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\nYou have been invited to the event: " + e.getName() + ".\nCheck your invitation request on Meteocal.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                EmailManager.getInstance().sendEmail(u.getEmail(), subject, body);
                //EmailManager.getInstance().sendEmail("francesco.fermi@gmail.com", subject, body);
                Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "END invitation (email )");
            }
        }
    }

    /**
     * Store the invitation into the DB
     *
     * @param u the receiver.
     * @param e the involved event.
     */
    private void saveInvitation(User u, Event e) {
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "enter in save invitation");
        e.getInvitedUserCollection().add(u);
        u.getEventInvitationCollection().add(e);
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "ready to merge");
        em.merge(e);
        em.merge(u);
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "merge done");
    }

    /**
     * Creates a new Information, not necessarily related to an event.
     *
     * @return new information instance.
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
     */
    public void newInformation(User u, String message, Event e) {
        Information info = new Information(e, u, message);
        em.persist(info);
    }

    //TODO added TODOs
    /**
     * Remove a previously created invitation.
     *
     * @param u the former-receiver.
     * @param e the former-related-event.
     */
    public void revokeInvitation(User u, Event e) {
        //revoke invitation
        if (e.getInvitedUserCollection().contains(u)) { //TODO ***
            //revoke invitaiton
            e.getInvitedUserCollection().remove(u);     //TODO (later)
            u.getEventInvitationCollection().remove(e);
            em.merge(e);
            em.merge(u);
        } else {
            //if user have already accepted,  
            Collection<Answer> ans = e.getAnswerCollection(); //TODO ***
            for (Answer next : ans) {
                if (next.getAnswerPK().getUserId() == u.getUserId() && next.getValue()) {
                    //remove from the event
                    e.getAnswerCollection().remove(next); //TODO (later)
                    u.getAnswerCollection().remove(next);
                    em.merge(e);
                    em.merge(u);
                    //and send him an info
                    newInformation(u, "You have been removed from the event: " + e.getName() + ".");
                }
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
    public void acceptInvitation(User u, Event e) {
        if (e.getInvitedUserCollection().contains(u)) { //TODO ***
            //Check overlap 

            if (verifyConsistency(u, e.getStart(), e.getEnd())) {
                //no overlap
                //delete invitation
                u.getEventInvitationCollection().remove(e); //TODO (later)
                e.getInvitedUserCollection().remove(u);
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
            } else {
                //overlap
                declineInvitation(u, e);
                newInformation(u, "The event: " + e.getName() + " overlaps your other events.", e);
            }
        }
        //if the user have not been invited 
    }

    //TODO added TODOs
    /**
     * Decline a received invitation.
     *
     * @param u
     * @param e
     */
    public void declineInvitation(User u, Event e) {
        if (e.getInvitedUserCollection().contains(u)) { //TODO ***
            u.getEventInvitationCollection().remove(e); //TODO (later)
            e.getInvitedUserCollection().remove(u);
            em.merge(e);
            em.merge(u);
            Answer answer = new Answer(e.getEventId(), u.getUserId(), false);
            em.persist(answer);
        }
    }

    //TODO *any other condition*
    /**
     * Given an event it says if it has adverse weather conditions. If there are
     * adverse weather conditions, it returns false. In any other condition it
     * returns true.
     *
     * @param e the event w/ the weather conditions we want to check.
     * @return false if there are adverse weather conditions -- true otherwise.
     */
    public boolean checkWeather(Event e) {

        boolean out = false;

        if (e.getConstraints() == null || canBeDone(OpenWeatherMapController.getValueFromCode(e.getForecast()), e.getConstraints())) {
            out = true;
        }

        return out;

    }


    private boolean canBeDone(WeatherCondition wc, Integer constraint){
        boolean out=true;
        
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
        //richiamato anche quando modifichi...quindi prima check se sono già stati invitati.
        for (User user : ul) {
            newInvitation(user, event);
        }
    }

    //TODO FIX IT
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
    public boolean verifyConsistency(User creator, Date start, Date end) {
        boolean result = false;
        
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "--- START Verify consistency ---");
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- {0}",creator.getUserId());
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- {0}",start.toString());
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- {0}",end.toString());
        //check if end is > then start
        if (!end.before(start)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Query query = em.createNativeQuery(
                    "SELECT COUNT(*) FROM events e JOIN answers a ON e.event_id = a.event_id"
                        + "WHERE ((e.creator = ?) OR ( a.value = 1 AND a.user_id = ?))"
                            + "AND (e.start_date <= '?' AND e.end_date >= '?')"  )                    
                    .setParameter(1, creator.getUserId())
                    .setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start))
                    .setParameter(4, formatter.format(end));
            Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- query res: {0}",query.toString());
            int count = (Integer)query.getSingleResult();
            Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- count: {0}",count);               
            
            if (count == 0) {
                result = true;
            }else{
                //result = start.after(Date.from(Calendar.getInstance().toInstant())) && query == 0;
                result = start.after(Calendar.getInstance().getTime()) && count == 0;
            }
        }
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "- result: {0}",result);
        Logger.getLogger(EventManager.class.getName()).log(Level.INFO, "--- END Verify consistency ---");
        return result;
    }

}

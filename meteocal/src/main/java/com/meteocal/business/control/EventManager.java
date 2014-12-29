package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * FROM - C:EventCreationManager
 * FROM/TO - B: NotificationP
 * TO - E:Event, E:Notification(Invitation & Answer), E:User
 * @author Manuel
 */
public class EventManager {
        
    @PersistenceContext
    EntityManager em;
    
    private static EventManager instance = null;
    protected EventManager() {
       // Exists only to defeat instantiation.
    }
    public static EventManager getInstance() {
       if(instance == null) {
          instance = new EventManager();
       }
       return instance;
    }
    
    //TODO RC
    /**
     * Creates a new invitation.
     * 
     * @param u the receiver.
     * @param e the involved event.
     */
    public void newInvitation(User u, Event e){
        //check user has not been already invited
        if(e.getInvitedUserCollection().contains(u)){
            //user has been already invited
        }else{
            //check user not already answered
            try{
                em.createNativeQuery(
                        "SELECT a FROM Answer a WHERE a.answerPK.userId = :userId AND a.answerPK.eventId = :eventId")
                        .setParameter("userId", u.getUserId())
                        .setParameter("eventId", e.getEventId())
                        .getSingleResult();
                //user has already answered -> do nothing
            }catch(NoResultException ex){
                //OK, create new invitation
                e.getInvitedUserCollection().add(u);
                u.getEventInvitationCollection().add(e);
                em.merge(e);
                em.merge(u);
                //send email notification
                String subject = "METEOCAL: new invitation";
                String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\nYou have been invited to the event: " + e.getName() + ".\nCheck your invitation request on Meteocal.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                EmailManager.getInstance().sendEmail(u.getEmail(), subject, body);
            }
        }
    }
    
    //TODO RC event field in the db is set as null.
    /**
     * Creates a new Information, not necessarily related to an event.
     * 
     * @param u the receiver.
     * @param message the actual information.
     */
    public void newInformation(User u, String message){
        Information info = new Information(u, message);
        em.persist(info);
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
        Information info = new Information(e,u, message);
        em.persist(info);
    }
    
    //TODO RC
    /**
     * Remove a previously created invitation.
     * 
     * @param u the former-receiver.
     * @param e the former-related-event.
     */
    public void revokeInvitation(User u, Event e){
        //revoke invitation
        if(e.getInvitedUserCollection().contains(u)){
            //revoke invitaiton
            e.getInvitedUserCollection().remove(u);
            u.getEventInvitationCollection().remove(e);
            em.merge(e);
            em.merge(u);
        } else { 
            //if user have already accepted,  
            Collection<Answer> ans = e.getAnswerCollection();
            for (Answer next : ans) {
               if(next.getAnswerPK().getUserId()==u.getUserId() && next.getValue()){
                   //remove from the event
                   e.getAnswerCollection().remove(next);
                   u.getAnswerCollection().remove(next);
                   em.merge(e);
                   em.merge(u);
                   //and send him an info
                   newInformation(u, "You have been removed from the event: " + e.getName() + ".", e);
               }
            }
            //if user declined or he is not in the invited list do nothing.
        }   
    }
    
    //TODO RC remember to check if it exists
    /**
     * Accept a received invitation.
     * 
     * @param u the receiver.
     * @param e the event.
     */
    public void acceptInvitation(User u, Event e){
        if(e.getInvitedUserCollection().contains(u)){
            //Check overlap 
            /*
            TypedQuery<Event> query;
            query = (TypedQuery<Event>) em.createNativeQuery(
                    "SELECT e FROM Event e, Answer a"
                            + "WHERE (e.start <= :start AND e.end >= :end) "
                                + "AND ((e.creator = :creator) "
                                + "OR (a.event_id = e.event_id AND a.value = 1 AND :creator = a.user_id))")
                    .setParameter("creator", u.getUserId())
                    .setParameter("start", e.getStart())
                    .setParameter("end", e.getEnd()); 
            
            if(query.getResultList().isEmpty()){
                */
            if(verifyConsistency(u, e.getStart(), e.getEnd())){
                //no overlap
                //delete invitation
                u.getEventInvitationCollection().remove(e);
                e.getInvitedUserCollection().remove(u);
                em.merge(e);
                em.merge(u);
                //add answer
                Answer answer = new Answer(e.getEventId(), u.getUserId(), true);
                em.persist(answer);
                //create info for the creator
                newInformation(e.getCreator(), u.getUserName() + " is attending the event: " + e.getName() + "." , e);
                //send email notification for the creator
                String subject = "METEOCAL: " + e.getName() + ", new attender";
                String body = "Dear " + e.getCreator().getFirstName() + " " + e.getCreator().getLastName() + ",\n" + u.getUserName() +" is attending the event: " + e.getName() + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
                EmailManager.getInstance().sendEmail(u.getEmail(), subject, body);
            } else {
                //overlap
                declineInvitation(u, e);
                newInformation(u, "The event: " + e.getName() + " overlaps your other events.",e);
            }
        }
        //if the user have not been invited 
    }
    
    //TODO RC remember to check if it exists 
    /**
     * Decline a received invitation.
     * 
     * @param u
     * @param e 
     */
    public void declineInvitation(User u, Event e){
        if(e.getInvitedUserCollection().contains(u)){
            u.getEventInvitationCollection().remove(e);
            e.getInvitedUserCollection().remove(u);
            em.merge(e);
            em.merge(u);
            Answer answer = new Answer(e.getEventId(), u.getUserId(), false);
            em.persist(answer);
        }
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
                            + "OR (a.answerPK.event_id = e.event_id AND a.value = 1 AND :creator = a.answerPK.user_id))")
                .setParameter("creator", creator.getUserId())
                .setParameter("start", start)
                .setParameter("end", end);
        
        return start.after(Date.from(Calendar.getInstance().toInstant())) && query.getResultList().isEmpty();
    }
 
}

package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import java.util.Collection;
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
                u.getEventCollection().add(e);
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
            u.getEventCollection().remove(e);
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
                //no overlap
                //delete invitation
                u.getEventCollection().remove(e);
                e.getInvitedUserCollection().remove(u);
                em.merge(e);
                em.merge(u);
                //add answer
                Answer answer = new Answer(e.getEventId(), u.getUserId(), true);
                em.persist(answer);
                //create info 
                newInformation(u, "You are attending the event: " + e.getName() + "." , e);
                //send email notification
                String subject = "METEOCAL: new event in your calendar";
                String body = "Dear " + u.getFirstName() + " " + u.getLastName() + ",\nCOngratulations, you are now attending the event: " + e.getName() + ".\nCheck your calendar on Meteocal.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
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
            u.getEventCollection().remove(e);
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
}

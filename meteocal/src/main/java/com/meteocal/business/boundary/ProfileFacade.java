package com.meteocal.business.boundary;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
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
@Stateless
public class ProfileFacade {
    
    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;
    
    public boolean eventPublic(String eventId){
        return getEvent(eventId).isPublicEvent();
    }
    

    private Event getEvent(String e) {
        try {
            return em.createNamedQuery("Event.findByEventId", Event.class).setParameter("eventId", Integer.parseInt(e)).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
 
    
    public ScheduleModel getEvents(Date from, Date to, String userId){
        
        ScheduleModel eventModel = new DefaultScheduleModel();
        em.refresh(getUser(userId));
        List<Event> list_events = getUser(userId).getEvents(from, to);

        list_events.stream().forEach((ev) -> {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
            } else {               
                if(ev.getRelated().contains(getUser(userId)))
                    eventModel.addEvent(new DefaultScheduleEvent("(P) " + ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
                else
                    eventModel.addEvent(new DefaultScheduleEvent("(P)", ev.getStart(), ev.getEnd(), ev.getEventId()));
            }
        });

        return eventModel;
    }
    
        
    public String isCalendarVisible(String userId) {
        String out = "false";
        
        if (getUser(userId).isPublicCalendar())
            out = "true";
            
            
        return out;
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
   
}

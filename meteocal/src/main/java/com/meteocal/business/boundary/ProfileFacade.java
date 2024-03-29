package com.meteocal.business.boundary;

import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.jboss.logging.Logger;
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

    @Inject
    LogInManager lm;

    /**
     *
     * @param eventId
     * @return
     */
    public boolean eventPublic(String eventId) {

        return getEvent(eventId).isPublicEvent();

    }

    /**
     *
     * @param e
     * @return
     */
    private Event getEvent(String e) {

        try {
            return em.createNamedQuery("Event.findByEventId", Event.class).setParameter("eventId", Integer.parseInt(e)).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    /**
     *
     * @param from
     * @param to
     * @param userId
     * @return
     */
    public ScheduleModel getEvents(Date from, Date to, Integer userId) {

        ScheduleModel eventModel = new DefaultScheduleModel();
        em.refresh(getUserFromId(userId));
        List<Event> list_events = getUserFromId(userId).getEvents(from, to);

        list_events.stream().forEach((ev) -> {
            if (ev.isPublicEvent()) {
                eventModel.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
            } else {
                if (ev.getRelated().contains(getUserFromId(userId))) {
                    eventModel.addEvent(new DefaultScheduleEvent("(P) " + ev.getName(), ev.getStart(), ev.getEnd(), ev.getEventId()));
                } else {
                    eventModel.addEvent(new DefaultScheduleEvent("(P)", ev.getStart(), ev.getEnd(), ev.getEventId()));
                }
            }
        });

        return eventModel;
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean isCalendarVisible(Integer userId) {

        return getUserFromId(userId).isPublicCalendar();

    }

    /**
     *
     * @param username the username of the user you are looking for
     * @return the User you are looking for
     */
    private User getUserFromId(Integer id) {

        try {
            Logger.getLogger(ProfileFacade.class.getName(), "Called getUserFromId with value: " + id);
            return em.createNamedQuery("User.findByUserId", User.class).setParameter("userId", id).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    /**
     *
     * @param userId
     * @return
     */
    public String getUserNames(Integer userId) {

        return getUserFromId(userId).toString();

    }

    /**
     *
     * @param loadedUser
     * @return
     */
    public String getPrivacy(Integer loadedUser) {

        String out = "Private calendar";

        if (getUserFromId(loadedUser).isPublicCalendar()) {
            out = "Public calendar";
        }

        return out;

    }

    /**
     *
     * @param id
     * @return
     */
    public boolean hasRelated(Integer id) {

        return getEvent(id.toString()).getRelated().contains(getUser(getUserId(getLoggedUser())));

    }

    /**
     *
     * @param username
     * @return
     */
    private String getUserId(String username) {

        return getUser(username).getUserId().toString();

    }

    /**
     *
     * @return the username of the current user logged in.
     */
    private String getLoggedUser() {

        return lm.getLoggedUserName();

    }

    /**
     *
     * @param username
     * @return
     */
    private User getUser(String username) {

        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

}

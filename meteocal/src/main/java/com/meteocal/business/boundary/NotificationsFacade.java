/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.Answer;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manuel
 */
@Stateful
public class NotificationsFacade {

    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;
    @Inject
    EventManager ev_m;
    @Inject
    LogInManager lm;

    private List<Information> getInformations() {
        User user = getUser(lm.getLoggedUserName());
        em.refresh(user);
        return user.getInformations();
    }

    private List<Event> getInvitations() {
        User user = getUser(lm.getLoggedUserName());
        em.refresh(user);
        return user.getInvitations();
    }

    public List<ArrayList<String>> getCompleteList() {
        Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "?-- START getCompleteList() --");
        ArrayList<String> row = new ArrayList<>();//{"type", "from", "text", "event_id", "disabled"};
        List<ArrayList<String>> list = new ArrayList<>();
        for (Iterator<Information> it = getInformations().iterator(); it.hasNext();) {
            row = new ArrayList<>();
            Information info = it.next();
            //type
            row.add("information");
            //from
            if (info.getEventId() == null) {
                row.add("MeteoCal Service");

            } else {
                //Dall evento
                row.add("Event: " + info.getEventId().getName());

            }
            //text
            row.add(info.getText());
            //info_id
            row.add(info.getInformationId().toString());
            //disable accept button
            row.add("false");

            //add element
            list.add(row);
            Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "|-- information added:{0}", row.toString());
        }

        for (Iterator<Event> it = getInvitations().iterator(); it.hasNext();) {
            //row = {"type", "from", "text", "event_id", "disabled"};
            row = new ArrayList<>();
            Event e = it.next();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            row.add("invitation");
            row.add("[Event] " + e.getName());
            row.add("You have been invited to the event. \tStart: " + formatter.format(e.getStart()) + "\tEnd: " + formatter.format(e.getEnd()));
            row.add(e.getEventId().toString());
            row.add(String.valueOf(!canAccept(e)));
            list.add(row);
            Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "---| invitation added: {0}", row.toString());
        }
        Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "?-- END getComleteList() --");
        return list;
    }

    private boolean canAccept(Event e) {
        boolean out = false;
        if (getNumOverlappingEvents(getUser(lm.getLoggedUserName()), e.getStart(), e.getEnd()) == 0) {
            out = true;
        }
        return out;
    }

    private int getNumOverlappingEvents(User creator, Date start, Date end) {
        em.flush();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        try {
            String query = "SELECT COUNT(e.event_id) FROM events e LEFT JOIN answers a ON e.event_id = a.event_id "
                    + "WHERE (((e.creator = ?) OR ( a.answer_value = 1 AND a.user_id = ?)) "
                    + "AND("
                    + "(e.start_date <= ? AND e.end_date >= ? ) OR"
                    + "(e.start_date >= ? AND e.end_date >= ? AND e.start_date < ? ) OR"
                    + "(e.start_date <= ? AND e.end_date <= ? AND e.end_date > ? ) OR"
                    + "(e.start_date > ? AND e.end_date < ? ) "
                    + ")"
                    + ")";
            Long l = (Long) em.createNativeQuery(query)
                    .setParameter(1, creator.getUserId())
                    .setParameter(2, creator.getUserId())
                    .setParameter(3, formatter.format(start))
                    .setParameter(4, formatter.format(end))
                    .setParameter(5, formatter.format(start))
                    .setParameter(6, formatter.format(end))
                    .setParameter(7, formatter.format(end))
                    .setParameter(8, formatter.format(start))
                    .setParameter(9, formatter.format(end))
                    .setParameter(10, formatter.format(start))
                    .setParameter(11, formatter.format(start))
                    .setParameter(12, formatter.format(end))
                    .getSingleResult();
            count = l.intValue();
        } catch (NoResultException ex) {

        }
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "|-->-- num overlapping events = {0}", count);
        return count;
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

    public void readInformation(int infoId) {
        Information info = em.find(Information.class, infoId);
        if (info != null) {
            em.remove(info);
        }
        info = null;
        em.flush();
        Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "-- information read");
    }

    public void acceptInvitation(int eventId) {
        Event event = em.find(Event.class, eventId);
        User user = getUser(lm.getLoggedUserName());
        if (event != null) {
            ev_m.acceptInvitation(user, event);

            //save into the db
            em.merge(event);
            em.merge(user);

            //add answer
            Answer answer = new Answer(event.getEventId(), user.getUserId(), true);
            em.persist(answer);
            /*
            //send email notification for the creator
            String subject = "METEOCAL: " + event.getName() + ", new attender";
            String body = "Dear " + event.getCreator().getFirstName() + " " + event.getCreator().getLastName() + ",\n" + user.getUserName() + " is attending the event: " + event.getName() + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            EmailManager.getInstance().sendEmail(event.getCreator().getEmail(), subject, body);
            */
            //new information for the creator
            Information info = ev_m.newInformation(event.getCreator(), user.toString() + " is attending your event: " + event.getName(), event);
            em.merge(info);
            em.flush();
            Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "-- invitation accepted.");
        }

    }

    public void declineInvitation(int eventId) {
        Event event = em.find(Event.class, eventId);
        User user = getUser(lm.getLoggedUserName());
        if (event != null) {
            ev_m.declineInvitation(user, event);
            em.merge(event);
            em.merge(user);
            Answer answer = new Answer(event.getEventId(), user.getUserId(), false);
            em.persist(answer);
            em.flush();
        }
        Logger.getLogger(NotificationsFacade.class.getName()).log(Level.INFO, "-- invitation declined.");
    }
}

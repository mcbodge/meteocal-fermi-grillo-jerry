/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
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
        return getUser(lm.getLoggedUserName()).getInformations();
    }

    private List<Event> getInvitations() {
        return getUser(lm.getLoggedUserName()).getInvitations();
    }

    public List<String[]> getCompleteList() {
        String[] row = null; //string{type, from, text, event_id, disabled}
        List<String[]> list = null;

        for (Iterator<Information> it = getInformations().iterator(); it.hasNext();) {
            Information info = it.next();
            row[0] = "information";
            if (info.getEventId() == null) {
                row[1] = "MeteoCal Service";
                row[3] = "";
            } else {
                //Dall evento
                row[1] = "Event: " + info.getEventId().getName();
                row[3] = info.getEventId().getEventId().toString();
            }
            row[2] = info.getText();
            row[4] = "true";
            list.add(row);
        }

        for (Iterator<Event> it = getInvitations().iterator(); it.hasNext();) {
            Event e = it.next();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            row[0] = "invitation";
            row[1] = "Event: " + e.getName();
            row[2] = "You have been invited to the event. \nStart: "+ formatter.format(e.getStart())+ " End: "+ formatter.format(e.getEnd()) ;
            row[3] = e.getEventId().toString();
            row[4] = String.valueOf(!canAccept(e));
            list.add(row);
        }

        return list;
    }

    private boolean canAccept(Event e){
        User u = getUser(lm.getLoggedUserName());
        return true;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
/**
 *
 * @author Manuel
 */
@Stateful
public class PersonalFacade {

    @PersistenceContext
    EntityManager em;
         
    @Inject
    LogInManager lm;

    @Inject
    EventCreationManager eventCreationManager;

    public String getLoggedUser() {
        return lm.getLoggedUserName();        
    }

    public boolean createEvent(String name, String location, Date dateTime, double duration, String invited, boolean event_private, String constraint, String description) {

        //check constraint
        Integer constr = null;
        if (!constraint.equals("")) {
            constr = Integer.parseInt(constraint);
        }

        //check datetime
        if (dateTime == null) {
            return false;
        }
        
        Date end_date = getDateEnd(dateTime, duration);

        // invited users list
        List<User> list_invited = new ArrayList<User>();
        if (invited != null) {
            StringTokenizer people = new StringTokenizer(invited, ",");
            User u = null;
            while (people.hasMoreElements()) {
                u = getUser(people.nextToken());
                if (u != null) {
                    list_invited.add(u);
                }
            }
        } else {
            list_invited = null;
        }

        return eventCreationManager.newEvent(getUser(getLoggedUser()), name, dateTime, end_date, location, list_invited, event_private, constr, description);

    }

    private User getUser(String username) {
        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private Date getDateEnd(Date dateStart, double duration) {
        //get hours and minutes to add
        int hours = new Double(duration).intValue();
        int minutes = 0;
        if (duration - hours > 0) {
            minutes = 30;
        }

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(dateStart); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, hours); // adds hours
        cal.add(Calendar.MINUTE, minutes); // adds minutes

        return cal.getTime(); // returns new date object, in the future
    }

}

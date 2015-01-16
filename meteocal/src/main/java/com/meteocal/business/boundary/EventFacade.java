/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.control.OpenWeatherMapController;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import com.meteocal.business.entity.WeatherCondition;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manuel
 */
@Stateless
public class EventFacade {

    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;

    @Inject
    LogInManager lm;

    @Inject
    EventManager man;

    @Inject
    EventCreationManager ev_cm;

    /**
     *
     * @return the entity of the current user
     */
    private User getUser() {
        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", lm.getLoggedUserName()).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private Event getEvent(String e) {
        try {
            return em.createNamedQuery("Event.findByEventId", Event.class).setParameter("eventId", Integer.parseInt(e)).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public EventFacade() {
    }

    public String getName(String eventId) {

        Event e = getEvent(eventId);
        String out = "Private event";

        if (man.showable(e, getUser())) {
            out = e.getName();
        }

        return out;

    }

    public String getPrivacy(String eventId) {

        String out = "";

        if (man.showable(getEvent(eventId), getUser())) {
            out = "Public event";
        }

        return out;

    }

    public boolean eventPrivate(String eventId) {
        return !getEvent(eventId).isPublicEvent();
    }

    public String getPicture(String eventId) {

        String out = "url";

        if (man.showable(getEvent(eventId), getUser())) {
            out = "lock";
        }

        return out;

    }

    public String getConstraint(String eventId) {

        Event e = getEvent(eventId);
        String out = "No weather constraint";

        if (e.getConstraint() != null && man.showable(e, getUser())) {
            switch (e.getConstraint()) {
                case 1: //Requires clear sky
                    out = "Requires clear sky";
                    break;
                case 2: //Requires no precipitation
                    out = "Requires no precipitation";
                    break;
                case 3: //Requires snow
                    out = "Requires snow";
                    break;
                case 4: //No extreme conditions
                    out = "No extreme conditions";
                    break;
                default:
            }
        }

        return out;

    }

    public Integer getConstraintBack(String eventId) {
        return getEvent(eventId).getConstraint();
    }

    public String getForecast(String eventId) {

        Weather w = em.find(Weather.class, Integer.parseInt(eventId));
        Integer code = null;
        if (w != null) {
            code = w.getForecast();
        }
        WeatherCondition wc = OpenWeatherMapController.getValueFromCode(code);
        return wc.toString();
    }

    public String getLocation(String eventId) {

        String out = "hidden";
        Event e = getEvent(eventId);

        if (man.showable(e, getUser())) {
            out = e.getLocation();
            if (out == null || out.isEmpty()) {
                out = "none";
            }
        }

        return out;

    }

    public String getAttendees(String eventId) {

        String out = "hidden";
        Event e = getEvent(eventId);

        if (man.showable(e, getUser())) {
            out = e.getCreator().toString() + " [creator] ";
            String temp = e.getAttendee().toString();
            if (temp.length() > 2) {
                out = out + ", " + temp.substring(1, temp.length() - 2);
            }
        }

        return out;

    }

    public String getMaybe(String eventId) {

        String out = "hidden";
        Event e = getEvent(eventId);

        if (man.showable(e, getUser())) {
            out = "";
            String temp = e.getMaybeGoing().toString();
            if (temp.length() > 2) {
                out = out + temp.substring(1, temp.length() - 1);
            }
        }

        return out;

    }

    public String getNotGoing(String eventId) {

        String out = "hidden";
        Event e = getEvent(eventId);

        if (man.showable(e, getUser())) {
            out = "";
            String temp = e.getDeclined().toString();
            if (temp.length() > 2) {
                out = out + temp.substring(1, temp.length() - 2);
            }
        }

        return out;

    }

    public String getDate(String eventId) {

        Event e = getEvent(eventId);

        DateFormat df = new SimpleDateFormat("EEE, yyyy/MM/dd, HH:mm");

        return df.format(e.getStart()) + " - " + df.format(e.getEnd());

    }

    public Date getStart(String eventId) {

        return getEvent(eventId).getStart();

    }

    public String getDescription(String eventId) {

        String out = "hidden";
        Event e = getEvent(eventId);

        if (man.showable(e, getUser())) {
            out = e.getDescription();
            if (out == null || out.isEmpty()) {
                out = "none";
            }
        }

        return out;

    }

    public Boolean canAccept(String eventId) {

        return getEvent(eventId).getMaybeGoing().contains(getUser());

    }

    public Boolean canDecline(String eventId) {

        Event e = getEvent(eventId);

        return e.getMaybeGoing().contains(getUser()) && e.getAttendee().contains(getUser());

    }

    public Boolean isCreator(String eventId) {

        return getEvent(eventId).getCreator().equals(getUser());

    }

    public Boolean isObserver(String eventId) {

        return getEvent(eventId).getRelated().contains(getUser());

    }

    public String getPeople(String eventId) {

        Event e = getEvent(eventId);

        String people = "";

        for (User u : e.getAttendee()) {
            people = people + ", " + u.getUserName();
        }

        for (User u : e.getMaybeGoing()) {
            people = people + ", " + u.getUserName();
        }

        if (!people.isEmpty()) {
            people = people.substring(2);
        }

        return people;

    }

    public Integer getGeoname(String eventId) {
        try {
            return Integer.parseInt(getEvent(eventId).getWeather().getLocationCode());
        } catch (NullPointerException e) {
            return null;
        }

    }

    public double getDuration(String eventId) {
        Event e = getEvent(eventId);

        return ((double) e.getEnd().getTime() - (double) e.getStart().getTime()) / 3600000;
    }

    public void deleteEvent(int eventId) {
        Event event = em.find(Event.class, eventId);
        event.getAttendee().stream().map((u) -> man.newInformation(u, event.getCreator().toString() + " has just deleted the event: \"" + event.getName() + "\"")).map((info) -> {
            em.merge(info);
            return info;
        }).forEach((_item) -> {
            em.flush();
        });

        //delete event;
        em.remove(event);
        em.flush();

    }

}

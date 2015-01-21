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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

    @EJB
    NotificationsFacade nf;

    public EventFacade() {

    }

    /**
     *
     * @return the current user (entity) or null if it is not in the DB
     */
    private User getUser() {

        try {

            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", lm.getLoggedUserName()).getSingleResult();

        } catch (NoResultException ex) {

            return null;

        }

    }

    /**
     *
     * @param e eventId
     * @return the event (entity) or null if it is not in the DB
     */
    public Event getEvent(String e) {

        try {

            return em.createNamedQuery("Event.findByEventId", Event.class).setParameter("eventId", Integer.parseInt(e)).getSingleResult();

        } catch (NoResultException ex) {

            return null;

        }

    }

    /**
     *
     * @param eventId
     * @return the name of the event if it is not private, "Private event"
     * otherwise.
     */
    public String getName(String eventId) {

        Event e = getEvent(eventId);
        String out = "Private event";

        if (man.showable(e, getUser())) {
            out = e.getName();
        }

        return out;

    }

    /**
     *
     * @param eventId
     * @return "Public event" if the user can see the event's details
     */
    public String getPrivacy(String eventId) {

        String out = "";

        if (man.showable(getEvent(eventId), getUser())) {
            out = "Public event";
        }

        return out;

    }

    /**
     *
     * @param eventId
     * @return
     */
    public boolean eventPrivate(String eventId) {

        return !getEvent(eventId).isPublicEvent();

    }

    /**
     *
     * @param eventId
     * @return
     */
    public String getPicture(String eventId) {

        String out = "url";

        if (man.showable(getEvent(eventId), getUser())) {
            out = "lock";
        }

        return out;

    }

    /**
     *
     * @param eventId
     * @return
     */
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

    /**
     *
     * @param eventId
     * @return
     */
    public Integer getConstraintBack(String eventId) {

        return getEvent(eventId).getConstraint();

    }

    /**
     *
     * @param eventId
     * @return
     */
    public String getForecast(String eventId) {

        Weather w = em.find(Weather.class, Integer.parseInt(eventId));

        if (w != null) {
            Logger.getLogger(EventFacade.class.getName()).log(Level.INFO, "Weather: {0}", w.toString());
        }

        Integer code = null;

        if (w != null && w.getForecast() != null) {
            code = w.getForecast();
        }

        WeatherCondition wc = OpenWeatherMapController.getValueFromCode(code);
        return wc.toString();

    }

    /**
     *
     * @param eventId
     * @return
     */
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

    /**
     *
     * @param eventId
     * @return
     */
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

    /**
     *
     * @param eventId
     * @return
     */
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

    /**
     *
     * @param eventId
     * @return
     */
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

    /**
     *
     * @param eventId
     * @return
     */
    public String getDate(String eventId) {

        Event e = getEvent(eventId);
        DateFormat df = new SimpleDateFormat("EEE, yyyy/MM/dd, HH:mm");
        return df.format(e.getStart()) + " - " + df.format(e.getEnd());

    }

    /**
     *
     * @param eventId
     * @return
     */
    public Date getStart(String eventId) {

        return getEvent(eventId).getStart();

    }

    /**
     *
     * @param eventId
     * @return
     */
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
    
    /**
     * 
     * @param eventId
     * @return 
     */
    public Boolean canAccept(String eventId) {

        return getEvent(eventId).getMaybeGoing().contains(getUser());

    }

    /**
     * 
     * @param eventId
     * @return 
     */
    public Boolean canDecline(String eventId) {

        Event e = getEvent(eventId);
        return e.getMaybeGoing().contains(getUser()) || e.getAttendee().contains(getUser());

    }
    
    /**
     * 
     * @param eventId
     * @return 
     */
    public Boolean isCreator(String eventId) {

        return getEvent(eventId).getCreator().equals(getUser());

    }
    
    /**
     * 
     * @param eventId
     * @return 
     */
    public Boolean isObserver(String eventId) {

        return getEvent(eventId).getRelated().contains(getUser());

    }

    /**
     * 
     * @param eventId
     * @return 
     */
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
    
    /**
     * 
     * @param eventId
     * @return 
     */
    public Integer getGeoname(String eventId) {
        
        try {
            return Integer.parseInt(getEvent(eventId).getWeather().getLocationCode());
        } catch (NullPointerException e) {
            return null;
        }

    }
    
    /**
     * 
     * @param eventId
     * @return 
     */
    public double getDuration(String eventId) {
        
        Event e = getEvent(eventId);
        return ((double) e.getEnd().getTime() - (double) e.getStart().getTime()) / 3600000;
        
    }
    
    /**
     * 
     * @param eventId 
     */
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
    
    public String loadWeatherImage(String eventId){
        Integer code = getEvent(eventId).getWeather().getForecast();
        String out;
        
        if (code == null) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/221/backdrop_cloud_weather.png";
        } else if (199 < code && code < 233) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/17_cloud_weather.png";
        } else if (299 < code && code < 322) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/11_cloud_weather.png";
        } else if (499 < code && code < 505) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/12_cloud_weather.png";
        } else if (519 < code && code < 532) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/12_cloud_weather.png";
        } else if (599 < code && code < 623) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/41_cloud_weather.png";
        } else if (700 < code && code < 781) {
            out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/21_cloud_weather.png";
        } else {
            switch (code) {
                case 511:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/40_cloud_weather.png";
                    break;
                case 800:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/32_cloud_weather.png";
                    break;
                case 801:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/26_cloud_weather.png";
                    break;
                case 802:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/26_cloud_weather.png";
                    break;
                case 803:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/44_cloud_weather.png";
                    break;
                case 804:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/28_cloud_weather.png";
                    break;
                case 951:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/32_cloud_weather.png";
                    break;
                case 952:
                case 953:
                case 954:
                case 955:
                case 956:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/24_cloud_weather.png";
                    break;
                case 957:
                case 958:
                case 959:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/24_cloud_weather.png";
                    break;
                case 960:
                case 961:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/37_cloud_weather.png";
                    break;
                case 962:
                case 902:
                    out = "https://openclipart.org/image/256px/svg_to_png/170677/stormy.png";
                    break;
                case 900:
                case 781:
                    out = "https://openclipart.org/image/256px/svg_to_png/170677/stormy.png";
                    break;
                case 901:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/00_cloud_weather.png";
                    break;
                case 903:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/25_cloud_weather.png";
                    break;
                case 904:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/36_cloud_weather.png";
                    break;
                case 905:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/23_cloud_weather.png";
                    break;
                case 906:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/256/06_cloud_weather.png";
                    break;
                default:
                    out = "https://cdn0.iconfinder.com/data/icons/sketchy-weather-icons-by-azuresol/221/backdrop_cloud_weather.png";
                    break;
            }
        }
        return out;
    }

}

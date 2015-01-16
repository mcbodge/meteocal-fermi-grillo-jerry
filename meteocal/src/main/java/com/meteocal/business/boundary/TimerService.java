package com.meteocal.business.boundary;

import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.OpenWeatherMapController;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Information;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import com.meteocal.business.entity.WeatherCondition;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONException;

/**
 *
 * @author Francesco
 */
@Singleton
public class TimerService {

    @Inject
    OpenWeatherMapController owmc;
    @Inject
    EventManager ev_m;

    @PersistenceContext
    EntityManager em;

    /**
     * Update weather information associated to events periodically (every 12
     * hours), and, of course, notify outdoor event participants in case the
     * forecast has changed.
     *
     */
    @Schedule(second = "*", minute = "*", hour = "*/12", persistent = false)
    public void updateAllForecasts() {
        System.out.println("- updateAllForecasts timer: " + new Date().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm ");
        List<Weather> weather_list = em.createNativeQuery("SELECT * FROM weather ORDER BY weather.last_update", Weather.class).getResultList();
        if (weather_list != null) {
            for (Iterator<Weather> it = weather_list.iterator(); it.hasNext();) {
                Weather w = it.next();
                int geoname = Integer.parseInt(w.getLocationCode());
                try {
                    int updatedForecast = owmc.getForecast(geoname,2);
                    System.out.println("----- Read: " + updatedForecast);
                    if (w.getForecast() != null && !w.getForecast().equals(updatedForecast)) {
                        //new information for all attendee
                        for (User u : w.getEvent().getAttendee()) {
                            Information info = ev_m.newInformation(u, "The forecast has been changed on " + formatter.format(new Date()) + "." + w.getEvent().getName(), w.getEvent());
                            em.merge(info);
                            em.flush();
                        }
                    }
                    //update forecast and store.
                    w.setForecast(updatedForecast);
                    w.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                    em.merge(w);
                    em.flush();
                } catch (JSONException ex) {
                    Logger.getLogger(TimerService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * In case of bad weather conditions for outdoor events, - three days before
     * the event, the system should propose to its creator the closest (in time)
     * sunny day (if any). - one day before the event, the system should notify
     * all event participants (in case of bad weather conditions for outdoor
     * events).
     *
     */
    @Schedule(second = "*", minute = "*/30", hour = "*", persistent = false)
    public void checkConstraintViolations() {
        final int CONSTR_VIOLATION_INTERVAL = 30; //minutes
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm ");
        System.out.println("- checkConstraintViolations timer: " + new Date().toString());
        
        //bound for the next 3 days
        Calendar min3days = Calendar.getInstance();
        min3days.setTime(new Date());
        min3days.add(Calendar.DAY_OF_YEAR, 3);
        min3days.add(Calendar.MINUTE, -(CONSTR_VIOLATION_INTERVAL / 2));
        Calendar max3days = Calendar.getInstance();
        max3days.setTime(min3days.getTime());
        max3days.add(Calendar.MINUTE, CONSTR_VIOLATION_INTERVAL);
        //bound for the next day
        Calendar min1day = Calendar.getInstance();
        min1day.setTime(new Date());
        min1day.add(Calendar.DAY_OF_YEAR, 3);
        min1day.add(Calendar.MINUTE, -(CONSTR_VIOLATION_INTERVAL / 2));
        Calendar max1day = Calendar.getInstance();
        max1day.setTime(min1day.getTime());
        max1day.add(Calendar.MINUTE, CONSTR_VIOLATION_INTERVAL);

        //prepare query
        String query = "SELECT e.* FROM events e JOIN weather w ON e.event_id = w.event_id "
                + "WHERE (e.start_date > ? AND e.start_date <= ?) OR"
                + "(e.start_date > ? AND e.start_date <= ?)";
        List<Event> events_list = em.createNativeQuery(query)
                .setParameter(1, min3days.getTime()).setParameter(2, max3days.getTime())
                .setParameter(3, min1day.getTime()).setParameter(4, max1day.getTime())
                .getResultList();

        for (Event event : events_list) {
            Weather w = em.find(Weather.class, event.getEventId());
            WeatherCondition wc = OpenWeatherMapController.getValueFromCode(w.getForecast());
            Event e = w.getEvent();

            //check the constraint
            if (!ev_m.checkWeather(e)) {
                if (e.getStart().after(min1day.getTime())) //send info
                {
                    for (User u : e.getAttendee()) {
                        //new information for all attendee
                        Information info = ev_m.newInformation(u, "The forecast has been changed on " + formatter.format(new Date()) + "." + e.getName(), e);
                        em.merge(info);
                        em.flush();
                    }
                    //new information for the creator
                    Information info = ev_m.newInformation(e.getCreator(), "The forecast has been changed on " + formatter.format(new Date()) + "." + e.getName(), e);
                    em.merge(info);
                    em.flush();
                }
                //send info + email to creator
                Information info = ev_m.newInformation(e.getCreator(), "The forecast has been changed on " + formatter.format(new Date()) + ".\nPlease reschedule your Event." + e.getName(), e);
                em.merge(info);
                em.flush();
                ev_m.sendEmail(e.getCreator().getEmail(), "MeteoCal: bad weather conditions", "The event \"" + e.getName() + " has bad weather conditions.\nPlease reschedule your Event.");
            }
        }
    }

}

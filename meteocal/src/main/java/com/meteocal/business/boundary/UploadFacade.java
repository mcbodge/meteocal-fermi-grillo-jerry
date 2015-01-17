/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.control.OpenWeatherMapController;
import com.meteocal.business.control.UserCalendarManager;
import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.Location;
import com.meteocal.business.entity.User;
import com.meteocal.business.entity.Weather;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONException;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Manuel
 */
@Stateless
public class UploadFacade {

    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;

    @Inject
    UserCalendarManager ucm;

    @Inject
    EventCreationManager ev_cm;

    @Inject
    LogInManager lim;
    
    @Inject
    OpenWeatherMapController owmc;

    /**
     * Import a calendar
     *
     * @param f the uploaded file
     */
    private void setCalendar(File f) {
        //ucm.startUpload(getUser(lim.getLoggedUserName()), f);
        if (ucm.verifyFile(f)) {
            extractFromFile(f, getUser(lim.getLoggedUserName()));
        }
    }

    /**
     * Returns the current User
     *
     * @param username username, saved in the current session
     * @return the user entity
     */
    private User getUser(String username) {
        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Upload a new file (calendar)
     *
     * @param up the UploadedFile in the cache
     */
    public void upload(UploadedFile up) {
        // Do what you want with the file
        try {
            copyFile(Integer.toString(up.hashCode()), up.getInputstream());
        } catch (IOException e) {
        }

    }

    /**
     * Transfer the file in the files/ directory, in memory.
     *
     * @param hash name of the file to be saved
     * @param in initialized input stream
     */
    public void copyFile(String hash, InputStream in) {
        try {
            File file = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("files/") + File.separatorChar + hash);

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(file);

            int read;// = 0;
            byte[] bytes = new byte[4096];

            while ((read = in.read(bytes)) >= 0) {
                out.write(bytes, 0, read);
            }

            in.close();
            out.flush();
            setCalendar(file);

        } catch (IOException e) {

        }
    }

    /**
     * It extracts the interesting events from the calendar: If the user who is
     * importing the calendar is the same who has exported it: - Import
     * non-overlapped personal events (without invited people) adding to the
     * event names the string " [Imported]". If the user who is importing the
     * calendar is not the same who has exported it: - Import non-overlapped
     * personal events (without invited people) adding to the event names the
     * string " ["++NAME_OF_THE_FORMER_CREATOR++"]"; - Modify the creator as the
     * currently importing user.
     *
     * @param f the previously imported and verified file.
     * @param u the user who is importing the file.
     */
    private void extractFromFile(File f, User u) {

        InputStream is;
        BufferedReader reader;
        String line;
        String user_file, e_id, e_name, e_str_location, e_str_geoname, e_str_start, e_str_end, e_descr, e_str_constraint;
        StringTokenizer token;

        try {
            //open stream
            is = new FileInputStream(f);
            reader = new BufferedReader(new InputStreamReader(is));

            //first line = user_id
            user_file = reader.readLine();

            while ((line = reader.readLine()) != null) {
                //line = eventid   name    location    start   end   descr   constraint
                token = new StringTokenizer(line, "\t");

                e_id = token.nextToken();
                e_name = token.nextToken();
                e_str_location = token.nextToken();
                e_str_geoname = token.nextToken();
                e_str_start = token.nextToken();
                e_str_end = token.nextToken();
                e_descr = token.nextToken();
                e_str_constraint = token.nextToken();

                //<editor-fold defaultstate="collapsed" desc="JDoc Date.toString()">
                /**
                 * Converts this Date object to a String of the form: dow mon dd
                 * hh:mm:ss zzz yyyy where: dow is the day of the week (Sun,
                 * Mon, Tue, Wed, Thu, Fri, Sat). mon is the month (Jan, Feb,
                 * Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec). dd is the
                 * day of the month (01 through 31), as two decimal digits. hh
                 * is the hour of the day (00 through 23), as two decimal
                 * digits. mm is the minute within the hour (00 through 59), as
                 * two decimal digits. ss is the second within the minute (00
                 * through 61, as two decimal digits. zzz is the time zone (and
                 * may reflect daylight saving time). Standard time zone
                 * abbreviations include those recognized by the method parse.
                 * If time zone information is not available, then zzz is empty
                 * - that is, it consists of no characters at all. yyyy is the
                 * year, as four decimal digits.
                 *
                 */
                //</editor-fold> 
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                Date e_start = formatter.parse(e_str_start);
                Date e_end = formatter.parse(e_str_end);
                Integer e_geoname = null;
                if(e_str_geoname != null && !e_str_geoname.equals("") && !e_str_geoname.equals("null")){
                    e_geoname = Integer.parseInt(e_str_geoname);
                }
                Integer e_constraint = null;
                if(e_str_constraint != null && !e_str_constraint.equals("") && !e_str_constraint.equals("null")){
                    e_constraint = Integer.parseInt(e_str_constraint);
                }
                
                if (user_file.equals(u.getUserName())) {
                    e_name = e_name + " [Imported]";
                } else {
                    e_name = e_name + " [" + user_file + "]";
                }

                //create event
                Event event = ev_cm.newEvent(u, e_name, e_start, e_end, e_str_location, null, true, e_constraint, e_descr, getNumOverlappingEvents(u, e_start, e_end));

                //save event if it have been created     
                if (event != null) {
                    //save in db
                    em.flush();
                    event = em.merge(event);
                    em.flush();

                    if (e_geoname == null) {
                        //no weather condition is given
                    } else {
                        //create weather constraint and bind it to the event
                        Weather weather;
                        weather = new Weather(event.getEventId(), e_geoname);

                        if (e_constraint != null) {
                            weather.setConstraint(e_constraint);
                        }else{
                            weather.setConstraint(0);
                        }

                        //calc desired forecast day
                        Calendar cal_today = Calendar.getInstance();
                        cal_today.setTime(new Date());
                        Calendar cal_day = Calendar.getInstance();
                        cal_day.setTime(event.getStart());
                        int day = cal_day.get(Calendar.DAY_OF_YEAR) - cal_today.get(Calendar.DAY_OF_YEAR) + 1;
                        if (day < 17) {
                            try {
                                weather.setForecast(owmc.getForecast(e_geoname, day));
                                weather.setLastUpdate(new Timestamp(System.currentTimeMillis()));
                            } catch (JSONException ex) {
                                Logger.getLogger(PersonalFacade.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        em.persist(weather);
                        em.flush();
                    }

                }
            }
            //close stream
            reader.close();
            is.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        Logger.getLogger(PersonalFacade.class.getName()).log(Level.INFO, "-- num overlapping events = {0}", count);
        return count;
    }

}

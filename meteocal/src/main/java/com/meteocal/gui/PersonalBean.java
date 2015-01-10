/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import boundaries.EventBean;
import com.meteocal.business.boundary.HomeFacade;
import com.meteocal.business.boundary.PersonalFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;

/**
 *
 * @author Manuel
 */
@ManagedBean
@ViewScoped
public class PersonalBean implements Serializable {

    @EJB
    PersonalFacade pf;
    @EJB
    HomeFacade hf;
      
    private static final long serialVersionUID = 1L;

    private Date dateTime = new Date();
    private String eventName, eventLocation, people, descr, constraint;
    private double eventDuration = 0.5;
    private boolean event_private;

    private String country, city, province, location;
    private Integer geoname = null;

    private List<String> countries, cities, provinces;
    private String text = "";

    private String calendarPrivacy;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event;

    

    @PostConstruct
    public void init() {

        countries = pf.getCountries();
        calendarPrivacy = pf.getCalendarString();

        //get all events
        //lazyEventModel = pf.getAllEvents();
        lazyEventModel = new LazyScheduleModel() {

            @Override
            public void loadEvents(Date start, Date end) {
                lazyEventModel = pf.getEvents(start, end);
            }

            //private Date getRandomDate(Date start) {
            //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            //}
        };

    }

    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1);    //set random day of month

        return date.getTime();
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public double getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(double eventDuration) {
        this.eventDuration = Math.max(eventDuration, 0.5);
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public boolean isEvent_private() {
        return event_private;
    }

    public void setEvent_private(boolean event_private) {
        this.event_private = event_private;
    }

    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }

    public String getCalendarPrivacy() {
        return calendarPrivacy;
    }

    public void setCalendarPrivacy(String calendarPrivacy) {
        this.calendarPrivacy = calendarPrivacy;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getCountries() {

        return countries;
    }

    public List<String> getProvinces() {

        return provinces;
    }

    public List<String> getCities() {

        return cities;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getGeoname() {
        return geoname;
    }

    public void setGeoname(Integer geocode) {
        this.geoname = geocode;
    }

    public String getText() {
        return text;
    }

    //</editor-fold>
    public PersonalBean() {
    }

    public void onCountryChange() {
        if (country != null && !country.equals("")) {
            provinces = pf.getProvinces(country);
        } else {
            provinces = new ArrayList<>();
        }
        cities = new ArrayList<>();
    }

    public void onProvinceChange() {
        if (province != null && !province.equals("")) {
            cities = pf.getCities(country, province);
        } else {
            cities = new ArrayList<>();
        }
    }

    public void onCityChange() {
        if (city != null && !city.equals("")) {
            location = city + " (" + province + ") - " + country;
            text = location;
            geoname = pf.getGeoname(country, province, city);

        } else {
            text = "";
        }
    }

    public void displayLocation() {
        FacesMessage msg;
        if (city != null && country != null) {
            msg = new FacesMessage("Selected", city + " of " + country);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "City is not selected.");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getLoggedUser() {
        return pf.getLoggedUser();
    }

    public void createEvent() {
        if (geoname == null) {
            pf.createEvent(eventName, text.trim(), dateTime, eventDuration, people, !event_private, constraint, descr);
        } else {
            //TODO Event w/ Integer geoname
        }
        init();
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
        }
        if (text != null && !text.trim().isEmpty()) {
            this.text = text.trim();
            location = text.trim();
            geoname = null;
        }
    }

    public void handleKeyEvent() {
        if (!"".equals(text.trim())) {
            text = "(" + text.trim() + ")";
        }
    }

    public String logout() {
        hf.logOut();
        return "/home?faces-redirect=true";
    }
    /*
     public void addMessage() {
     String summary = event_private ? "You have choosen to make the event Private" : "The event will no longer be Private";
     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
     }

     public void addMessage2() {
     String summary2 = event_private ? "Private" : "Public";
     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary2));
     }

     public void currentDate() {
     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
     Date date = new Date();
     System.out.println(dateFormat.format(date));
     }
     

     public void printHello() {
     try {
     ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
     HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
     PrintWriter pw = response.getWriter();
     pw.write("Hello");
     FacesContext.getCurrentInstance().responseComplete();
     } catch (IOException e) {
     FacesContext.getCurrentInstance().addMessage(
     "helloWorldButtonId",
     new FacesMessage("Error:" + e.getMessage())
     );
     }
     }
     */

    public String getCalendarString() {
        return pf.getCalendarString();
    }

    public void toggleCalendarPrivacy() {
        pf.togglePrivacy();
        calendarPrivacy = pf.getCalendarString();
    }

    public String getCalendar() {
        return pf.startDownload();
    }

    public String cannotCreate() {
        String out = "true";
        if (((location != null && location.length() > 3) || geoname != null) && dateTime != null) {
            out = "false";
        }
        return out;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        
        Logger.getLogger(PersonalBean.class.getName()).log(Level.INFO, "event clicked , {0}",event.getStartDate().toString());
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("event.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    private Date previousDay8Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 8);

        return t.getTime();
    }

    private Date previousDay11Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 11);

        return t.getTime();
    }

    private Date today1Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 1);

        return t.getTime();
    }

    private Date theDayAfter3Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 3);

        return t.getTime();
    }

    private Date today6Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 6);

        return t.getTime();
    }

    private Date nextDay9Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 9);

        return t.getTime();
    }

    private Date nextDay11Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 11);

        return t.getTime();
    }

    private Date fourDaysLater3pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
        t.set(Calendar.HOUR, 3);

        return t.getTime();
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            lazyEventModel.addEvent(event);
        } else {
            lazyEventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}

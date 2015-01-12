package com.meteocal.gui;

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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.event.SelectEvent;



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
    
    /*    @ManagedProperty(value="#{eventBean}")
    private EventBean eventBean;
    
    public EventBean getEventBean() {
    return eventBean;
    }
    
    public void setEventBean(EventBean eventBean) {
    this.eventBean = eventBean;
    }*/
      
    private static final long serialVersionUID = 1L;

    private Date dateTime = new Date();
    private String eventName, eventLocation, people, descr, constraint;
    private double eventDuration = 0.5;
    private boolean event_private;

    private String country, city, province;
    private Integer geoname = null;

    private List<String> countries, cities, provinces;
    private String text = "";

    private String calendarPrivacy;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event;

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    @PostConstruct
    public void init() {
      
        //get all events
        //lazyEventModel = pf.getAllEvents();
        lazyEventModel = new LazyScheduleModel() {

            @Override
            public void loadEvents(Date start, Date end) {
                
                List<ScheduleEvent> list = pf.getEvents(start, end).getEvents();
                
                list.stream().forEach((e) -> {
                    this.addEvent(e);
                });
            }

        };
        
        countries = pf.getCountries();
        
        calendarPrivacy = pf.getCalendarString();

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


    public Integer getGeoname() {
        return geoname;
    }

    public void setGeoname(Integer geocode) {
        this.geoname = geocode;
    }

    public String getText() {
        if(text==null || text.isEmpty()){
            return "";
        } else if (geoname==null){
            return "(" + text + ")"; 
        } else {
            return text;
        }
    }


           

    //</editor-fold>
    
    
    public PersonalBean() {

    }
    

    public void onCountryChange() {
        if (country != null && !country.isEmpty()) {
            provinces = pf.getProvinces(country);
        } else {
            provinces = new ArrayList<>();
        }
        cities = new ArrayList<>();
    }
    
    
    public void onProvinceChange() {
        if (province != null && !province.isEmpty()) {
            cities = pf.getCities(country, province);
        } else {
            cities = new ArrayList<>();
        }
    }
    

    public void onCityChange() {
        if (city != null && !city.isEmpty()) {
            text = city + " (" + province + ") - " + country;
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
        if(!pf.createEvent(eventName, text.trim(), geoname, dateTime, eventDuration, people, !event_private, constraint, descr))
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"ERROR, overlap","ERROR, overlap"));
        //init();
         lazyEventModel = new LazyScheduleModel() {

            @Override
            public void loadEvents(Date start, Date end) {
                
                List<ScheduleEvent> list = pf.getEvents(start, end).getEvents();
                
                list.stream().forEach((e) -> {
                    this.addEvent(e);
                });
            }

        };
    }

    
    public void setText(String text) {
        if (text == null){
            this.text = "";
        } else {
            this.text = text.trim();
            geoname = null;
        }
    }

    
    public void handleKeyEvent() {
          
        text = text.trim();

    }

    
    public String logout() {
        hf.logOut();
        return "/home?faces-redirect=true";
    }

    
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
        if (((text != null && text.length() > 3) || geoname != null) && dateTime != null) {
            out = "false";
        }
        return out;
    }    
    
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getSessionMap().put("eventId", event.getData());
        //TODO bind the ID of the original event (maybe we should to override getId() )
        try {
            ec.redirect("event.xhtml?faces-includeViewParams=true");
        } catch (IOException ex) {
            Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
  
}
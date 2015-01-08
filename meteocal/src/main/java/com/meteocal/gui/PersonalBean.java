/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.PersonalFacade;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Manuel
 */
@ManagedBean
@ViewScoped
public class PersonalBean implements Serializable{

    @EJB
    PersonalFacade pf;

    private Date dateTime = new Date();
    private String eventName, eventLocation, people, descr;
    private String constraint;
    private double eventDuration = 0.5;
    private boolean event_private;
    
    private String country; 
    private String city;  
    private String province;
    
    private String location;
    private Integer geoname = null;

    private List<String> countries;
    private List<String> cities;
    private List<String> provinces;
    
    private String text="";
    
    
    @PostConstruct
    public void init() {
        
        countries = pf.getCountries();

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
        
        return provinces ;
    }
 
    public List<String> getCities() {
        
        return cities;
    }
 
    public void onCountryChange(){
        if(country !=null && !country.equals("")){
            provinces = pf.getProvinces(country);
        }
        else{
            provinces = new ArrayList<>();
        }
        cities = new ArrayList<>();
    }
    
    public void onProvinceChange() {
        if(province !=null && !province.equals("")){
            cities = pf.getCities(country, province);
        }
        else{
            cities = new ArrayList<>();
        }
    }
    
    public void onCityChange(){
        if(city !=null && !city.equals("")){
            location = city + " (" + province + ") - " + country;
            text = location;
            geoname=pf.getGeoname(country, province, city);

        }
        else{
            text="";
        }
    }
    
    public void displayLocation() {
        FacesMessage msg;
        if(city != null && country != null)
            msg = new FacesMessage("Selected", city + " of " + country);
        else
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "City is not selected."); 
             
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }
  

    //there's some stuff in the personal page that uses the EventBean instead of the PersonalBean -- we need to fix it.
    public PersonalBean() {
    }
    
    //it's better to use the String here and the StringTokenizer in the PersonalFacade
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

    public void createEvent() {
        if(geoname==null){
            pf.createEvent(eventName, eventLocation, dateTime, eventDuration, people, !event_private, constraint, descr);
        } else {
            //TODO Event w/ Integer geoname
        }
        //TODO refresh page
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
    
    public String getLoggedUser(){
        return pf.getLoggedUser();
    }
 
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        if(text==null)
            this.text = "";
        if(text!=null && !text.trim().isEmpty()){
            this.text = text.trim();
            location = text.trim();
            geoname = null;
        }
    }
    
    public void handleKeyEvent() {
        if(!"".equals(text.trim()))
            text = "(" + text.trim() + ")";
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
    */
}

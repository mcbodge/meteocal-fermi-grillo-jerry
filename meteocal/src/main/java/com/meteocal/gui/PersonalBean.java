/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.PersonalFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Manuel
 */
@ManagedBean
@RequestScoped
public class PersonalBean implements Serializable{

    @EJB
    PersonalFacade pf;

    private Date dateTime = new Date();
    private String eventName, eventLocation, people, descr;
    private String constraint;
    private double eventDuration = 0.5;
    private boolean event_private;

    //TODO there's some stuff in the personal page that uses the EventBean instead of the PersonalBean we need to fix it.
    public PersonalBean() {
    }
    
    //TODO it's better to use the String here and the StringTokenizer in the PersonalFacade
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
        pf.createEvent(eventName, eventLocation, dateTime, eventDuration, people, !event_private, constraint, descr);
    }
    
    public String getLoggedUser(){
        return pf.getLoggedUser();
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

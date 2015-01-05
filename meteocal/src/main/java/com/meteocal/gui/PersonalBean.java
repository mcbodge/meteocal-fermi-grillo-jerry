/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.PersonalFacade;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.inject.Named;

/**
 *
 * @author Manuel
 */
@Named
public class PersonalBean {
    
    @EJB
    PersonalFacade pf;

    private StringTokenizer people;
    private String eventName, eventLocation, date, time, descr;
    private int constraint;
    private double eventDuration;
    private boolean event_private;
    
    public PersonalBean() {
    }

    public StringTokenizer getPeople() {
        return people;
    }

    public void setPeople(StringTokenizer people) {
        this.people = people;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public double getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(double eventDuration) {
        this.eventDuration = eventDuration;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getConstraint() {
        return constraint;
    }

    public void setConstraint(int constraint) {
        this.constraint = constraint;
    }

    public boolean isEvent_private() {
        return event_private;
    }

    public void setEvent_private(boolean event_private) {
        this.event_private = event_private;
    }
    
    public String getLoggedUser(){
        return pf.getLoggedUser();
    }
   
    public void createEvent(){
        //createEvent(String creator, String name, String dateStart, String timeStart, double duration, boolean private, Integer constraint, String description)
        pf.createEvent(getLoggedUser(), eventName,eventLocation, date, time, eventDuration, event_private, constraint, descr);
    }
    
    
    
}

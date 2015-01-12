package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import com.meteocal.business.boundary.PersonalFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;




/**
 *
 * @author Jude
 */
@ManagedBean/*(name="eventBean")*/
@ViewScoped
public class EventBean implements Serializable{
    
    private String eventId;
    private boolean menuShowable;
    
    @EJB
    EventFacade ef;
    @EJB
    PersonalFacade pf;
    
    @PostConstruct
    private void init(){
        eventId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("eventId").toString();
        menuShowable = ef.getCreator(eventId).equals(pf.getLoggedUser());
    }
   
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }



    public String getName(){
        return ef.getName(eventId);
    }
    
    public String getPrivacy(){
        return ef.getPrivacy(eventId);
    }
    
    public String getPicture(){
        return ef.getPicture(eventId);
    }
    
    public String getConstraint(){
        return ef.getConstraint(eventId);
    }
    
    public String getLocation(){
        return ef.getLocation(eventId);
    }
    
    public String getAttendees(){
        return ef.getAttendees(eventId);
    }
    
    public String getMaybe(){
       return ef.getMaybe(eventId);
    }
    
    public String getDate(){
        return ef.getDate(eventId);
    }
    
    public String getDescription(){
        return ef.getDescription(eventId);
    }
    
    public String getNotGoing(){
        return ef.getNotGoing(eventId);
    }
    
    public String getForecast(){
        return ef.getForecast(eventId);
    }

    public boolean isMenuShowable() {
        return menuShowable;
    }

    public void setMenuShowable(boolean menuShowable) {
        this.menuShowable = menuShowable;
    }
    
    
    
}
    

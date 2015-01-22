package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import com.meteocal.business.boundary.NotificationsFacade;
import com.meteocal.business.boundary.PersonalFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Manuel
 */
@ManagedBean/*(name="eventBean")*/

@ViewScoped
public class EventBean implements Serializable {

    @EJB
    EventFacade ef;
    @EJB
    PersonalFacade pf;
    @EJB
    NotificationsFacade nf;

    private String eventId;
    private String menuShowable;

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public String getMenuShowable() {
        return menuShowable;
    }

    public void setMenuShowable(String menuShowable) {
        this.menuShowable = menuShowable;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    //</editor-fold>

    public EventBean() {

    }

    @PostConstruct
    private void init() {
        eventId = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("eventId").toString();
        menuShowable = ef.isObserver(eventId).toString();
    }

    public String getName() {
        return ef.getName(eventId);
    }

    public String getPrivacy() {
        return ef.getPrivacy(eventId);
    }

    public String getPicture() {
        return ef.getPicture(eventId);
    }

    public String getConstraint() {
        return ef.getConstraint(eventId);
    }

    public String getLocation() {
        return ef.getLocation(eventId);
    }

    public String getAttendees() {
        return ef.getAttendees(eventId);
    }

    public String getMaybe() {
        return ef.getMaybe(eventId);
    }

    public String getDate() {
        return ef.getDate(eventId);
    }

    public String getDescription() {
        return ef.getDescription(eventId);
    }

    public String getNotGoing() {
        return ef.getNotGoing(eventId);
    }

    public String getForecast() {
        return ef.getForecast(eventId);
    }

    public String canAccept() {
        return ef.canAccept(eventId).toString();
    }

    public String disableAccept() {
        return String.valueOf(!nf.canAccept(ef.getEvent(eventId)));
    }

    public String canDecline() {
        return ef.canDecline(eventId).toString();
    }

    public String canDelete() {
        return ef.isCreator(eventId).toString();
    }

    public String canEdit() {
        String out = "false";
        //non edit un evento già finito
        if (!(pf.isFinished(Integer.parseInt(eventId)))) {
            out = ef.isCreator(eventId).toString();
        }
        return out;
    }

    public String accept() {
        nf.acceptInvitation(Integer.parseInt(eventId));
        return "event?faces-redirect=true";
    }

    public String decline() {
        
        String out = "event?faces-redirect=true";
        
        if(ef.canAccept(eventId)){
            nf.declineInvitation(Integer.parseInt(eventId));
        }else{
            nf.turnBack(Integer.parseInt(eventId));
            out = "personal?faces-redirect=true";
        }
        
        return out;
    }

    public String deleteEvent() {
        ef.deleteEvent(Integer.parseInt(eventId));
        return "personal?faces-redirect=true";
    }

    public void edit() {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        ec.getSessionMap().clear();

        ec.getSessionMap().put("editMode", true);

        ec.getSessionMap().put("dateTime", ef.getStart(eventId));
        ec.getSessionMap().put("eventName", ef.getName(eventId));
        ec.getSessionMap().put("people", ef.getPeople(eventId));
        ec.getSessionMap().put("descr", ef.getDescription(eventId));
        ec.getSessionMap().put("constraint", ef.getConstraintBack(eventId).toString());
        ec.getSessionMap().put("eventDuration", ef.getDuration(eventId));
        ec.getSessionMap().put("event_private", ef.eventPrivate(eventId));
        ec.getSessionMap().put("geoname", ef.getGeoname(eventId));
        ec.getSessionMap().put("text", ef.getLocation(eventId));
        ec.getSessionMap().put("eventId", Integer.parseInt(eventId));

        try {
            ec.redirect("personal.xhtml?faces-includeViewParams=true");
        } catch (IOException ex) {
            Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String loadWeatherImage() {
        
        return ef.loadWeatherImage(eventId);
        
    }

}

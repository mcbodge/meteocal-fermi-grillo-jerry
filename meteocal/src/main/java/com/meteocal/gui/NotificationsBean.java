/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.NotificationsFacade;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Manuel
 */
@ManagedBean
@RequestScoped
public class NotificationsBean implements Serializable {

    @EJB
    NotificationsFacade nf;

    private String title;
    private List<ArrayList<String>> string_list;

    public NotificationsBean() {
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public List<ArrayList<String>> getString_list() {
        return string_list;
    }

    public void setString_list(List<ArrayList<String>> string_list) {
        this.string_list = string_list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    //</editor-fold>

    @PostConstruct
    public void init() {
        string_list = new ArrayList<>();
        Logger.getLogger(NotificationsBean.class.getName()).log(Level.INFO, "NOTIFICATIONS BEAN INIT---------START");
        if(nf.getCompleteList().isEmpty()){
            title = "No notifications to show";
        }else{
            title = "Your Notifications";
            string_list = nf.getCompleteList();
        }
        Logger.getLogger(NotificationsBean.class.getName()).log(Level.INFO, "NOTIFICATIONS BEAN INIT---------END");
    }

    public String showButton(String type) {
        String out = "false";
        if (type.equals("invitation")) {
            out = "true";
        }
        return out;
    }

    public String iconPath(String type) {
        String out = "https://cdn3.iconfinder.com/data/icons/49handdrawing/256x256/info.png";
        if (type.equals("invitation")) {
            out = "https://cdn3.iconfinder.com/data/icons/49handdrawing/128x128/mail.png";
        }
        return out;
    }

    public String accept(String id, String type) {
        
        if (type.equals("invitation")) {
            //accept invitation
            nf.acceptInvitation(Integer.parseInt(id));
        }else{
            //hide information
            nf.readInformation(Integer.parseInt(id));
        }
        //refresh
        return "notifications?faces-redirect=true";
    }

    public String decline(String eventId) {
        //decline invitation
        nf.declineInvitation(Integer.parseInt(eventId));
        return "notifications?faces-redirect=true";
    }

    public String acceptText(String type) {
        String out = "Got it";
        if (type.equals("invitation")) {
            out = "Accept";
        }
        return out;
    }
    
    public void openEvent(Integer eventId){
        ExternalContext exc = FacesContext.getCurrentInstance().getExternalContext();

        exc.getSessionMap().put("eventId", eventId);

        //bind the ID of the original event 
        try {
            exc.redirect("event.xhtml?faces-includeViewParams=true");
        } catch (IOException ex) {
            Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String hasEvent(Integer eventId){
        return Boolean.toString(eventId!=null);
    }
}

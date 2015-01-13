/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.NotificationsFacade;
import java.io.File;
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
    //</editor-fold>

    @PostConstruct
    public void init() {
        Logger.getLogger(NotificationsBean.class.getName()).log(Level.INFO, "NOTIFICATIONS BEAN INIT---------START");
        string_list = nf.getCompleteList();
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

    public void accept(String eventId, String type) {
        
        if (type.equals("invitation")) {
            //accept invitation
        }else{
            //hide information
        }
    }

    public void decline(String eventId) {
        //decline invitation
    }

    public String acceptText(String type) {
        String out = "Got it";
        if (type.equals("invitation")) {
            out = "Accept";
        }
        return out;
    }
}

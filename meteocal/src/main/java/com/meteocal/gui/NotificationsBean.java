/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.NotificationsFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Manuel
 */
@Named
@ViewScoped
public class NotificationsBean implements Serializable {

    @EJB
    NotificationsFacade nf;

    private List<String[]> string_list;

    public NotificationsBean() {
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public List<String[]> getString_list() {
        return string_list;
    }

    public void setString_list(List<String[]> string_list) {
        this.string_list = string_list;
    }
    //</editor-fold>

    @PostConstruct
    public void init() {

    }

    public void accept() {

    }

    public void decline() {

    }
}

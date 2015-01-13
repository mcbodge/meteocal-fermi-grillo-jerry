/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.HomeFacade;
import javax.ejb.EJB;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Francesco
 */
@ManagedBean
@RequestScoped
public class HomeBean implements Serializable {

    @EJB
    HomeFacade hf;

    private String username, password;
    private boolean remainlogged;

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getRemainLogged() {
        return remainlogged;
    }

    public void setRemainLogged(boolean remainlogged) {
        this.remainlogged = remainlogged;
    }
    //</editor-fold>


    public String submitLogIn() {
        if (hf.submitLogIn(username, password)) {
            //goto personal page
            return "/user/personal?faces-redirect=true";
        }
        //stay in home
        return "/home?faces-redirect=true";
    }

}

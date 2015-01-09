/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.RegistrationFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Jude
 */
@ManagedBean
@SessionScoped
public class RegistrationBean implements Serializable {

    @EJB
    RegistrationFacade rf;

    private String firstname, lastname, username, email, password, emailagain;

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public String getEmailagain() {
        return emailagain;
    }

    public void setEmailagain(String emailagain) {
        this.emailagain = emailagain;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
            return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    //</editor-fold>

    public RegistrationBean() {
    }

    //TODO jdoc
    /**
     *
     * @return
     */
    public String createAccount() {
        //check email and create user

        if (password!=null && rf.registerUser(firstname, lastname, username, email, password)) {
            return "/home?faces-redirect=true";
        }

        //Submitted data not valid
        return "/registration?faces-redirect=true";
    }

}

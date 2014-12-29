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
 * @author Manuel
 */

/**
 *
 * @author Jude
 */
@ManagedBean
@SessionScoped
public class RegistrationBean implements Serializable{
    
    @EJB
    RegistrationFacade rf;
    
     private String firstname;
     private String lastname;
    
    
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
    
    public void createAccount() {    //Jude - TODO needs to check the provided data and create account
        
    }



    public RegistrationBean() {    //Jude - I don't know what this method is suppossed tp be doing.
        
    }
    
    
}

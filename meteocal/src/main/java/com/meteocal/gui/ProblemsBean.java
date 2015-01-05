/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.ProblemsFacade;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Manuel
 */
@Named
@RequestScoped
public class ProblemsBean {

    private String email;

    @EJB
    ProblemsFacade pf;

    public ProblemsBean() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String forgotMyPassword() {
        pf.forgotPassword(email);
        return "home?faces-redirect=true";
    }
    public String forgotMyUserName() {
        pf.forgotUsername(email);
        return "home?faces-redirect=true";
    }

}

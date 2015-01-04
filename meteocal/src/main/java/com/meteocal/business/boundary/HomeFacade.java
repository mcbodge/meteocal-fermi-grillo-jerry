/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Manuel
 */
@Stateless
public class HomeFacade {
    
    @Inject 
    LogInManager loginManager;
    @Inject
    EventManager eventmanager;
    
    @PersistenceContext
    EntityManager em;
    
    //welcome inform. still need to be added. -- a welcome information is simply a new instance in the information table, with event set to null.
    /**
     * Logs in - a validated user - redirecting him to his/her personal page. 
     * It also sends a "welcome" information.
     * 
     * @param u the username we want to log in
     * @param p the password
     * @return the URL of the user's personal page
     */
    public String loadUser(String u, String p){
        if (loginManager.checkLogIn(em.createNamedQuery("User.findByUserName",User.class).setParameter("userName", u).getSingleResult())){
            loginManager.logOutCurrentSession();
            return "re-log";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(u,p);
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Login failed."));
            Logger.getLogger(LogInManager.class.getName()).log(Level.SEVERE, "Login Failed");
            return "home";
        }        
        context.addMessage(null, new FacesMessage("Login OK."));
        Logger.getLogger(LogInManager.class.getName()).log(Level.INFO, "LoggedIN");
        //welcome information
        User user_param = (User)em.createNamedQuery("User.findByUserName",User.class).setParameter("userName", u).getSingleResult();
        eventmanager.newInformation(user_param, "Welcome!");
        
        return "/user/personal";
    }
}

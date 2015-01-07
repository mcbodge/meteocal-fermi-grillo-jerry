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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Manuel
 */
@Stateless
public class HomeFacade {

    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;

    @Inject
    LogInManager loginManager;

    @Inject
    EventManager eventmanager;

    /**
     * Logs in - a validated user - redirecting him to his/her personal page. It
     * also sends a "welcome" information.
     *
     * @param u the username we want to log in
     * @param p the password
     * @return true if login is ok.
     */
    public boolean submitLogIn(String u, String p) {
        boolean result = false;

        if (loginManager.checkLogInFields(u, p)) {
            //check if user exists and 
            User user = null;
            try {
                user = em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", u).getSingleResult();
            } catch (NoResultException ex) {
                Logger.getLogger(LogInManager.class.getName()).log(Level.SEVERE, "Login Failed, invalid username");
            }
            if (user == null) {
                //user does not exist 
            } else {
                //chek if the user is already logged in
                if (u.equals(loginManager.getLoggedUserName())) {
                    //user is already logged in 
                    logOut();
                } else {
                    //user is not already logged in
                    //try to login
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                    try {
                        request.login(u, p);
                        context.addMessage(null, new FacesMessage("Login OK."));
                        Logger.getLogger(LogInManager.class.getName()).log(Level.INFO, "Login succeeded, user {0} LoggedIn", u);
                        result = true;

                        //create a welcome information
                        String welcome_message = "Welcome " + user.getUserName() + "!";
                        em.persist(eventmanager.newInformation(user, welcome_message));

                    } catch (ServletException e) {
                        //login failed
                        context.addMessage(null, new FacesMessage("Login failed."));
                        Logger.getLogger(LogInManager.class.getName()).log(Level.SEVERE, "Login Failed, failed to LogIn {0}", u);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Perform Log Out
     * 
     */
    public void logOut() {
        Logger.getLogger(LogInManager.class.getName()).log(Level.INFO, "User {0} Logged out.", loginManager.getLoggedUserName());
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
    }

}

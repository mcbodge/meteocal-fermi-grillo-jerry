/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.User;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * FROM/TO - B:HomePage
 * TO - B:PersonalPage, E:User
 * 
 * @author Manuel
 */
public class LogInManager{
    
    @PersistenceContext
    EntityManager em;
    
    //TODO
    /**
     * If the structure of the input is wrong (empty fields, the password is too short, etc.) 
     * it returns the registration page.
     * 
     * If the structure of the input is correct, it calls verifyLogin() that, 
     * if it returns true, it redirect to the personal page, logging the user.
     * 
     * @return the URL of the appropriate page
     */
    public String checkLogInFields(){
        return null;
    }
    
    
    //TODO - To be implemented only if needed. Cannot be public.
    /**
     * Returns true if the user is currently logged. False otherwise.
     * 
     * @param u the user that we want to log in
     * @return "true" if the user is logged
     */
    private boolean checkLogIn(User u){
        return false;
    }
    
    
    //TODO RC
    /**
     * Logs out a session
     * 
     * //@param u 
     */  
    //USER PARAM. IS NOT NEEDED, BUT WE SHOULD REDIRECT TO LOG IN PAGE.
    public void logOutCurrentSession(/*User u*/){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        //logger.log(Level.INFO, "User Logged out");
        //return "/index?faces-redirect=true";
    }
    
    
    //TODO RC welcome inform. still need to be added.
    /**
     * Logs in - a validated user - redirecting him to his/her personal page. 
     * It also sends a "welcome" information.
     * 
     * @param u the username we want to log in
     * @param p the password
     * @return the URL of the user's personal page
     */
    public String loadUser(String u, String p){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(u,p);
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Login failed."));
            //logger.log(Level.SEVERE,"Login Failed");
            return "home";
        }        
        context.addMessage(null, new FacesMessage("Login OK."));
        return "/user/personal";
        
    }
    
 
    //TODO RC
    /**
     * False <- User name doesn't exist
     * False <- User name exist && password is wrong
     * True <- Otherwise
     * 
     * @param un user name
     * @param p password
     * @return "true" if the the fields are correct
     */ 
    private boolean verifyLogIn(String un, String p){
        User usr;
        
        //check user
        try{
            usr = (User)em.createNamedQuery("User.findByUserName")
                    .setParameter("userName", un).getSingleResult();
        }catch(NoResultException nre){
            //user doesn't exists.
            return false;
        }
        
        //check password
        if(usr.getPassword().equals(encryptPassword(p))){
            // user ok AND psw ok
            return true;
        }
        //user ok AND psw mismatch
        return false;
    }
    
    /**  
     * Encrypt the password.
     * 
     * @param password
     * @return password encrypted
     */
    public static String encryptPassword(String password) {
        String encPass = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, hash);
            encPass = bigInt.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encPass;
    }
}

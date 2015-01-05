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
import java.security.Principal;
import java.util.logging.Level;
import javax.faces.context.FacesContext;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

/**
 * FROM/TO - B:HomePage
 * TO - B:PersonalPage, E:User
 * 
 * @author Manuel
 */
public class LogInManager{
    
    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;
    @Inject
    Principal principal;
    
    
    
    /**
     * 
     * @return the username of the user logged in
     */
    public String getLoggedUser(){
        return principal.getName();
    }

    //TODO
    /**
     * If the structure of the input is wrong (empty fields, the password is too short, etc.) 
     * it returns false.
     * 
     * If the structure of the input is correct, it returns true
     * @param u the username
     * @param p the password
     * @return false if the structure of the input is wrong
     */
    public boolean checkLogInFields(String u, String p){
        if(p == null || p.length() < 8 || u == null || u.length() < 4 )
            return false;
        return true;
    }
    
    /**
     * Returns true if the user is currently logged. False otherwise.
     * 
     * @param u the user that we want to log in
     * @return "true" if the user is logged
     */
    public boolean checkAlreadyLoggedIn(User u){         
        if(u != null && getLoggedUser() != null){
            if(u.getUserName().equals(getLoggedUser()))
                return true;
        }
        return false;
    }
    
    
    /**
     * Logs out a session
     * 
     */  
    public void logOutCurrentSession(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        Logger.getLogger(LogInManager.class.getName()).log(Level.INFO, "User Logged out");
    }
    
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
        
        //check password of an existing user
        //if the password is ok it returns true, otherwise - if it mismatches - it returns false
        return usr.getPassword().equals(encryptPassword(p));  

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
            Logger.getLogger(LogInManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encPass;
    }
}

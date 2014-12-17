/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.User;

/**
 * FROM/TO - B:HomePage
 * TO - B:PersonalPage, E:User
 * 
 * @author Manuel
 */
public class LogInManager{
    
    //TODO
    /**
     * If the structure of the input is wrong (empty fields, the password is too short, etc.) it returns the registration page.
     * If the structure of the input is correct, it calls verifyLogin() that, if it returns true, it redirect to the personal page, logging the user.
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
    
    
    //TODO
    /**
     * Logs out a session
     * 
     * @param u 
     */
    private void logOutCurrentSession(User u){
        
    }
    
    
    //TODO
    /**
     * Logs in - a validated user - redirecting him to his/her personal page. It also sends a "welcome" information.
     * 
     * @param u the user we want to log in
     * @return the URL of the user's personal page
     */
    private String loadUser(User u){
        return null;
    }
    
 
    //TODO
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
        return false;
    }
}

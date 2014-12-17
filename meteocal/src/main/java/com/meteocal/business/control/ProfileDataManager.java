/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import java.util.ArrayList;

/**
 * FROM/TO - B:RegistrationPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class ProfileDataManager {
    
    /**
     * registration page <- s.thing wrong
     * home page <- user successfully created (he/she needs to log in)
     * 
     * @param first first name
     * @param last last name
     * @param username user name
     * @param email email
     * @param password password
     * @return the URL of the appropriate page
     */
    public String verifySubmittedData(String first, String last, String username, String email, String password){
        return null;
    }
    
    
    /**
     * It creates a new user, giving the previously submitted and checked data.
     * 
     * @param first first name
     * @param last last name
     * @param username user name
     * @param email email
     * @param password password
     */
    private void newUser(String first, String last, String username, String email, String password){
    }
}

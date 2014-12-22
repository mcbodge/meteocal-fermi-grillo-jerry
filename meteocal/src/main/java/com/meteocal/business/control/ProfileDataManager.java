/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * FROM/TO - B:RegistrationPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class ProfileDataManager {
    
    EntityManager em;
    
    //TODO RC
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
        //the length of username, first, last ...etc have to be checked in the gui pkg (mng beans)        
        
        //check email & username !alreadyInDB      
        TypedQuery<User> query;
        query = (TypedQuery<User>) em.createNativeQuery(
                "SELECT u FROM User u WHERE u.userName = :userName OR u.email = :email")
                .setParameter("userName", username)
                .setParameter("email", email);
        if(query.getResultList().isEmpty()){
            //OK, email and username are not already in DB
            newUser(first, last, username, email, password);
            return "home";
        }
        //Submitted data not valid
        return "registration";
    }
    
    //TODO RC
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
        User user = new User(username, first, last, email, password, true);
        em.persist(user);
    }
}

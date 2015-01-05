/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.Group;
import com.meteocal.business.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * FROM/TO - B:RegistrationPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class ProfileDataManager {
    
    @PersistenceContext
    EntityManager em;
    
    //TODO already tested, but we should do a little edit (new user should call verifySubmittedData)
    /**
     * registration page <- s.thing wrong
     * home page <- user successfully created (he/she needs to log in)
     * 
     * @param first first name
     * @param last last name
     * @param username user name
     * @param email email
     * @param password password
     * @return true = ok
     */
    private boolean verifySubmittedData(String first, String last, String username, String email, String password){
        //check email & username !alreadyInDB      
        TypedQuery<User> query;
        query = (TypedQuery<User>) em.createQuery(
                "SELECT u FROM User u WHERE u.userName = :userName OR u.email = :email")
                .setParameter("userName", username)
                .setParameter("email", email);
        if(query.getResultList().isEmpty()){
            //OK, email or username are not already in DB
            return true;
        }
        //Submitted data not valid
        return false;
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
    public boolean newUser(String first, String last, String username, String email, String password){
        if(verifySubmittedData(first, last, username, email, password)){
            User user = new User(username, first, last, email, password, true);
            user.setGroupName(Group.USERS);
            em.persist(user);
            return true;
        }
        return false;
    }
}

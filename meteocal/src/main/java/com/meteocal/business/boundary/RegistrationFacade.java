/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.ProfileDataManager;
import com.meteocal.business.entity.Group;
import com.meteocal.business.entity.User;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Manuel
 */
@Stateless
public class RegistrationFacade {

    @PersistenceContext
    EntityManager em;

    @Inject
    ProfileDataManager pdm;

    //TODO check user
    /**
     * write in db
     *
     * @param firstname
     * @param lastname
     * @param username
     * @param email
     * @param password
     *
     * @return true if registration is ok.
     */
    public boolean registerUser(String firstname, String lastname, String username, String email, String password) {
        User user = null;        
        if (verifySubmittedData(firstname, lastname, username, email, password)) {
            user = pdm.newUser(firstname, lastname, username, email, password);
            user.setGroupName(Group.USERS);
            user = em.merge(user);
            em.flush();
        }
        return user != null;
    }

    /**
     *
     * @param first first name
     * @param last last name
     * @param username user name
     * @param email email
     * @param password password
     * @return true = valid data
     */
    private boolean verifySubmittedData(String first, String last, String username, String email, String password) {

        if (first != null && !first.equals("") && last != null && !last.equals("") && username != null && !username.equals("") 
                && email != null && !email.equals("") && password != null && !password.equals("") ) {

            //check email & username !alreadyInDB      
            TypedQuery<User> query;
            query = (TypedQuery<User>) em.createQuery(
                    "SELECT u FROM User u WHERE u.userName = :userName OR u.email = :email")
                    .setParameter("userName", username)
                    .setParameter("email", email);

            if (query.getResultList().isEmpty()) {
                //OK, email or username are not already in DB
                return true;
            }
        }
        //Submitted data not valid
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.IssuesDataManager;
import com.meteocal.business.entity.User;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manuel
 */


@Stateless
public class ProblemsFacade {

    @Inject
    IssuesDataManager idm;

    @PersistenceContext
    EntityManager em;
    
    @Inject 
    EmailManager email_mng;
    
    /**
     * Returns the User if the email is a valid email of an user of the system.
     * Null otherwise.
     *
     * @param e - email of the user that has requested for support
     * @return User if the email is in the DB, null otherwise.
     */
    private User verifySubmittedData(String e) {
        User user = null;
        try {
            user = (User) em.createNamedQuery("User.findByEmail").setParameter("email", e).getSingleResult();
        } catch (NoResultException ex) {

        }
        return user;
    }
    
    
    /**
     * 
     * @param email the email of the user that request support
     */
    public void forgotUsername(String email) {
        User user = verifySubmittedData(email);
        if (user != null) {
            idm.sendUserName(user);
        }
    }
    

    /**
     * 
     * @param email the email of the user that request support
     */
    public void forgotPassword(String email) {
        User user = verifySubmittedData(email);
        if (user != null) {
            user = idm.sendPassword(user);
            //now the user has a new password
            em.merge(user);
        }
    }

}

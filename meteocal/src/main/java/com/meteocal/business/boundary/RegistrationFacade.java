/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.ProfileDataManager;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
     * @param firstname
     * @param lastname
     * @param username
     * @param email
     * @param password
     * 
     * @return true if registration is ok.
     */
    public String registerUser(String firstname, String lastname, String username, String email, String password){
        if(pdm.newUser(firstname, email, username, email, password))
            return "/home?faces-redirect=true";
        //Submitted data not valid
        return "/registration?faces-redirect=true";
    } 
}

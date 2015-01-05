/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.EventCreationManager;
import com.meteocal.business.control.EventManager;
import com.meteocal.business.control.LogInManager;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Manuel
 */
@Stateless
public class PersonalFacade {

    @Inject
    LogInManager lm;
    @Inject
    EventCreationManager eventCreationManager;
    @Inject
    EventManager eventManager;

    public String getLoggedUser() {
        return lm.getLoggedUser();
    }

    public boolean createEvent(String creator, String name, String location, String dateStart, String timeStart, 
            double duration, boolean event_private, Integer constraint, String description){
        //all conversions 
        //invoke method
        //eventCreationManager.newEvent(User creator, String name, Date start, Date end, 
        //             String location, List<User> invited, boolean p, Integer constraint, String description)
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.IssuesDataManager;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Manuel
 */
@Stateless
public class ProblemsFacade {
    
    @Inject
    IssuesDataManager idm;
    
    public void forgotUsername(String email){
        idm.sendUserName(email);
    }
    
    public void forgotPassword(String email){
        idm.sendPassword(email);
    }
    
}

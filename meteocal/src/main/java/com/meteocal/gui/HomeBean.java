/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.HomeFacade;
import javax.ejb.EJB;

/**
 *
 * @author Manuel
 */
public class HomeBean {
    
    @EJB
    HomeFacade hf;

    public HomeBean() {
    }
    
    
}

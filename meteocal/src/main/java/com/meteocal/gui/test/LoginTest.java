/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui.test;

import com.meteocal.business.boundary.HomeFacade;
import com.meteocal.gui.HomeBean;
import javax.ejb.EJB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
/**
 *
 * @author Jude
 */

@RunWith(MockitoJUnitRunner.class) 

public class LoginTest {
    
    @EJB
    HomeBean hb;
    
    @Test
    public void testLogin()throws Exception{
        HomeFacade hf;
        hf = Mockito.mock(HomeFacade.class);
         hf.submitLogIn("more","morepassword");
         
         Mockito.verify(hf.submitLogIn("more","morepassword"));
         
    }
}

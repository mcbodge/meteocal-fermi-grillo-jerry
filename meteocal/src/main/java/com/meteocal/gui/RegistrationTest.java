/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.RegistrationFacade;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Jude
 */

@RunWith(MockitoJUnitRunner.class) 

public class RegistrationTest extends EasyMockSupport{
    
    
    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    
    
        @Test
            public void testRegisterUser() throws Exception {
                
                RegistrationFacade rf = Mockito.mock(RegistrationFacade.class);
                
                //RegistrationBean registrationBean = new RegistrationBean();
                
                rf.registerUser("me","you", "everyone","everyone@yahoo.com", "dddddddddd");
                
                Mockito.verify(rf).registerUser("me","you", "everyone","everyone@yahoo.com", "dddddddddd");
            }
    
}

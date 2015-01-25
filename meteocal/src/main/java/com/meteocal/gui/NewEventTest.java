/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import com.meteocal.business.boundary.HomeFacade;
import com.meteocal.business.boundary.PersonalFacade;
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
public class NewEventTest {
    
    @EJB
    PersonalFacade pf;
    @EJB
    HomeFacade hf;
    @EJB
    EventFacade ef;
    
    @Test
    public void testCreateNewEvent() throws Exception{
        pf = Mockito.mock(PersonalFacade.class);
        pf.createEvent(null, null, Integer.SIZE, null, .5, null, true, null, null);
        Mockito.verify(pf.createEvent(null, null, Integer.SIZE, null, .5, null, true, null, null));
    }
}

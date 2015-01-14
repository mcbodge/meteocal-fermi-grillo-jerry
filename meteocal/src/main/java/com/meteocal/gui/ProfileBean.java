/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import com.meteocal.business.boundary.ProfileFacade;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Manuel
 */
@ManagedBean
@ViewScoped
public class ProfileBean {
    
    @EJB
    ProfileFacade pf;
    

    private String loadedUser;
    private ScheduleModel lazyEventModel;
    private String calendarVisible;

    public ProfileBean() {
    }
    
    @PostConstruct
    public void init(){
        
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        
        if (ec.getSessionMap().containsKey("loadUser")){
            loadedUser = (String) ec.getSessionMap().get("loadUser");
            ec.getSessionMap().clear();
            
            
        lazyEventModel = new LazyScheduleModel() {

            @Override
            public void loadEvents(Date start, Date end) {

                List<ScheduleEvent> list = pf.getEvents(start, end, loadedUser).getEvents();

                list.stream().forEach((e) -> {
                    this.addEvent(e);
                });
            }

        };

        calendarVisible = pf.isCalendarVisible(loadedUser);
            
            
        } else {
            try {
                ec.redirect("error.xhtml?faces-includeViewParams=true");
            } catch (IOException ex) {
                Logger.getLogger(ProfileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    public void onEventSelect(SelectEvent selectEvent) {

        ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();
        String id = (String) event.getData();

        if (pf.eventPublic(id)) {

            ExternalContext exc = FacesContext.getCurrentInstance().getExternalContext();
            exc.getSessionMap().put("eventId", id);

            //bind the ID of the original event 
            try {
                exc.redirect("event.xhtml?faces-includeViewParams=true");
            } catch (IOException ex) {
                Logger.getLogger(PersonalBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
         
}

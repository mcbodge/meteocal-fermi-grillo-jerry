package com.meteocal.business.boundary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Jude
 */

@ManagedBean
@SessionScoped
public class CalendarBean implements Serializable {
    
    private Date date = new Date();
    
    
    private Date calendar;
    
     
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
     
    public void click() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
         
        requestContext.update("form:display");
        requestContext.execute("PF('dlg').show()");
    }
 
    public Date getCalendar() {
        return calendar;
    }
 
    
    public void setCalendar(Date calendar) {
        this.calendar = calendar;
        
    }
    
    
    public Date getDate(){
        return date;
    }
    public void setDate(Date date){
        this.date = date;
    }

    
    
    

}


//<editor-fold defaultstate="collapsed" desc="comment">
/*package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
*
* @author Jude
*/
/*
@ManagedBean
@SessionScoped
public class CalendarBean implements Serializable {
    
    @EJB
            EventFacade ef;
    
    private Date date = new Date();
    
    
    private Date calendar;
    
    
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
    
    public void click() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        
        requestContext.update("form:display");
        requestContext.execute("PF('dlg').show()");
    }
    
    public Date getCalendar() {
        return calendar;
    }
    
    
    public void setCalendar(Date calendar) {
        this.calendar = calendar;
        
    }
    
    
    public Date getDate(){
        return date;
    }
    public void setDate(Date date){
        this.date = date;
    }
}*/
//</editor-fold>

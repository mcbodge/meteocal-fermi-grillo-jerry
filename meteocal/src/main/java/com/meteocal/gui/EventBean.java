package com.meteocal.gui;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;




/**
 *
 * @author Jude
 */
@ManagedBean/*(name="eventBean")*/
@ViewScoped
public class EventBean implements Serializable{

    public EventBean() {
    }
    
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
   
    public String myInfo(){
        return (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("eventId");
    }
  
}
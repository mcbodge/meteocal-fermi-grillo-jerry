package boundaries;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;




/**
 *
 * @author Jude
 */
@ManagedBean
@SessionScoped
public class EventBean implements Serializable{

    
    


}



//<editor-fold defaultstate="collapsed" desc="comment">
/*package com.meteocal.gui;

import com.meteocal.business.boundary.EventFacade;
import javax.ejb.EJB;
import java.io.Serializable;
import java.text.DateFormat;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
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
public class EventBean implements Serializable{

@EJB
EventFacade ef;

private Date startDate;
private Date startTime;
private String eventName;
private String eventLocation;
private String people;
private String duration;
private boolean privateEvent;
private String description;

public EventBean() {
}

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

public String getEventName(){
return eventName;
}
public void setEventName(String eventName){
this.eventName = eventName;
}

public String getEventLocation(){
return eventLocation;
}
public void setEventLocation(String eventLocation){
this.eventLocation = eventLocation;
}

public String getPeople(){
return people;
}
public void setPeople(String people){
this.people = people;
}


public Date getStartDate(){
return startDate;
}
public void setStartdate(Date startDate){
this.startDate = startDate;

}

public Date getStartTime(){
return startTime;
}
public void setStartTime(Date startTime){
this.startTime = startTime;
}

public boolean getPrivateEvent(){
return privateEvent;
}
public void setprivateEvent(boolean privateEvent){
this.privateEvent = privateEvent;
}


public String getDescription(){
return description;
}
public void setDescription(String description){
this.description = description;
}

public void displayContent(){

}

public void createEvent(){ //Check the values and create a new event and return a successful message

}




}*/
//</editor-fold>

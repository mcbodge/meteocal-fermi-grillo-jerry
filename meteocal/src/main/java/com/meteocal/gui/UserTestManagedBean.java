/*
 * This class si used to test the connection to the db
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.boundary.UserTestSessionBean;
import com.meteocal.business.control.IssuesDataManager;
import com.meteocal.business.entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author Francesco
 */
@Named
@RequestScoped
public class UserTestManagedBean {
    
    @EJB 
    private UserTestSessionBean sb;
    
    private User user;
    private String receiver;
    private String yem;

    public String getYem() {
        return yem;
    }

    public void setYem(String yem) {
        this.yem = yem;
    }
    
    public UserTestManagedBean() {
    }
       
    public String allUserNames(){
        String result="";
        List<User> users = sb.findAll();
        
        for (User next : users) {
            result= result + "\t " + next.getUserName();
        }
        return result;
    }
       
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
   
    public String save(){
        sb.saveUser(user);
        return "index?faces-redirect=true";
    }
       
    public String sendMail(){
        EmailManager.getInstance().sendEmail(receiver, "METEOCAL: Test", "Hello world. \nThis is a notification.?");
        return "index?faces-redirect=true";
    }
    
    public String issues(){
        sb.issues(yem);
        return "index?faces-redirect=true";
    }
    
}

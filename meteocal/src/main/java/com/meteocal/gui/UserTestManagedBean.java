/*
 * This class si used to test the connection to the db
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.boundary.UserTestSessionBean;
import com.meteocal.business.control.IssuesDataManager;
import com.meteocal.business.entity.User;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
    private String lun;
    private String lpw;
    private String lvres;
    private String logres;

    public String getLogres() {
        return logres;
    }

    public void setLogres(String logres) {
        this.logres = logres;
    }

    public String getLvres() {
        return lvres;
    }

    public void setLvres(String lvres) {
        this.lvres = lvres;
    }

    public String getLun() {
        return lun;
    }

    public void setLun(String lun) {
        this.lun = lun;
    }

    public String getLpw() {
        return lpw;
    }

    public void setLpw(String lpw) {
        this.lpw = lpw;
    }

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
        return "francescoTEST?faces-redirect=true";
    }
       
    public String sendMail(){
        EmailManager.getInstance().sendEmail(receiver, "METEOCAL: Test", "Hello world. \nThis is a notification.");
        return "francescoTEST?faces-redirect=true";
    }
    
    public String issues(){
        sb.issues(yem);
        return "francescoTEST?faces-redirect=true";
    }
    
    public void verify(){
        lvres=sb.verifyLogIn(lun, lpw);
    }
    /*
    public void login(){
        logres=sb.loadUser(lun, lpw);
    }
    */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(this.lun, this.lpw);
            return "/user/personal";
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login Failed","Login Failed"));
            //logger.log(Level.SEVERE,"Login Failed");
            return null;
        }
    }
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        //logger.log(Level.INFO, "User Logged out");
        return "/index?faces-redirect=true";
    }
}

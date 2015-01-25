package com.meteocal.gui;

import com.meteocal.business.boundary.ProblemsFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Manuel
 */
@ManagedBean
@SessionScoped
public class ProblemsBean implements Serializable {

    @EJB
    ProblemsFacade pf;

    private String email; 
            
    private boolean loginProblem;
    
    private String message="nothing ¯\\(°_o)/¯";

    public String getMessage() {
        return message;
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS AND SETTERS">
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getLoginProblem() {
        return loginProblem;
    }

    public void setLoginProblem(boolean loginProblem) {
        this.loginProblem = loginProblem;
    }
    //</editor-fold>

    public ProblemsBean() {
    }
    
    public String message() {
        return this.message;
    }
    
    
    
    public void submitProblem() {
        if (this.loginProblem) {
            message = "your new password.";
            pf.forgotPassword(email);
        } else {
            message = "your user name.";
            pf.forgotUsername(email);
        }
    }

    
    

}

//<editor-fold defaultstate="collapsed" desc="comment">
/*
 package boundaries;

 import java.io.Serializable;
 import javax.enterprise.context.SessionScoped;
 import javax.faces.bean.ManagedBean;

 /**
 *
 * @author Jude
 *//*
 @ManagedBean
 @SessionScoped
 public class ProblemBean implements Serializable{

 private String loginProblem;
 private String email;

 public String getLoginProblem(){
 return loginProblem;
 }
 public void setLoginProblem(String loginProblem){
 this.loginProblem = loginProblem;
 }

 public String getEmail(){
 return email;
 }
 public void setEmail(String email){
 this.email = email;
 }

 public String submitProblem(){
 return "/ProblemSolved.xhtml";
 }

 public String goToLogin(){
 return "index.xhtml";
 }

 }*/
//</editor-fold>

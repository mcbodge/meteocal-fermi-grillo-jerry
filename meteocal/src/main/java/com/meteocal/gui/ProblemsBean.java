/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.ProblemsFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;


/**
 *
 * @author Jude
 */

@ManagedBean
@SessionScoped
public class ProblemsBean implements Serializable{


    private String email;

    @EJB
    ProblemsFacade pf;
    
    private String loginProblem;

    public ProblemsBean() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getLoginProblem(){
        return loginProblem;
    }
    public void setLoginProblem(String loginProblem){
        this.loginProblem = loginProblem;
    }
    
    public String forgotMyPassword() {
        pf.forgotPassword(email);
        return "home?faces-redirect=true";
    }
    public String forgotMyUserName() {
        pf.forgotUsername(email);
        return "home?faces-redirect=true";
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Jude
 */
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
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.HomeFacade;
import javax.ejb.EJB;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;


/**
 *
 * @author Jude
 */


@ManagedBean
@SessionScoped
public class HomeBean implements Serializable{
    
    @EJB
    HomeFacade hf;

   
     
    private String username;
    private String password;
    private boolean remainlogged;
    
    
    
    public String getUserName(){
        return username;
    }
    
    public void setUserName(String username){
        this.username = username;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public boolean getRemainLogged(){
        return remainlogged;
    }
    
    public void setRemainLogged(boolean remainlogged){
        this.remainlogged = remainlogged;
    }
    

    public String signIn() {
        return "user/personal.xhtml";
        
    }
    
              

}
    
    


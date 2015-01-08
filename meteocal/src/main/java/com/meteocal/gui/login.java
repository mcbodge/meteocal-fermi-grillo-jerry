/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;



import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;


/**
 *
 * @author Jude
 */

@ManagedBean
@SessionScoped
public class login implements Serializable{
    
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
    
//    public void signIn() {
//        if(remainlogged == true){
//        FacesContext.getCurrentInstance().addMessage(null,
//                new FacesMessage("Welcome " + username + " " + password + " " + " You will remain logged in"));
//        
//        
//        }
//        else{
//           FacesContext.getCurrentInstance().addMessage(null,
//                new FacesMessage("Welcome " + username + " " + password + " You will not remain logged in")); 
//        }
//        }
    
    public String signIn() {
        return "secure/personal_page.xhtml";
        
    }
    
     public void currentDate(){
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    System.out.println(dateFormat.format(date));
   }
    
           
}

   



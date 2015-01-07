/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.entity.User;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.logging.Level;
import javax.faces.context.FacesContext;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

/**
 * FROM/TO - B:HomePage
 * TO - B:PersonalPage, E:User
 * 
 * @author Manuel
 */
public class LogInManager{
    
    @Inject
    Principal principal;

    /**
     * If the structure of the input is wrong (empty fields, the password is too short, etc.) 
     * it returns false.
     * 
     * If the structure of the input is correct, it returns true
     * @param u the username
     * @param p the password
     * @return false if the structure of the input is wrong
     */
    public boolean checkLogInFields(String u, String p){
        if(p == null || p.length() < 8 || p.length() > 255 || u == null || u.length() < 4 || u.length() > 15  )
            return false;
        return true;
    }
    
    /**
     * 
     * @return the username of the current user logged in.
     */
    public String getLoggedUserName(){
        return principal.getName();
    }
    
    /**  
     * Encrypt the password.
     * 
     * @param password the password not encypted
     * @return password encrypted
     */
    public static String encryptPassword(String password) {
        String encPass = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, hash);
            encPass = bigInt.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(LogInManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encPass;
    }
}

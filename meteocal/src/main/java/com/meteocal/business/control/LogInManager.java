package com.meteocal.business.control;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

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
     * @return false if the structure of the input is wrong.
     */
    public boolean checkLogInFields(String u, String p){
        
        return !(p == null || p.length() < 8 || u == null || u.length() < 4 || u.length() > 15);
        
    }
    
    /**
     * Gets the username of the current user
     * 
     * @return the username of the current user logged in.
     */
    public String getLoggedUserName(){
        
        return principal.getName();
        
    }
    
    /**  
     * Encrypt the password.
     * 
     * @param password the password not encrypted
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

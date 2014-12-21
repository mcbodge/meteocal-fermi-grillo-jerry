package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
//import org.apache.commons.lang.RandomStringUtils;


/**
 * FROM/TO - B:ProblemsPage
 * TO: - E:User
 * 
 * @author Manuel
 */
public class IssuesDataManager{
    
    EntityManager em;
    EmailManager emailManager;
    
    //TODO RC
    /**
     * Returns true if the email is a valid email of an user of the system. False otherwise. 
     * It also removes spaces from the very start and the very and of the string.
     * 
     * @param e - email of the user that has requested for support
     * @return true if the email is in the DB
     */
    private boolean verifySubmittedData(String e){
        try{
            TypedQuery<User> query;
            query = (TypedQuery<User>) em.createNamedQuery("User.findByEmail").setParameter("email", e);
            if(query.getSingleResult().getEmail()==e){
                return true;
            }
            return false;
        }catch(NoResultException ex){
            return false;
        }
    }
    
    //TODO RC
    /**
     * If the check of the data is correct it sends an email with the requested username, otherwise it does nothing
     * 
     * @param e - email of the user that has requested for support
     */
    public void sendUserName(String e){
        if(verifySubmittedData(e)){
            //query
            TypedQuery<User> query = (TypedQuery<User>) em.createNamedQuery("User.findByEmail").setParameter("email", e);
            String username = query.getSingleResult().getUserName();
            String fullname = query.getSingleResult().getFirstName() + " " + query.getSingleResult().getLastName();

            //email text parts
            String subject = "METEOCAL: username request";
            String body = "METEOCAL ISSUES MANAGER\n\nDear " + fullname + ",\nYour username is:\t" + username + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            emailManager.sendEmail(e, subject, body);
        }
    }
    
    //TODO RC
    /**
     * If the check of the data is correct it sends an email with the requested password, otherwise it does nothing
     * 
     * @param e - email of the user that has requested for support
     */
    public void sendPassword(String e){
        if(verifySubmittedData(e)){
            //query
            TypedQuery<User> query = (TypedQuery<User>) em.createNamedQuery("User.findByEmail").setParameter("email", e);
            String fullname = query.getSingleResult().getFirstName() + " " + query.getSingleResult().getLastName();
            String password = "polpo"; //pass temporanea generata
            //call method that edit the password
            
            //email text parts
            String subject = "METEOCAL: password request";
            String body = "METEOCAL ISSUES MANAGER\n\nDear " + fullname + ",\nYour new temporally password is:\t" + password + "\nPLEASE CHANGE YOUR PASSWORD ASAP.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            emailManager.sendEmail(e, subject, body);
        }
    }
    
}

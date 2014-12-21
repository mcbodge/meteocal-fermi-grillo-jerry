package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.RandomStringUtils;


/**
 * FROM/TO - B:ProblemsPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class IssuesDataManager{
    
    EntityManager em;
    EmailManager emailManager;
    
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
            String subject = "MeteoCal: user name request";
            String body = "Dear " + fullname + ",\nYour username is:\t" + username + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            emailManager.sendEmail(e, subject, body);
        }
    }
    
    //TODO Password generator. :)
    /**
     * If the check of the data is correct it sends an email with the requested password, otherwise it does nothing
     * 
     * @param e - email of the user that has requested for support
     */
    public void sendPassword(String e){
        if(verifySubmittedData(e)){
            //query (it is a User)
            TypedQuery<User> query = (TypedQuery<User>) em.createNamedQuery("User.findByEmail").setParameter("email", e);
            String fullname = query.getSingleResult().getFirstName() + " " + query.getSingleResult().getLastName();
            //generate a new lenght-6 password
            String password = RandomStringUtils.randomAlphanumeric(6);
            //edit user's pasword
            query.getSingleResult().setPassword(password);
            em.merge(query);
            
            //email text parts
            String subject = "MeteoCal: password request";
            String body = "Dear " + fullname + ",\nYour new temporally password is:\t" + password + "\nPLEASE CHANGE YOUR PASSWORD ASAP.\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            emailManager.sendEmail(e, subject, body);
        }
    }
    
}

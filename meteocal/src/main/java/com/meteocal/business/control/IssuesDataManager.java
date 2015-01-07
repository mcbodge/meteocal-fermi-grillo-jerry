package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.RandomStringUtils;


/**
 * FROM/TO - B:ProblemsPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class IssuesDataManager{
    
    @PersistenceContext
    private EntityManager em;
    
    public IssuesDataManager(){}
    
    /**
     * Returns true if the email is a valid email of an user of the system. False otherwise. 
     * It also removes spaces from the very start and the very and of the string.
     * 
     * @param e - email of the user that has requested for support
     * @return true if the email is in the DB
     */
    private boolean verifySubmittedData(String e){
        try{
            User query;
            query = (User) em.createNamedQuery("User.findByEmail").setParameter("email", e).getSingleResult();
            if(query.getEmail().equals(e)){
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
            User query = (User)em.createNamedQuery("User.findByEmail").setParameter("email", e).getSingleResult();
            String username = query.getUserName();
            String fullname = query.getFirstName() + " " + query.getLastName();

            //email text parts
            String subject = "METEOCAL: user name request";
            String body = "Dear " + fullname + ",\nYour username is:\t" + username + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            EmailManager.getInstance().sendEmail(e, subject, body);
        }
    }
    
    
    /**
     * If the check of the data is correct it sends an email with the requested password, otherwise it does nothing
     * 
     * @param e - email of the user that has requested for support
     */
    public void sendPassword(String e){
        if(verifySubmittedData(e)){
            //query (it is a User)
            User query = (User) em.createNamedQuery("User.findByEmail").setParameter("email", e).getSingleResult();
            String fullname = query.getFirstName() + " " + query.getLastName();
            //generate a new lenght-8 password
            String password = RandomStringUtils.randomAlphanumeric(8);
            
            //email text parts
            String subject = "METEOCAL: password request";
            String body = "Dear " + fullname + ",\nYour new temporary password is:\t" + password + "\n\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            EmailManager.getInstance().sendEmail(e, subject, body);
            
            //edit user's pasword
            query.setPassword(password);
            em.merge(query);
        }
    }
    
}

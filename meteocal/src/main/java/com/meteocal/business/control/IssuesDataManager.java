package com.meteocal.business.control;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.entity.User;
import org.apache.commons.lang3.RandomStringUtils;


/**
 * FROM/TO - B:ProblemsPage
 * TO - E:User
 * 
 * @author Manuel
 */
public class IssuesDataManager{
    private EmailManager email_mng = new EmailManager();
    
    public IssuesDataManager(){ 
    }

    /**
     * If the check of the data is correct it sends an email with the requested username, otherwise it does nothing
     * 
     * @param user - the user that has requested support
     */
    public void sendUserName(User user){
        if(user != null){
            String username = user.getUserName();
            String fullname = user.getFirstName() + " " + user.getLastName();
            String email = user.getEmail();
            //email text parts
            String subject = "METEOCAL: user name request";
            String body = "Dear " + fullname + ",\nYour username is:\t" + username + ".\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            email_mng.sendEmail(email, subject, body);
        }
    }
    
    /**
     * If the check of the data is correct it sends an email with the requested password, otherwise it does nothing
     * 
     * @param user - the user that has requested support
     * @return the user with the new password
     */
    public User sendPassword(User user){
        if(user != null){
            String fullname = user.getFirstName() + " " + user.getLastName();
            String email = user.getEmail();
            
            //generate a new lenght-8 password
            String password = RandomStringUtils.randomAlphanumeric(8);
            
            //email text parts
            String subject = "METEOCAL: password request";
            String body = "Dear " + fullname + ",\nYour new temporary password is:\t" + password + "\n\n\nPLEASE DO NOT REPLY TO THIS EMAIL";
            email_mng.sendEmail(email, subject, body);
            
            //edit user's pasword
            user.setPassword(password);
        }
        return user;
    }
    
}

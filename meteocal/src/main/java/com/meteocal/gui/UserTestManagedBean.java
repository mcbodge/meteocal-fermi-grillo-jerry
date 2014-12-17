/*
 * This class si used to test the connection to the db
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.UserTestSessionBean;
import com.meteocal.business.entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Model;

/**
 *
 * @author Francesco
 */
@Named(value = "userTestManagedBean")
@Model
public class UserTestManagedBean {

    /**
     * Creates a new instance of UserTestManagedBean
     */
    public UserTestManagedBean() {
    }
    
    @EJB UserTestSessionBean sb;
    public String allUserNames(){
        String result="";
        List<User> users = sb.findAll();
        
        for (User next : users) {
            result= result + " " + next.getUserName();
        }
        return result;
    }
}

/*
 * This class si used to test the connection to the db
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.IssuesDataManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.Group;
import com.meteocal.business.entity.User;
import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Francesco
 */
@Stateless
public class UserTestSessionBean {
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    IssuesDataManager idm;
    @Inject
    LogInManager lm;
        
    //readFron db
    public List<User> findAll(){
        TypedQuery<User> query;
        query = em.createNamedQuery("User.findAll",User.class);
        return query.getResultList();
    }
  
    /**
     * write in db
     * @param user
     */
    public void saveUser(User user){
        user.setFirstName("bot");
        user.setLastName("bot");
        user.setPassword("qqqqq");
        user.setPublicCalendar(true);
        user.setGroupName(Group.USERS);
        em.persist(user);
    } 
    
    public void issues(String email){
        idm.sendPassword(email);
        idm.sendUserName(email);
    }
    
    //login_mng
    public String loadUser(String un, String p){
        return lm.loadUser(un, p);
    }
    
    public String verifyLogIn(String un, String p){
        if(lm.verifyLogIn(un, p))
            return "true";
        
        return "false";
    }
}

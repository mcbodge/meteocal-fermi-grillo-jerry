/*
 * This class si used to test the connection to the db
 */
package com.meteocal.business.boundary;

import com.meteocal.business.entity.Group;
import com.meteocal.business.entity.User;
import java.util.List;
import javax.ejb.Stateless;
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
    
}

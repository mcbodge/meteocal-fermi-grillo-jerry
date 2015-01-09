/*
 * This class si used to test the connection to the db
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.IssuesDataManager;
import com.meteocal.business.control.LogInManager;
import com.meteocal.business.entity.Group;
import com.meteocal.business.entity.User;
import java.util.List;
import javax.ejb.EJB;
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
            
    //readFrom db
    public List<User> findAll(){
        TypedQuery<User> query;
        query = em.createNamedQuery("User.findAll",User.class);
        return query.getResultList();
    }
   
    
    
}

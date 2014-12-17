/*
 * This class si used to test the connection to the db
 */
package com.meteocal.business.boundary;

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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PersistenceContext(name = "meteocal_PU" )
    EntityManager em;
    
    public List<User> findAll(){
        TypedQuery<User> query;
        query = em.createNamedQuery("User.findAll",User.class);
        return query.getResultList();
    }
    
    
}

package com.meteocal.gui;

import com.meteocal.business.boundary.ManuelTestSessionBean;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.primefaces.json.JSONException;


@Named
@RequestScoped
public class ManuelTestManagedBean {
    
    @EJB 
    private ManuelTestSessionBean sb;

    public ManuelTestManagedBean() {
    }
    
        
    public String getTest() throws JSONException {
        return sb.test();
    }

}

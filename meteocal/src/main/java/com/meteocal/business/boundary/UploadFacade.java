/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.control.LogInManager;
import com.meteocal.business.control.UserCalendarManager;
import com.meteocal.business.entity.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Manuel
 */
@Stateless
public class UploadFacade {
    
    @PersistenceContext(unitName = "meteocal_PU")
    EntityManager em;
    
    @Inject
    UserCalendarManager ucm;
    
    @Inject
    LogInManager lim;
    
    private void setCalendar(File f){
        ucm.startUpload(getUser(lim.getLoggedUserName()), f);
    }
    
    private User getUser(String username) {
        try {
            return em.createNamedQuery("User.findByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }


    public void upload(UploadedFile up) {
        // Do what you want with the file
        try {
            copyFile(Integer.toString(up.hashCode()), up.getInputstream());
        } catch (IOException e) {
        }

    }

    public void copyFile(String hash, InputStream in) {
        try {

            try ( // write the inputStream to a FileOutputStream
                    OutputStream out = new FileOutputStream(new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("files/") + File.separatorChar
                            + hash))) {
                int read;// = 0;
                byte[] bytes = new byte[4096];
                
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                
                in.close();
                out.flush();
            }

        } catch (IOException e) {

        }
    }

    
    
}

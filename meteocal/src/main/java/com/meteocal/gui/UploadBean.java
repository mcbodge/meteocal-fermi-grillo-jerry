/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.UploadFacade;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Manuel
 */

@ManagedBean
public class UploadBean {
    
    @EJB
    UploadFacade uf;

    public UploadBean() {
    }
    
    private UploadedFile file;
 
    //TODO jDoc
    /**
     * 
     * @return 
     */
    public UploadedFile getFile() {
        return file;
    }
 
    //TODO jDoc
    /**
     * 
     * @param file 
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }
     
    //TODO jDoc
    /**
     * 
     * @param f 
     */
    public void upload(FileUploadEvent f) {
        //if(file != null) {
            uf.upload(f.getFile());
        //}
    }


    
    
}

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

    private UploadedFile file;

    public UploadBean() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public void upload(FileUploadEvent f) {
        uf.upload(f.getFile());
    }
    
    public void uploadSingle() {
        if(file!= null){
            uf.upload(file);
        }
    }
     

}

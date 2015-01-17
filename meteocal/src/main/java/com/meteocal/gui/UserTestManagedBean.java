/*
 * This class si used to test the connection to the db
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.HomeFacade;
import com.meteocal.business.boundary.UserTestSessionBean;
import com.meteocal.business.entity.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

/**
 *
 * @author Francesco
 */
@Named
@RequestScoped
public class UserTestManagedBean {

    @EJB
    private UserTestSessionBean sb;

    private User user;
    private String receiver, downloadLink , uplmsg;
    private Part uploadedFile;
    

    public UserTestManagedBean() {
    }
    //<editor-fold defaultstate="state" desc="GETTERS AND SETTERS">

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getUplmsg() {
        return uplmsg;
    }

    public void setUplmsg(String uplmsg) {
        this.uplmsg = uplmsg;
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part file) {
        this.uploadedFile = file;
    }

    //</editor-fold>
    
    public String allUserNames() {
        String result = "";
        List<User> users = sb.findAll();

        for (User next : users) {
            result = result + "\t " + next.getUserName();
        }
        return result;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Just prints out file content
     */
    public void upload() {
        if (null != uploadedFile) {
            try {
                InputStream is = uploadedFile.getInputStream();
                String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files") + File.separatorChar
                        + "TEST-UPLOAD.dat";

                File file = new File(filename);
                if (file.exists() && !file.isDirectory()) {
                    file.delete();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder out = new StringBuilder();
                String line;
                FileWriter w = new FileWriter(file);
                out.append(file.getPath());
                while ((line = reader.readLine()) != null) {
                    //out.append(line);
                    w.write("\n" + line);
                }
                //Prints the string content read from input stream
                uplmsg = "the file is in the folder: " + out.toString() + "\n\nUPLOAD DONE";

                setDownloadLink("files/TEST-UPLOAD.dat");
                reader.close();
                w.close();
            } catch (IOException ex) {
                uplmsg = "UPLOAD FAILED";
            }
        }
    }

    
}

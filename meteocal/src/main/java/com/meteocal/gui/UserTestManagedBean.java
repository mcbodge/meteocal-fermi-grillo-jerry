/*
 * This class si used to test the connection to the db
 */
package com.meteocal.gui;

import com.meteocal.business.boundary.EmailManager;
import com.meteocal.business.boundary.HomeFacade;
import com.meteocal.business.boundary.UserTestSessionBean;
import com.meteocal.business.entity.User;
import com.sun.messaging.jmq.util.log.SysLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.in;
import java.net.URI;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Francesco
 */
@Named
@RequestScoped
public class UserTestManagedBean {
    
    @EJB 
    private UserTestSessionBean sb;
    @EJB
    private HomeFacade hf;
    
    private User user;
    private String receiver;
    private String yem;
    private String lun;
    private String lpw;
    private String lvres;
    private String logres;
    private String downloadLink;
    
    private Part uploadedFile;
    private String uplmsg;
    
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
        
    public String getLogres() {
        return logres;
    }

    public void setLogres(String logres) {
        this.logres = logres;
    }

    public String getLvres() {
        return lvres;
    }

    public void setLvres(String lvres) {
        this.lvres = lvres;
    }

    public String getLun() {
        return lun;
    }

    public void setLun(String lun) {
        this.lun = lun;
    }

    public String getLpw() {
        return lpw;
    }

    public void setLpw(String lpw) {
        this.lpw = lpw;
    }

    public String getYem() {
        return yem;
    }

    public void setYem(String yem) {
        this.yem = yem;
    }
    
    public UserTestManagedBean() {
    }
       
    public String allUserNames(){
        String result="";
        List<User> users = sb.findAll();
        
        for (User next : users) {
            result= result + "\t " + next.getUserName();
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
   
    public String save(){
        sb.saveUser(user);
        return "francescoTEST?faces-redirect=true";
    }
       
    public String sendMail(){
        EmailManager.getInstance().sendEmail(receiver, "METEOCAL: Test", "Hello world. \nThis is a notification.");
        return "francescoTEST?faces-redirect=true";
    }
    
    public String issues(){
        sb.issues(yem);
        return "francescoTEST?faces-redirect=true";
    }
    
    public void login(){
        //logres=sb.loadUser(lun, lpw);
        logres = hf.loadUser(lun, lpw);
    }
       
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        //logger.log(Level.INFO, "User Logged out");
        return "/index?faces-redirect=true";
    }

    
    /**
     * Just prints out file content
     */
    public void upload() {
        if (null != uploadedFile) {
            try {
                InputStream is = uploadedFile.getInputStream();
                String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files/") + File.separatorChar 
                + "TEST-UPLOAD.dat";
                
                File file = new File(filename);
                if(file.exists() && !file.isDirectory()) { 
                    file.delete(); 
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder out = new StringBuilder();
                String line;
                FileWriter w = new FileWriter(file);
                out.append(file.getPath());
                while ((line = reader.readLine()) != null) {
                    //out.append(line);
                    w.write("\n"+line);
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

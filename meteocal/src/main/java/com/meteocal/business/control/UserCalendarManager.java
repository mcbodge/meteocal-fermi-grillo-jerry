package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * FROM/TO - B:PersonalPage, B:UploadPage
 * TO: E:Event, E:User
 * 
 * @author Manuel
 */
public class UserCalendarManager {
    
    @PersistenceContext
    EntityManager em;
    
    private final String EXT = ".bin";
    
    //TODO RC the file contains the name of the owner and a list of eventId(one per line).
    //nb we need to put also the information about the owner of the calendar 
    //(if - for some reason - is tricky, just put its userId as the name of the file).
    // aaa.bin
    /**
     * It start the download of the file by putting it in a temporary folder and redirecting the browser to that path.
     * 
     * @param u the User who will download his own calendar.
     * @return the URL of the file position.
     */
    public String startDownload(User u){
        //create a file in the /files directory 
        String dir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files") + File.separatorChar;
        String filename = u.getUserName()+"_meteocal"+EXT;
        File file = new File(filename);
        FileWriter writer ;
        
        //if file already exist overwrite
        if(file.exists() && !file.isDirectory()) { 
            file.delete(); 
        }
        
        try{
            writer = new FileWriter(dir+filename);
            writer.write(u.getUserId());
            //get all personal public events
            for (Event e : u.getEventCreatedCollection()){
                if(e.isPersonal() && e.isPublicEvent()){
                   //write in file
                   writer.write("\n"+e.getEventId());
                }
            }   
            writer.close();
        }catch(IOException ex){
            
        }        
        return "files/"+filename;
    }
    
    
    //TODO RC
    /**
     * It does all the required procedures to upload a calendar:
     * - upload the file;
     * - verify it;
     * - extract interesting events.
     * 
     * @param u the user whose calendar will be uploaded.
     * @param f the file to be uploaded
     */
    public void startUpload(User u, File f){
        if(verifyFile(f)){
            extractFromFile(f, u);
        }
    }
    
    
    //TODO RC
    /**
     * It verify the consistency of the uploaded file:
     * - verify the correct type/extension;
     * - verify the correct db structure.
     * 
     * @param f the result of the verification (true if the file can be correctly imported)
     * @return 
     */
    private boolean verifyFile(File f){
        double kilobytes = (f.length() / 1024);
        String line;
        InputStream is;
        BufferedReader reader;
        String user_id;
        //size < 2MB
        if(kilobytes < 2048){
            //check extension
            if(f.getName().substring(f.getName().length()-4).equals(EXT)){
                try {
                    is = new FileInputStream(f);
                    reader = new BufferedReader(new InputStreamReader(is));
                    //check user existance.
                    user_id = reader.readLine();
                    em.createNamedQuery("User.findByUserId",User.class).setParameter("userId", Integer.parseInt(user_id)).getSingleResult();
                    while ((line = reader.readLine()) != null) {
                        //check each event if exist have been created from the user_id
                        Event e = null;
                        try {    
                            e = em.createNamedQuery("Event.findByEventId",Event.class).setParameter("eventId", line).getSingleResult();
                        } catch (NoResultException exNoRes){ /*the event doesn't exist*/ }
                        if(e != null && !e.getCreator().getUserId().toString().equals(user_id)){
                            return false;
                        }
                    }
                    //Done with the file
                    reader.close();
                    reader = null;
                    is = null;
                    return true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException exIO) {
                    Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, exIO);
                } catch (NoResultException exNoRes){
                    //user that exported the file doesn't exist
                }
            }
        }
        return false;
    }
    
    
    //TODO RC
    /**
     * It extracts the interesting events from the calendar:
     * If the user who is importing the calendar is the same who has exported it:
     * - Import non-overlapped personal events (without invited people) adding to the event names the string " [Imported]".
     * If the user who is importing the calendar is not the same who has exported it:
     * - Import non-overlapped personal events (without invited people) adding to the event names the string " ["++NAME_OF_THE_FORMER_CREATOR++"]";
     * - Modify the creator as the currently importing user.
     * 
     * @param f the previously imported and verified file.
     * @param u the user who is importing the file.
     */
    private void extractFromFile(File f, User u){
        int user_id;
        InputStream is;
        BufferedReader reader;
        String line;
        try {
            is = new FileInputStream(f);
            reader = new BufferedReader(new InputStreamReader(is));
            //first line = user_id
            user_id = Integer.parseInt(reader.readLine());
            EventCreationManager ecm = new EventCreationManager();
            while ((line = reader.readLine()) != null){
                try{
                    Event e = em.createNamedQuery("Event.findByEventId",Event.class).setParameter("eventId", line).getSingleResult();
                    String eventName;
                    if (user_id == u.getUserId()){
                        eventName = e.getName() + " [Imported]";
                    } else {
                        eventName = e.getName() +" ["+ em.createNamedQuery("User.findByUserId",User.class).setParameter("userId", user_id).getSingleResult().getUserName()+"]";
                    }
                    ecm.newEvent(u, eventName, e.getStart(), e.getEnd(), e.getLocation(), null, true, null, e.getDescription());
                }catch(NoResultException e){
                    //event doesn't exits
                }
            }
            ecm = null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //TODO RC
    /**
     * It changes the privacy setting of the given user (from public to private or vice versa).
     * 
     * @param u the User we want to modify.
     */
    public void togglePrivacy(User u){
        u.setPublicCalendar(!u.getPublicCalendar());
    }
}

package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * FROM/TO - B:PersonalPage, B:UploadPage
 * TO: E:Event, E:User
 * 
 * @author Manuel
 */
public class UserCalendarManager {
    
    @PersistenceContext
    EntityManager em;
    
    //TODO RC the file contains the name of the owner and a list of eventId.
    //nb we need to put also the information about the owner of the calendar 
    //(if - for some reason - is tricky, just put its userId as the name of the file).
    //
    /**
     * It start the download of the file by putting it in a temporary folder and redirecting the browser to that path.
     * 
     * @param u the User who will download his own calendar.
     * @return the URL of the file position.
     */
    public String startDownload(User u){
        //create a file in the /files directory 
        String dir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files") + File.separatorChar;
        String filename = u.getUserName()+"_meteocal.dat";
        File file = new File(filename);
        //if file already exist overwrite
        if(file.exists() && !file.isDirectory()) { 
            file.delete(); 
        }
        FileWriter writer ;
        try{
            writer = new FileWriter(dir+filename);
            writer.write(u.getUserId());
            //get all personal events
            Collection<Event> events = u.getEventCreatedCollection();
            for (Event e : events){
                if(e.isPersonal()&& e.isPublicEvent()){
                   //write in file
                   writer.write("\n"+e.getEventId());
                }
            }   
            writer.close();
        }catch(IOException ex){
            
        }        
        return "files/"+filename;
    }
    
    
    //TODO
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
        
        if(kilobytes < 2048){
            if(f.getName().substring(f.getName().length()-5).equals(".dat")){
                try {
                    is = new FileInputStream(f);
                    reader = new BufferedReader(new InputStreamReader(is));
                    //first line = user_id
                    user_id = reader.readLine();
                    em.createNamedQuery("User.findByUserId",User.class).setParameter("userId", Integer.parseInt(user_id)).getSingleResult();
                    while ((line = reader.readLine()) != null) {
                        //check each event if exist have been created from the user_id
                        Event e = em.createNamedQuery("Event.findByEventId",Event.class).setParameter("eventId", line).getSingleResult();
                        if(!e.getCreator().getUserId().toString().equals(user_id)){
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
                    ecm.newEvent(u, e.getName(), e.getStart(), e.getEnd(), e.getLocation(), null, true, null, e.getDescription());
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

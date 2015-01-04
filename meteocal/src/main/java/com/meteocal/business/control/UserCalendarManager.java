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
    
    //TODO add event infos
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
            writer.write(u.getUserName());
            //get all personal public events
            for (Event e : u.getEventCreatedCollection()){
                if(e.isPersonal() && e.isPublicEvent()){
                   //write in file
                   writer.write("\n"+e.getEventId()+"\t"+e.getName());
                }
            }   
            writer.close();
        }catch(IOException ex){
            
        }        
        return "files/"+filename;
    }
    
    
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
        String user_name;
        //size < 2MB
        if(kilobytes < 2048){
            //check extension
            if(f.getName().substring(f.getName().length()-4).equals(EXT)){
                try {
                    is = new FileInputStream(f);
                    reader = new BufferedReader(new InputStreamReader(is));
                    //check user existance.
                    user_name = reader.readLine();
                    
                    //Done with the file
                    reader.close();
                    //reader = null;
                    //is = null;
                    return true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException exIO) {
                    Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, exIO);
                }
            }
        }
        return false;
    }
    
    
    //TODO 
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
        String username;
        InputStream is;
        BufferedReader reader;
        String line;
        try {
            is = new FileInputStream(f);
            reader = new BufferedReader(new InputStreamReader(is));
            //first line = user_id
            username = reader.readLine();
            EventCreationManager ecm = new EventCreationManager();
            
            while ((line = reader.readLine()) != null){
                //stringtokenizer per info evento
                String eventName;
                if (username.equals(u.getUserName())){
                    eventName = e.getName() + " [Imported]";
                } else {
                    eventName = e.getName() +" ["+ username +"]";
                }
                ecm.newEvent(u, eventName, e.getStart(), e.getEnd(), e.getLocation(), null, true, null, e.getDescription());
                
            }
            //ecm = null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * It changes the privacy setting of the given user (from public to private or vice versa).
     * 
     * @param u the User we want to modify.
     */
    public void togglePrivacy(User u){
        u.setPublicCalendar(!u.getPublicCalendar());
    }
}

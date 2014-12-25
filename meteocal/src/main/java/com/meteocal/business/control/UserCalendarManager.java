package com.meteocal.business.control;

import com.meteocal.business.entity.User;
import java.io.File;

/**
 * FROM/TO - B:PersonalPage, B:UploadPage
 * TO: E:Event, E:User
 * 
 * @author Manuel
 */
public class UserCalendarManager {
    
    //TODO nb we need to put also the information about the owner of the calendar (if - for some reason - is tricky, just put its userId as the name of the file).
    /**
     * It start the download of the file by putting it in a temporary folder and redirecting the browser to that path.
     * 
     * @param u the User who will download his own calendar.
     * @return the URL of the file position.
     */
    public String startDownload(User u){
        return null;
        
    }
    
    
    /**
     * It does all the required procedures to upload a calendar:
     * - upload the file;
     * - verify it;
     * - extract interesting events.
     * 
     * @param u the user whose calendar will be uploaded.
     */
    public void startUpload(User u){
        
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
        return false;
        
    }
    
    
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
        
    }
    
    
    /**
     * It changes the privacy setting of the given user (from public to private or vice versa).
     * 
     * @param u the User we want to modify.
     */
    public void togglePrivacy(User u){
        
    }
}

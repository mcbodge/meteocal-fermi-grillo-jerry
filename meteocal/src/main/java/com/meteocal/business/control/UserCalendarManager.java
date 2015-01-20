package com.meteocal.business.control;

import com.meteocal.business.entity.Event;
import com.meteocal.business.entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.faces.context.FacesContext;

/**
 * FROM/TO - B:PersonalPage, B:UploadPage
 * TO: E:Event, E:User
 * 
 * @author Manuel
 */
public class UserCalendarManager {

    private final String EXT = ".bin";
    

    /**
     * It start the download of the file by putting it in a temporary folder and
     * redirecting the browser to that path.
     *
     * @param u the User who will download his own calendar.
     * @return the URL of the file position.
     */
    public FileInputStream startDownload(User u) throws FileNotFoundException {
        
        //create a file in the /files directory 
        String dir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files/") + File.separatorChar;
        String filename = u.getUserName() + "_meteocal" + EXT;
        File file = new File(dir+filename);
        FileWriter writer;

        //if file already exist overwrite
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }

        try {
            writer = new FileWriter(dir + filename);
            writer.write(u.getUserName());
            
            //get all personal public events
            for (Event e : u.getCreatedEvents()) {
                if (e.isPersonal() && e.isPublicEvent()) {
                    //write in file  eventid   name    location  geoname   start   end   descr   constraint
                    String weather_constraint = null;
                    String geoname = null;
                    
                    if(e.getWeather() != null){
                        weather_constraint = e.getWeather().getConstraint().toString();
                        geoname = e.getWeather().getLocationCode().toString();
                    }
                    
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    writer.write("\n" + e.getEventId().toString() + "\t" + e.getName() + "\t" + e.getLocation() + "\t" + geoname + "\t" + formatter.format(e.getStart()) + "\t" + formatter.format(e.getEnd()) + "\t" + e.getDescription() + "\t" + weather_constraint);
                }
            }
            
            writer.close();
        } catch (IOException ex) {

        }
        
        return new FileInputStream(file);
        
    }

    /**
     * It verify the consistency of the uploaded file: - verify the correct
     * type/extension; - verify the correct db structure. ?! <-- deprecated
     *
     * @param f the result of the verification (true if the file can be
     * correctly imported)
     * @return
     */
    public boolean verifyFile(File f) {
        
        double kilobytes = (f.length() / 1024);

        //check size < 2MB
        if (kilobytes < 2048) {
            //if (f.getName().substring(f.getName().length() - 4).equals(EXT)) {
                System.out.println("file ok");
                return true;
            //}
        }
        
        return false;
        
    }

    


}

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public String startDownload(User u) {
        //create a file in the /files directory 
        String dir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("files") + File.separatorChar;
        String filename = u.getUserName() + "_meteocal" + EXT;
        File file = new File(filename);
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
                    //write in file  eventid   name    location    start   end   descr   constraint
                    writer.write("\n" + e.getEventId().toString() + "\t" + e.getName() + "\t" + e.getLocation() + "\t" + e.getStart().toString() + "\t" + e.getEnd().toString() + "\t" + e.getDescription() + "\t" + e.getWeather().getConstraint().toString());
                }
            }
            writer.close();
        } catch (IOException ex) {

        }
        return "files/" + filename;
    }

    /**
     * It does all the required procedures to upload a calendar: - upload the
     * file; - verify it; - extract interesting events.
     *
     * @param u the user whose calendar will be uploaded.
     * @param f the file to be uploaded
     */
    public void startUpload(User u, File f) {
        if (verifyFile(f)) {
            extractFromFile(f, u);
        }
    }

    /**
     * It verify the consistency of the uploaded file: - verify the correct
     * type/extension; - verify the correct db structure. ?! <-- deprecated
     *
     * @param f the result of the verification (true if the file can be
     * correctly imported)
     * @return
     */
    private boolean verifyFile(File f) {
        double kilobytes = (f.length() / 1024);

        //check size < 2MB
        if (kilobytes < 2048) {
            if (f.getName().substring(f.getName().length() - 4).equals(EXT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It extracts the interesting events from the calendar: If the user who is
     * importing the calendar is the same who has exported it: - Import
     * non-overlapped personal events (without invited people) adding to the
     * event names the string " [Imported]". If the user who is importing the
     * calendar is not the same who has exported it: - Import non-overlapped
     * personal events (without invited people) adding to the event names the
     * string " ["++NAME_OF_THE_FORMER_CREATOR++"]"; - Modify the creator as the
     * currently importing user.
     *
     * @param f the previously imported and verified file.
     * @param u the user who is importing the file.
     */
    private void extractFromFile(File f, User u) {

        InputStream is;
        BufferedReader reader;
        String line;
        String user_file, e_id, e_name, e_str_location, e_str_start, e_str_end, e_descr, e_constraint;
        StringTokenizer token;

        try {
            //open stream
            is = new FileInputStream(f);
            reader = new BufferedReader(new InputStreamReader(is));

            //first line = user_id
            user_file = reader.readLine();

            while ((line = reader.readLine()) != null) {
                //line = eventid   name    location    start   end   descr   constraint
                token = new StringTokenizer(line, "\t");

                e_id = token.nextToken();
                e_name = token.nextToken();
                e_str_location = token.nextToken();
                e_str_start = token.nextToken();
                e_str_end = token.nextToken();
                e_descr = token.nextToken();
                e_constraint = token.nextToken();

                //<editor-fold defaultstate="collapsed" desc="JDoc Date.toString()">
                /**
                 * Converts this Date object to a String of the form: dow mon dd
                 * hh:mm:ss zzz yyyy where: dow is the day of the week (Sun,
                 * Mon, Tue, Wed, Thu, Fri, Sat). mon is the month (Jan, Feb,
                 * Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec). dd is the
                 * day of the month (01 through 31), as two decimal digits. hh
                 * is the hour of the day (00 through 23), as two decimal
                 * digits. mm is the minute within the hour (00 through 59), as
                 * two decimal digits. ss is the second within the minute (00
                 * through 61, as two decimal digits. zzz is the time zone (and
                 * may reflect daylight saving time). Standard time zone
                 * abbreviations include those recognized by the method parse.
                 * If time zone information is not available, then zzz is empty
                 * - that is, it consists of no characters at all. yyyy is the
                 * year, as four decimal digits.
                 *
                 */
                //</editor-fold> 
                
                DateFormat df = new SimpleDateFormat("dow mon dd hh:mm:ss zzz yyyy");
                Date e_start = df.parse(e_str_start);
                Date e_end = df.parse(e_str_end);

                if (user_file.equals(u.getUserName())) {
                    e_name = e_name + " [Imported]";
                } else {
                    e_name = e_name + " [" + user_file + "]";
                }
                //EventCreationManager.getInstance().newEvent(u, e_name, e_start, e_end, e_str_location, null, true, Integer.parseInt(e_constraint), e_descr);
            }

            //close stream
            reader.close();
            is.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(UserCalendarManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}

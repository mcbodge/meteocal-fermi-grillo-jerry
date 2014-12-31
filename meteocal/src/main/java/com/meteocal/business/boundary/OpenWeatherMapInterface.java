package com.meteocal.business.boundary;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;

/**
 *
 * @author Manuel
 */

@Singleton
public class OpenWeatherMapInterface {
     
    static final String url="http://api.openweathermap.org/data/2.5/weather?id=";

    public static String getMessage(Integer geoid){
        
        String result="";

        try {
            URL url_weather = new URL(url + geoid.toString());
 
            HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();
 
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
 
                InputStreamReader inputStreamReader =
                    new InputStreamReader(httpURLConnection.getInputStream());
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192)) {
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        result += line;
                    }
                }
 
            } 
 
        } catch (MalformedURLException ex) {
            Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    
    
}

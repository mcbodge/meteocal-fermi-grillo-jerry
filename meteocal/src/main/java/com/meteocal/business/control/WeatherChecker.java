package com.meteocal.business.control;

import static com.meteocal.business.control.WeatherCondition.*;
import static com.meteocal.business.control.WeatherCondition.Condition.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Manuel
 */


public class WeatherChecker {

    public WeatherChecker() {
    }
     
    /*    private JSONObject parser(String s) throws JSONException{
    JSONObject jsonObject = new JSONObject(s);
    return jsonObject;
    }
    
    public Condition getForecast(Integer geoid){
    
    WeatherCondition forecast = new WeatherCondition();
    forecast.setValue(NA);
    
    //Condition forecast = Condition.NA;
    
    String result="";
    
    try {
    URL url_weather = new URL("http://api.openweathermap.org/data/2.5/weather?id=" + geoid.toString());
    
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
    
    forecast.setValue(parseForecastId(result));
    
    }
    
    } catch (MalformedURLException ex) {
    Logger.getLogger(WeatherChecker.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | JSONException ex) {
    Logger.getLogger(WeatherChecker.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return forecast.getValue();
    }*/
    
    



}

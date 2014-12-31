/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    //<editor-fold defaultstate="collapsed" desc="comment">
    /*public static Condition getValueFromCode(Integer code){
    Condition out;
    
    if(199 < code && code < 233){
    out=Condition.THUNDERSTORM;
    }else if(299 < code && code < 322){
    out=Condition.DRIZZLE;
    }else if(499 < code && code < 505){
    out=Condition.RAIN;
    }else if(519 < code && code < 532){
    out=Condition.SHOWER_RAIN;
    }else if(599 < code && code < 623){
    out=Condition.SNOW;
    }else if(700 < code && code < 781){
    out=Condition.MIST;
    }else switch(code){
    case 511:
    out=Condition.FREEZING_RAIN;
    break;
    case 800:
    out=Condition.CLEAR_SKY;
    break;
    case 801:
    out=Condition.FEW_CLOUDS;
    break;
    case 802:
    out=Condition.SCATTERED_CLOUDS;
    break;
    case 803:
    out=Condition.BROKEN_CLOUDS;
    break;
    case 804:
    out=Condition.OVERCAST_CLOUDS;
    break;
    case 951:
    out=Condition.CALM;
    break;
    case 952: case 953: case 954: case 955: case 956:
    out=Condition.BREEZE;
    break;
    case 957: case 958: case 959:
    out=Condition.GALE;
    break;
    case 960: case 961:
    out=Condition.STORM;
    break;
    case 962: case 902:
    out=Condition.HURRICANE;
    break;
    case 900: case 781:
    out=Condition.TORNADO;
    break;
    case 901:
    out=Condition.TROPICAL_STORM;
    break;
    case 903:
    out=Condition.EXTREME_COLD;
    break;
    case 904:
    out=Condition.EXTREME_HOT;
    break;
    case 905:
    out=Condition.EXTREME_WINDY;
    break;
    case 906:
    out=Condition.EXTREME_HAIL;
    break;
    default:
    out=Condition.NA;
    break;
    }
    return out;
    }
    
    public static WeatherCondition.Condition getForecast(Integer geoid){
    
    Condition c = Condition.NA;
    
    /*WeatherCondition forecast = new WeatherCondition();
    forecast.setValue(Condition.NA);
    
    //Condition forecast = Condition.NA;
    
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
    
    c=getValueFromCode(parseForecastId(result));
    
    }
    
    } catch (MalformedURLException ex) {
    Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | JSONException ex) {
    Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return c;
    }
    
    private static Integer parseForecastId(String json) throws JSONException{
    
    Integer result_id = null;
    
    JSONObject jsonObject = new JSONObject(json);
    
    JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");
    
    if(JSONArray_weather.length() > 0){
    JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
    result_id = JSONObject_weather.getInt("id");
    }
    
    return result_id;
    
    }*/

    
    
    
    public static String getMessage(Integer geoid){
       
        
        /*WeatherCondition forecast = new WeatherCondition();
        forecast.setValue(Condition.NA);*/
        
        //Condition forecast = Condition.NA;
        
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
                
                return result;
 
            } 
 
        } catch (MalformedURLException ex) {
            Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenWeatherMapInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    
    
}

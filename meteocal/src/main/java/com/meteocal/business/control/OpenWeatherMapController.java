/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.control;

import com.meteocal.business.boundary.OpenWeatherMapInterface;
import com.meteocal.business.control.WeatherCondition.Condition;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Manuel
 */
public class OpenWeatherMapController {
    
    /*
    
    public static Condition getValueFromCode(Integer code){
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
    
    private static Integer parseForecastId(String json) throws JSONException{
    
    Integer result_id = null;
    
    JSONObject jsonObject = new JSONObject(json);
    
    JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");
    
    if(JSONArray_weather.length() > 0){
    JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
    result_id = JSONObject_weather.getInt("id");
    }
    
    return result_id;
    
    }
    
    
    public String sayForecast(Integer geoid) throws JSONException{
        return getValueFromCode(parseForecastId(OpenWeatherMapInterface.getMessage(geoid))).toString();
    }
    
    */
    
    public String sayForecast(Integer geoid){
        return "Blind test";
    }
  
}

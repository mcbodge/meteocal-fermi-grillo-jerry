package com.meteocal.business.control;

import com.meteocal.business.boundary.OpenWeatherMapInterface;
import com.meteocal.business.entity.WeatherCondition;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Manuel
 */
public class OpenWeatherMapController {
    
    public static WeatherCondition getValueFromCode(Integer code){
    WeatherCondition out;
    
        if (199 < code && code < 233) {
            out = WeatherCondition.THUNDERSTORM;
        } else if (299 < code && code < 322) {
            out = WeatherCondition.DRIZZLE;
        } else if (499 < code && code < 505) {
            out = WeatherCondition.RAIN;
        } else if (519 < code && code < 532) {
            out = WeatherCondition.SHOWER_RAIN;
        } else if (599 < code && code < 623) {
            out = WeatherCondition.SNOW;
        } else if (700 < code && code < 781) {
            out = WeatherCondition.MIST;
        } else {
            switch (code) {
                case 511:
                    out = WeatherCondition.FREEZING_RAIN;
                    break;
                case 800:
                    out = WeatherCondition.CLEAR_SKY;
                    break;
                case 801:
                    out = WeatherCondition.FEW_CLOUDS;
                    break;
                case 802:
                    out = WeatherCondition.SCATTERED_CLOUDS;
                    break;
                case 803:
                    out = WeatherCondition.BROKEN_CLOUDS;
                    break;
                case 804:
                    out = WeatherCondition.OVERCAST_CLOUDS;
                    break;
                case 951:
                    out = WeatherCondition.CALM;
                    break;
                case 952:
                case 953:
                case 954:
                case 955:
                case 956:
                    out = WeatherCondition.BREEZE;
                    break;
                case 957:
                case 958:
                case 959:
                    out = WeatherCondition.GALE;
                    break;
                case 960:
                case 961:
                    out = WeatherCondition.STORM;
                    break;
                case 962:
                case 902:
                    out = WeatherCondition.HURRICANE;
                    break;
                case 900:
                case 781:
                    out = WeatherCondition.TORNADO;
                    break;
                case 901:
                    out = WeatherCondition.TROPICAL_STORM;
                    break;
                case 903:
                    out = WeatherCondition.EXTREME_COLD;
                    break;
                case 904:
                    out = WeatherCondition.EXTREME_HOT;
                    break;
                case 905:
                    out = WeatherCondition.EXTREME_WINDY;
                    break;
                case 906:
                    out = WeatherCondition.EXTREME_HAIL;
                    break;
                default:
                    out = WeatherCondition.NA;
                    break;
            }
        }
        return out;
    }
    
    
    public static Integer parseForecastId(String json) throws JSONException {

        Integer result_id = null;

        JSONObject jsonObject = new JSONObject(json);

        JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");

        if (JSONArray_weather.length() > 0) {
            JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
            result_id = JSONObject_weather.getInt("id");
        }

        return result_id;

    }
    
    
    public String sayForecast(Integer geoid) throws JSONException{
        return  getValueFromCode(parseForecastId(OpenWeatherMapInterface.getMessage(geoid))).toString();
    }
      
}

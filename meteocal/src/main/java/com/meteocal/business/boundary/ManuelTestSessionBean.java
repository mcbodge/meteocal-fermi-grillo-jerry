package com.meteocal.business.boundary;

import com.meteocal.business.control.OpenWeatherMapController;
import com.meteocal.business.control.WeatherCondition;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.primefaces.json.JSONException;

@Stateless
public class ManuelTestSessionBean {
    
    @EJB
    OpenWeatherMapController wc;

    
    public String test(){
        return "lolol"; //wc.sayForecast(3176959);
    }
}


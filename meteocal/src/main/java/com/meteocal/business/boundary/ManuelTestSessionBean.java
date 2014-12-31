package com.meteocal.business.boundary;

import com.meteocal.business.control.OpenWeatherMapController;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.primefaces.json.JSONException;

@Stateless
public class ManuelTestSessionBean {
    
    @Inject
    OpenWeatherMapController wc;

    public String test() throws JSONException{
        return wc.sayForecast(292223);
    }
}


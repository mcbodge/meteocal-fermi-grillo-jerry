/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.meteocal.business.entity.WeatherCondition;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Manuel
 */
//@Path("weather")
public class WeatherResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WeatherCondition forecast() {
        return null;
        
    }
}

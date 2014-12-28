/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Manuel
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WeatherCondition{

    public enum Condition{
        CLEAR_SKY, FEW_CLOUDS, SCATTERED_CLOUDS, BROKEN_CLOUDS, DRIZZLE, RAIN, SHOWER_RAIN, THUNDERSTORM, SNOW, MIST, 
        CALM, BREEZE, GALE, STORM, HURRICANE, TORNADO, TROPICAL_STORM, COLD, HOT, WINDY, HAIL, 
        NA
    }
    
    private Condition value;

    public Condition getValue() {
        return value;
    }
    
    public Integer id;
    
    
    public void setFromForecastCode(Integer code){
        if(199 < code && code < 233){
            value=Condition.THUNDERSTORM;
        }else if(299 < code && code < 322){
            value=Condition.DRIZZLE;
        }else if(499 < code && code < 505){
            value=Condition.RAIN;
        }else if(519 < code && code < 532){
            value=Condition.SHOWER_RAIN;
        }else if(599 < code && code < 623){
            value=Condition.SNOW;
        }else if(700 < code && code < 782){
            value=Condition.MIST;
        }else switch(code){
            //TODO
        }
 
        
    }
   
}

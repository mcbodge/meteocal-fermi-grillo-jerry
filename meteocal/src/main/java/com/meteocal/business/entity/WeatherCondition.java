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
    
    private Condition value;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WeatherCondition(Condition value) {
        this.value = value;
    }
    
    public WeatherCondition(){
        this.value = Condition.NA;
    }

    public enum Condition{
        CLEAR_SKY, FEW_CLOUDS, SCATTERED_CLOUDS, BROKEN_CLOUDS, OVERCAST_CLOUDS, DRIZZLE, RAIN, FREEZING_RAIN, SHOWER_RAIN, THUNDERSTORM, SNOW, MIST, 
        CALM, BREEZE, GALE, STORM, HURRICANE, TORNADO, TROPICAL_STORM, EXTREME_COLD, EXTREME_HOT, EXTREME_WINDY, EXTREME_HAIL, 
        NA
    }
    

    
    

    public void setValue(Condition value) {
        this.value = value;
    }

    public Condition getValue() {
        return value;
    }
    
    private Condition getValueFromCode(Integer code){
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
    
    public void setValue(Integer code){
        value=getValueFromCode(code);            
    }
    
    public boolean isExtreme(){
        
        boolean out;
        
        switch(value){
            case HURRICANE:
            case TORNADO:
            case TROPICAL_STORM:
            case EXTREME_COLD:
            case EXTREME_HOT:
            case EXTREME_WINDY:
            case EXTREME_HAIL:
                out=true;
                break;
            default:
                out=false;
                break;
 
        }
        
        return out;
   
    }
    
    public boolean isRain(){
        
        boolean out;
        
        switch(value){
            case RAIN:
            case FREEZING_RAIN:
            case SHOWER_RAIN:
                out=true;
                break;
            default:
                out=false;
        }
        
        return out;
    }
    
    public boolean isCloudy(){
        
        boolean out;
        
        switch(value){
            case FEW_CLOUDS:
            case SCATTERED_CLOUDS:
            case BROKEN_CLOUDS:
            case OVERCAST_CLOUDS:
                out=true;
                break;
            default:
                out=false;
                break;
        }
        
        return out;
        
    }
    
    public boolean isAdditional(){
        boolean out;
        
        switch(value){

            case CALM:
            case BREEZE:
            case GALE:
            case STORM:
            case HURRICANE:
                out=true;
                break;
            default:
                out=false;
                break;
        }
        
        return out;       
    }
    
    public boolean isSpecial(){
        
        return isExtreme() || isAdditional() || value==Condition.NA;

    }
    
    public boolean isAvailable(){
        
        return value!=Condition.NA;
        
    }
}

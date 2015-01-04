/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

/**
 *
 * @author Manuel
 */
public enum WeatherCondition {

    CLEAR_SKY, FEW_CLOUDS, SCATTERED_CLOUDS, BROKEN_CLOUDS, OVERCAST_CLOUDS, DRIZZLE, RAIN, FREEZING_RAIN, SHOWER_RAIN, THUNDERSTORM, SNOW, MIST,
    CALM, BREEZE, GALE, STORM, HURRICANE, TORNADO, TROPICAL_STORM, EXTREME_COLD, EXTREME_HOT, EXTREME_WINDY, EXTREME_HAIL,
    NA;

    public boolean isExtreme() {

        boolean out;

        switch (this) {
            case HURRICANE:
            case TORNADO:
            case TROPICAL_STORM:
            case EXTREME_COLD:
            case EXTREME_HOT:
            case EXTREME_WINDY:
            case EXTREME_HAIL:
                out = true;
                break;
            default:
                out = false;

        }

        return out;

    }

    public boolean isRain() {

        boolean out;

        switch (this) {
            case RAIN:
            case FREEZING_RAIN:
            case SHOWER_RAIN:
                out = true;
                break;
            default:
                out = false;
        }

        return out;
    }

    public boolean isCloudy() {

        boolean out;

        switch (this) {
            case FEW_CLOUDS:
            case SCATTERED_CLOUDS:
            case BROKEN_CLOUDS:
            case OVERCAST_CLOUDS:
                out = true;
                break;
            default:
                out = false;
        }

        return out;

    }

    public boolean isAdditional() {
        boolean out;

        switch (this) {

            case CALM:
            case BREEZE:
            case GALE:
            case STORM:
            case HURRICANE:
                out = true;
                break;
            default:
                out = false;
        }

        return out;
    }

    public boolean isSpecial() {

        return isExtreme() || isAdditional() || this == NA;

    }

    public boolean isAvailable() {

        return this != NA;

    }

    @Override
    public String toString() {
        
        String value;
        
        switch (this) {
            case CLEAR_SKY:
                value = "Clear sky";
                break;
            case FEW_CLOUDS:
                value = "Clouds (few)";
                break;
            case SCATTERED_CLOUDS:
                value = "Clouds (scattered)";
                break;
            case BROKEN_CLOUDS:
                value = "Clouds (broken)";
                break;
            case OVERCAST_CLOUDS:
                value = "Clouds (overcast)";
                break;
            case DRIZZLE:
                value = "Drizzle";
                break;
            case RAIN:
                value = "Rain";
                break;
            case FREEZING_RAIN:
                value = "Rain (freezing)";
                break;
            case SHOWER_RAIN:
                value = "Rain (shower)";
                break;
            case THUNDERSTORM:
                value = "Thunderstorm";
                break;
            case SNOW:
                value = "Snow";
                break;
            case MIST:
                value = "Mist";
                break;
            case CALM:
                value = "Calm";
                break;
            case BREEZE:
                value = "Breeze";
                break;
            case GALE:
                value = "Gale";
                break;
            case STORM:
                value = "Storm";
                break;
            case HURRICANE:
                value = "Hurricane";
                break;
            case TORNADO:
                value = "Tornado";
                break;
            case TROPICAL_STORM:
                value = "Tropical storm";
                break;
            case EXTREME_COLD:
                value = "Cold (extreme)";
                break;
            case EXTREME_HOT:
                value = "Hot(extreme)";
                break;
            case EXTREME_WINDY:
                value = "Windy (extreme)";
                break;
            case EXTREME_HAIL:
                value = "Hail (extreme)";
                break;
            default:
                value = "Not available :(";

        }

        return value;

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Francesco
 */
@Entity
@Table(name = "weather", catalog = "meteocaldb", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Weather.findAll", query = "SELECT w FROM Weather w"),
    @NamedQuery(name = "Weather.findByEventId", query = "SELECT w FROM Weather w WHERE w.eventId = :eventId"),
    @NamedQuery(name = "Weather.findByConstraint", query = "SELECT w FROM Weather w WHERE w.constraint = :constraint"),
    @NamedQuery(name = "Weather.findByForecast", query = "SELECT w FROM Weather w WHERE w.forecast = :forecast"),
    @NamedQuery(name = "Weather.findByLastUpdate", query = "SELECT w FROM Weather w WHERE w.lastUpdate = :lastUpdate"),
    @NamedQuery(name = "Weather.findByLocationCode", query = "SELECT w FROM Weather w WHERE w.locationCode = :locationCode")})
public class Weather implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    @Column(name = "constraint")
    private Integer constraint;
    @Column(name = "forecast")
    private Integer forecast;
    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Size(max = 45)
    @Column(name = "location_code", length = 45, nullable = false)
    private String locationCode;
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Event event;

    public Weather() {
    }

    public Weather(Integer eventId, String locationCode) {
        this.eventId = eventId;
        this.locationCode = locationCode;
    }
    
    public Weather(Integer eventId, Integer locationCode) {
        this.eventId = eventId;
        this.locationCode = locationCode.toString();
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getConstraint() {
        return constraint;
    }

    public void setConstraint(Integer constraint) {
        this.constraint = constraint;
    }

    public Integer getForecast() {
        return forecast;
    }

    public void setForecast(Integer forecast) {
        this.forecast = forecast;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Weather)) {
            return false;
        }
        Weather other = (Weather) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.business.entity.Weather[ eventId=" + eventId + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "events", catalog = "meteocaldb", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByEventId", query = "SELECT e FROM Event e WHERE e.eventId = :eventId"),
    @NamedQuery(name = "Event.findByName", query = "SELECT e FROM Event e WHERE e.name = :name"),
    @NamedQuery(name = "Event.findByLocation", query = "SELECT e FROM Event e WHERE e.location = :location"),
    @NamedQuery(name = "Event.findByStart", query = "SELECT e FROM Event e WHERE e.start = :start"),
    @NamedQuery(name = "Event.findByEnd", query = "SELECT e FROM Event e WHERE e.end = :end"),
    @NamedQuery(name = "Event.findByPublic1", query = "SELECT e FROM Event e WHERE e.public1 = :public1"),
    @NamedQuery(name = "Event.findByDescription", query = "SELECT e FROM Event e WHERE e.description = :description"),
    @NamedQuery(name = "Event.findByPersonal", query = "SELECT e FROM Event e WHERE e.personal = :personal")})
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    @Basic(optional = false)
    @NotNull
    @Column(name = "public", nullable = false)
    private boolean public1;
    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "personal", nullable = false)
    private boolean personal;

    public Event() {
    }

    public Event(Integer eventId) {
        this.eventId = eventId;
    }

    public Event(Integer eventId, String name, Date start, Date end, boolean public1, boolean personal) {
        this.eventId = eventId;
        this.name = name;
        this.start = start;
        this.end = end;
        this.public1 = public1;
        this.personal = personal;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean getPublic1() {
        return public1;
    }

    public void setPublic1(boolean public1) {
        this.public1 = public1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
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
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.entity.Event[ eventId=" + eventId + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Francesco
 */
@Entity
@Table(name = "information", catalog = "meteocaldb", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Information.findAll", query = "SELECT i FROM Information i"),
    @NamedQuery(name = "Information.findByInformationId", query = "SELECT i FROM Information i WHERE i.informationId = :informationId"),
    @NamedQuery(name = "Information.findByText", query = "SELECT i FROM Information i WHERE i.text = :text")
})
public class Information implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "information_id", nullable = false)
    private Integer informationId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "text", nullable = false, length = 255)
    private String text;
    
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    @ManyToOne
    private Event eventId;
    
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private User userId;

    public Information() {
    }
    /* we should remove this constructor (autoincrement id)
    public Information(Integer informationId) {
        this.informationId = informationId;
    }
    */
    public Information(/*Integer informationId,*/ User userId, String text) {
        //this.informationId = informationId;
        this.userId = userId;
        this.text = text;
    }
    public Information(/*Integer informationId,*/ Event eventId, User userId, String text) {
        //this.informationId = informationId;
        this.userId = userId;
        this.eventId = eventId;
        this.text = text;
    }

    public Integer getInformationId() {
        return informationId;
    }
    /*
    public void setInformationId(Integer informationId) {
        this.informationId = informationId;
    }
    */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (informationId != null ? informationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Information)) {
            return false;
        }
        Information other = (Information) object;
        if ((this.informationId == null && other.informationId != null) || (this.informationId != null && !this.informationId.equals(other.informationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.business.entity.Information[ informationId=" + informationId + " ]";
    }
    
    // <editor-fold desc="Entity properties (Local information notification)">
    
    
    
    // </editor-fold>
    
}

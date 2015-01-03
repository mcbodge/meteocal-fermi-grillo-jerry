/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Francesco
 */
@Embeddable
public class AnswerPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_id", nullable = false)
    private int eventId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private int userId;

    public AnswerPK() {
    }

    public AnswerPK(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) eventId;
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnswerPK)) {
            return false;
        }
        AnswerPK other = (AnswerPK) object;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.business.entity.AnswerPK[ eventId=" + eventId + ", userId=" + userId + " ]";
    }
    
}

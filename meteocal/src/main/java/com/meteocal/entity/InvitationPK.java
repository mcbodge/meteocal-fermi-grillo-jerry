/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.entity;

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
class InvitationPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_id", nullable = false)
    private int eventId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private int userId;

    public InvitationPK() {
    }

    public InvitationPK(int eventId, int userId) {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvitationPK)) {
            return false;
        }
        InvitationPK other = (InvitationPK) object;
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
        return "com.meteocal.entity.InvitationPK[ eventId=" + eventId + ", userId=" + userId + " ]";
    }
    
}

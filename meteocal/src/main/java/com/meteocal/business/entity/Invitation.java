/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.PrimaryKey;

/**
 *
 * @author Francesco
 */
@Entity
@Table(name = "invitations", catalog = "meteocaldb", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invitations.findAll", query = "SELECT i FROM Invitations i"),
    @NamedQuery(name = "Invitations.findByEventId", query = "SELECT i FROM Answers i WHERE i.invitationsPK.eventId = :eventId"),
    @NamedQuery(name = "Invitations.findByUserId", query = "SELECT i FROM Answers i WHERE i.invitationsPK.userId = :userId"),
})
public class Invitation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InvitationPK invitationPK;
    
    public Invitation() {
    }

    public Invitation(InvitationPK invitationPK) {
        this.invitationPK = invitationPK;
    }

    public Invitation(int eventId, int userId) {
        this.invitationPK = new InvitationPK(eventId, userId);
    }

    public InvitationPK getInvitationPK() {
        return invitationPK;
    }

    public void setInvitationPK(InvitationPK invitationPK) {
        this.invitationPK = invitationPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invitationPK != null ? invitationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invitation)) {
            return false;
        }
        Invitation other = (Invitation) object;
        if ((this.invitationPK == null && other.invitationPK != null) || 
                (this.invitationPK != null && !this.invitationPK.equals(other.invitationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.entity.Invitation[ invitationPK=" + invitationPK + " ]";
    }
    
}

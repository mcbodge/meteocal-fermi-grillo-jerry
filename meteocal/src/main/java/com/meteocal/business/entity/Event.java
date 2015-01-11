/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
    @NamedQuery(name = "Event.findByPublicEvent", query = "SELECT e FROM Event e WHERE e.publicEvent = :publicEvent"),
    @NamedQuery(name = "Event.findByDescription", query = "SELECT e FROM Event e WHERE e.description = :description"),
    @NamedQuery(name = "Event.findByPersonal", query = "SELECT e FROM Event e WHERE e.personal = :personal")
})
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
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Basic(optional = false)
    @NotNull
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Basic(optional = false)
    @NotNull
    @Column(name = "public_event", nullable = false)
    private boolean publicEvent;

    @Size(max = 255)
    @Column(name = "description", length = 255, columnDefinition = "varchar(255) default 'null'")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "personal", nullable = false)
    private boolean personal;

    @JoinTable(name = "invitations", joinColumns = {
        @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)})
    @ManyToMany
    private Collection<User> invitedUserCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Collection<Answer> answerCollection;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "event")
    private Weather weather;

    @OneToMany(mappedBy = "eventId")
    private Collection<Information> informationCollection;

    @JoinColumn(name = "creator", referencedColumnName = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private User creator;

    public Event() {
    }
    
    public Event(User creator, String name, String location, Date start, Date end, boolean publicEvent) {
        this.name = name;
        this.location = location;
        this.start = start;
        this.end = end;
        this.publicEvent = publicEvent;
        this.creator = creator;
        this.description = null;
        this.personal = true;
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

    /**
     *
     * @deprecated
     */
    public boolean getPublicEvent() {
        return publicEvent;
    }

    public boolean isPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return @deprecated
     */
    public boolean getPersonal() {
        return personal;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    @XmlTransient
    /**
     * @deprecated
     */
    public Collection<User> getInvitedUserCollection() {
        return invitedUserCollection;
    }

    /**
     * @deprecated @param userCollection
     */
    public void setInvitedUserCollection(Collection<User> userCollection) {
        this.invitedUserCollection = userCollection;
    }

    @XmlTransient
    /**
     * @deprecated
     */
    public Collection<Answer> getAnswerCollection() {
        return answerCollection;
    }

    /**
     * @deprecated @param answerCollection
     */
    public void setAnswerCollection(Collection<Answer> answerCollection) {
        this.answerCollection = answerCollection;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    /**
     * @deprecated @return
     */
    @XmlTransient
    public Collection<Information> getInformationCollection() {
        return informationCollection;
    }

    /**
     * @deprecated @param informationCollection
     */
    public void setInformationCollection(Collection<Information> informationCollection) {
        this.informationCollection = informationCollection;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
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
        return "com.meteocal.business.entity.Event[ eventId=" + eventId + " ]";
    }

    // <editor-fold desc="Entity properties (Event)">
    //get the constraint code for this event from the Weather table. Null if it doesn't exist
    public Integer getConstraint() {
        
        Integer out=null;
        if (weather!= null)
            out=weather.getConstraint();
        return out;

    }

    //get the weather code for this event from the Weather table
    public Integer getForecast() {
        
        Integer out=null;
        if (weather != null)
            out=weather.getConstraint();
        return out;

    }

    /**
     * All the users that has answer=1 for this event
     *
     * @return empty list if no one attends
     */
    public List<User> getAttendee() {
        List<User> list = new ArrayList<>();
        for (Iterator<Answer> it = this.answerCollection.iterator(); it.hasNext();) {
            Answer ans = it.next();
            if (ans.getValue()) {
                list.add(ans.getUser());
            }
        }
        return list;

    }
    
    /**
     * All the users that have been invited to the event
     *
     * @return empty list if no one have been invited
     */
    public List<User> getMaybeGoing() {
        List<User> list = new ArrayList<>();
        for (Iterator<User> it = this.invitedUserCollection.iterator(); it.hasNext();) {
            User u = it.next();
            list.add(u);
        }
        return list;

    }

    /**
     * All the users that has answer=0 for this event
     *
     * @return
     */
    public List<User> getDeclined() {
        List<User> list = new ArrayList<>();
        for (Iterator<Answer> it = this.answerCollection.iterator(); it.hasNext();) {
            Answer ans = it.next();
            if (!ans.getValue()) {
                list.add(ans.getUser());
            }
        }
        return list;
    }
    
    public void addInvitation(User u){
        this.invitedUserCollection.add(u);
        
                    //u.getEventInvitationCollection().add(e);
    }
    
    public List<User> getRelated(){
        List<User> list = new ArrayList<>();
        list.add(creator);
        for (Iterator<Answer> it = this.answerCollection.iterator(); it.hasNext();) {
            Answer ans = it.next();
                list.add(ans.getUser());
        }
        for (Iterator<User> it = this.invitedUserCollection.iterator(); it.hasNext();) {
            User u = it.next();
            list.add(u);
        }
        return list;
    }

    // </editor-fold>
    // <editor-fold desc="Entity properties (Invitation notification)">
    //if necessary, move in control (event manager)

    /*    public Integer loadOptions(User u){
     return null;
    
     } */
    // </editor-fold>
    
    
}

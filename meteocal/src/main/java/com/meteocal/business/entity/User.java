/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.entity;

import com.meteocal.business.boundary.PersonalFacade;
import com.meteocal.business.control.LogInManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Francesco
 */
@Entity
@Table(name = "users", catalog = "meteocaldb", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_name"}),
    @UniqueConstraint(columnNames = {"email"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUserId", query = "SELECT u FROM User u WHERE u.userId = :userId"),
    @NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByPublicCalendar", query = "SELECT u FROM User u WHERE u.publicCalendar = :publicCalendar")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "user_name", nullable = false, length = 15)
    private String userName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Invalid email")
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Basic(optional = false)
    @NotNull
    @Column(name = "public_calendar", nullable = false)
    private boolean publicCalendar;

    @Column(name = "groupname")
    private String groupname;

    @ManyToMany(mappedBy = "invitedUserCollection")
    private Collection<Event> eventInvitationCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Answer> answerCollection;

    @OneToMany(mappedBy = "userId")
    private Collection<Information> informationCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator")
    private Collection<Event> eventCreatedCollection;

    public User() {
    }

    public User(String userName, String firstName, String lastName, String email, String password, boolean publicCalendar) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = LogInManager.encryptPassword(password);
        this.publicCalendar = publicCalendar;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = LogInManager.encryptPassword(password);
    }

    /**
     *
     * @deprecated use isPublicCalendar
     */
    public boolean getPublicCalendar() {
        return publicCalendar;
    }

    public boolean isPublicCalendar() {
        return publicCalendar;
    }

    public void setPublicCalendar(boolean publicCalendar) {
        this.publicCalendar = publicCalendar;
    }

    public void setGroupName(String groupName) {
        this.groupname = groupName;
    }

    public String getGroupName() {
        return groupname;
    }

    /**
     * @deprecated @return
     */
    @XmlTransient
    public Collection<Event> getEventInvitationCollection() {
        return eventInvitationCollection;
    }

    /**
     * @deprecated @param eventInvitationCollection
     */
    public void setEventInvitationCollection(Collection<Event> eventInvitationCollection) {
        this.eventInvitationCollection = eventInvitationCollection;
    }

    @XmlTransient
    public Collection<Answer> getAnswerCollection() {
        return answerCollection;
    }

    public void setAnswerCollection(Collection<Answer> answerCollection) {
        this.answerCollection = answerCollection;
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

    /**
     * @deprecated @return
     */
    @XmlTransient
    public Collection<Event> getEventCreatedCollection() {
        return eventCreatedCollection;
    }

    /**
     * @deprecated @param eventCreatedCollection
     */
    public void setEventCreatedCollection(Collection<Event> eventCreatedCollection) {
        this.eventCreatedCollection = eventCreatedCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + userName + ")";
    }

    // <editor-fold desc="Entity properties (User)">
    //
    /**
     * Returns all the events that the user is attending, including the ones
     * created by him
     *
     * @return
     */
    public List<Event> getEvents() {
        Logger.getLogger(User.class.getName()).log(Level.INFO, "---- START User.getEvents() -----------");

        List<Event> list = new ArrayList<>();
        for (Iterator<Event> it = this.eventCreatedCollection.iterator(); it.hasNext();) {
            Event e = it.next();
            list.add(e);
        }
        Logger.getLogger(User.class.getName()).log(Level.INFO, "----| list = {0}", list.toString());
        for (Iterator<Answer> it = this.answerCollection.iterator(); it.hasNext();) {
            Answer ans = it.next();
            Logger.getLogger(User.class.getName()).log(Level.INFO, "----| Answer : event_id={0} user_id={1} value={0}}", new Object[]{ans.getEvent().getEventId().toString(), ans.getUser().getUserName(), ans.getValue()});
            if (ans.getUser().equals(this) && ans.getValue()) {
                Logger.getLogger(User.class.getName()).log(Level.INFO, "----| Answer added");
                list.add(ans.getEvent());
            }
        }
        Logger.getLogger(User.class.getName()).log(Level.INFO, "---- STOP User.getEvents() -----------");
        return list;
    }

    public List<Event> getEvents(Date from, Date to) {
        Logger.getLogger(User.class.getName()).log(Level.INFO, "---- START User.getEvents() -----------");

        List<Event> list = new ArrayList<>();
        for (Iterator<Event> it = this.eventCreatedCollection.iterator(); it.hasNext();) {
            Event e = it.next();
            if (from.before(e.getStart()) && to.after(e.getEnd())) {
                list.add(e);
            }
        }
        Logger.getLogger(User.class.getName()).log(Level.INFO, "----| list = {0}", list.toString());
        for (Iterator<Answer> it = this.answerCollection.iterator(); it.hasNext();) {
            Answer ans = it.next();
            Event e = ans.getEvent();
            Logger.getLogger(User.class.getName()).log(Level.INFO, "----| Answer : event_id={0} user_id={1} value={0}}", new Object[]{ans.getEvent().getEventId().toString(), ans.getUser().getUserName(), ans.getValue()});
            if (ans.getUser().equals(this) && ans.getValue() && from.before(e.getStart()) && to.after(e.getEnd())) {
                Logger.getLogger(User.class.getName()).log(Level.INFO, "----| Answer added");
                list.add(e);
            }
            Logger.getLogger(User.class.getName()).log(Level.INFO, "----| Answer NOT added");
        }
        Logger.getLogger(User.class.getName()).log(Level.INFO, "---- STOP User.getEvents() -----------");
        return list;
    }

    /**
     * Returns all the events with invitations for this user
     *
     * @return
     */
    public List<Event> getInvitations() {
        List<Event> list = new ArrayList<>();
        for (Iterator<Event> it = this.eventInvitationCollection.iterator(); it.hasNext();) {
            Event e = it.next();
            list.add(e);
        }
        return list;
    }

    /**
     * Returns all the information "sent" to this user
     *
     * @return
     */
    public List<Information> getInformations() {
        List<Information> list = new ArrayList<>();
        for (Information inf : this.informationCollection) {
            list.add(inf);
        }
        return list;
    }

    public List<Event> getCreatedEvents() {
        List<Event> list = new ArrayList<>();
        for (Event e : this.eventCreatedCollection) {
            list.add(e);
        }
        return list;
    }

    //+ other gets, to use Lists instead of collections in controls or boudaries -- if needed.
    void addEventInvitation(Event e) {
        this.eventInvitationCollection.add(e);
    }
    // </editor-fold>

}

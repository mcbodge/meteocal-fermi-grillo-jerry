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
import javax.persistence.Id;
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
@Table(name = "locations", catalog = "meteocaldb", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Locations.findAll", query = "SELECT l FROM Locations l"),
    @NamedQuery(name = "Locations.findByGeonameid", query = "SELECT l FROM Locations l WHERE l.geonameid = :geonameid"),
    @NamedQuery(name = "Locations.findByName", query = "SELECT l FROM Locations l WHERE l.name = :name"),
    @NamedQuery(name = "Locations.findByAsciiname", query = "SELECT l FROM Locations l WHERE l.asciiname = :asciiname"),
    @NamedQuery(name = "Locations.findByCountry", query = "SELECT l FROM Locations l WHERE l.country = :country"),
    @NamedQuery(name = "Locations.findByAdmin2", query = "SELECT l FROM Locations l WHERE l.admin2 = :admin2"),
    @NamedQuery(name = "Locations.findByTimezone", query = "SELECT l FROM Locations l WHERE l.timezone = :timezone")})
public class Locations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "geonameid", nullable = false)
    private Integer geonameid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @Size(max = 200)
    @Column(name = "asciiname", length = 200)
    private String asciiname;
    @Size(max = 2)
    @Column(name = "country", length = 2)
    private String country;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "admin2", nullable = false, length = 80)
    private String admin2;
    @Size(max = 40)
    @Column(name = "timezone", length = 40)
    private String timezone;

    public Locations() {
    }

    public Locations(Integer geonameid) {
        this.geonameid = geonameid;
    }

    public Locations(Integer geonameid, String name, String admin2) {
        this.geonameid = geonameid;
        this.name = name;
        this.admin2 = admin2;
    }

    public Integer getGeonameid() {
        return geonameid;
    }

    public void setGeonameid(Integer geonameid) {
        this.geonameid = geonameid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsciiname() {
        return asciiname;
    }

    public void setAsciiname(String asciiname) {
        this.asciiname = asciiname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdmin2() {
        return admin2;
    }

    public void setAdmin2(String admin2) {
        this.admin2 = admin2;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geonameid != null ? geonameid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Locations)) {
            return false;
        }
        Locations other = (Locations) object;
        if ((this.geonameid == null && other.geonameid != null) || (this.geonameid != null && !this.geonameid.equals(other.geonameid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.meteocal.business.entity.Locations[ geonameid=" + geonameid + " ]";
    }
    
}

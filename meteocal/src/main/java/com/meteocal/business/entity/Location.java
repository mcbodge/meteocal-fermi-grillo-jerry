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
    @NamedQuery(name = "Locations.findByProvince", query = "SELECT l FROM Locations l WHERE l.admin2 = :province"),
    @NamedQuery(name = "Locations.findByTimezone", query = "SELECT l FROM Locations l WHERE l.timezone = :timezone")})
public class Location implements Serializable {
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
    private String province;
    @Size(max = 40)
    @Column(name = "timezone", length = 40)
    private String timezone;

    public Location() {
    }

    public Location(Integer geonameid) {
        this.geonameid = geonameid;
    }

    public Location(Integer geonameid, String name, String province) {
        this.geonameid = geonameid;
        this.name = name;
        this.province = province;
    }

    public Integer getGeonameid() {
        return geonameid;
    }

    public String getName() {
        return name;
    }

    public String getAsciiname() {
        return asciiname;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getTimezone() {
        return timezone;
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
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
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
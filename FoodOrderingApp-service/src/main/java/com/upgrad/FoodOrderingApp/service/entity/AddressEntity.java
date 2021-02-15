package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "address", schema = "public")
public class AddressEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "FLAT_BUIL_NUMBER")
    @NotNull
    private String flat_buil_number;

    @Column(name = "LOCALITY")
    @NotNull
    private String locality;

    @Column(name = "CITY")
    @NotNull
    private String city;

    @Column(name = "PINCODE")
    @NotNull
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "STATE_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StateEntity state_id;

    @Column(name = "ACTIVE")
    @NotNull

    private int active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlat_buil_number() {
        return flat_buil_number;
    }

    public void setFlat_buil_number(String flat_buil_number) {
        this.flat_buil_number = flat_buil_number;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState_id() {
        return state_id;
    }

    public void setState_id(StateEntity state_id) {
        this.state_id = state_id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}

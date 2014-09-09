package uk.co.yottr.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "boats")
public class Boat {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "manufacturer")
	@Size(min=1, max=30)
    private String manufacturer;

    @Column(name = "model")
    @Size(min=1, max=30)
    private String model;

    @Column(name = "length")
    @NotNull @Min(3) @Max(999)
    private Integer length;

    @Column(name = "units_imperial")
    private boolean isUnitsImperial;

    public enum HullType {
        MONO, MULTI
    }

    @Column(name = "hull_type")
    @NotNull
    private HullType hullType;

    @Column(name = "description")
    @NotEmpty
    private String desc;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isUnitsImperial() {
        return isUnitsImperial;
    }

    public void setUnitsImperial(boolean isUnitsImperial) {
        this.isUnitsImperial = isUnitsImperial;
    }

    public HullType getHullType() {
        return hullType;
    }

    public void setHullType(HullType hullType) {
        this.hullType = hullType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Boat)) return false;

        Boat boat = (Boat) o;

        if (id != boat.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

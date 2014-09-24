package uk.co.yottr.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Random;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Entity
@Table(name = "boats")
public class Boat {

    private static final String REQUIRED_ERROR_MSG = "required";

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "manufacturer")
    @NotNull(message = REQUIRED_ERROR_MSG)
	@Size(min=1, max=30)
    private String manufacturer;

    @Column(name = "model")
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Size(min=1, max=30)
    private String model;

    @Column(name = "length")
    @NotNull(message = REQUIRED_ERROR_MSG)
    @Min(3)
    @Max(999)
    private Integer length;

    @Column(name = "units_imperial")
    private boolean isUnitsImperial;

    public enum HullType {
        MONO, MULTI
    }

    @Column(name = "hull_type")
    @NotNull(message = REQUIRED_ERROR_MSG)
    private HullType hullType;

    @Column(name = "description")
    @NotEmpty(message = REQUIRED_ERROR_MSG)
    private String description;

    public Boat() {
        this.reference = System.currentTimeMillis() + "-" + new Random().nextInt(100);
    }

    public String getReference() {
        return reference;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

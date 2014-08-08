package com.journaldev.spring.form.model;

import com.journaldev.spring.form.validator.Year;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;

public class Boat {

    @Size(min=5, max=10, message = "must be between 5 and 10 characters long")
    private String reference;

	@Size(min=1, max=30)
    private String manufacturer;

    @Size(min=1, max=30)
    private String model;

    @NotNull @Min(3) @Max(999)
    private Integer length; //TODO units

    public enum HullType {
        MONO, MULTI
    }

    @NotNull
    private HullType hullType;

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
}

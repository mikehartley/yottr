package uk.co.yottr.model;

import javax.persistence.*;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Created by mike on 02/09/14.
 */
@Entity
@Table(name = "test")
public class TestEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "text")
    private String text;

    public TestEntity() {
    }

    public TestEntity(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

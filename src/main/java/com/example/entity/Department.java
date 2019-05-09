package com.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "org_department")
public class Department extends BaseEntity {

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

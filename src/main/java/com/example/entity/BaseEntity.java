package com.example.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, length = 39)
    protected UUID id;

    @Column(name = "create_time", updatable = false)
    protected LocalDateTime createTime;

    @PrePersist
    private void onCreate(){
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @Column(name = "update_time")
    protected LocalDateTime updateTime;

    @PreUpdate
    protected void onUpdate(){
        updateTime = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

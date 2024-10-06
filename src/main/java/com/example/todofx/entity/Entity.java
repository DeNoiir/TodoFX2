package com.example.todofx.entity;

public interface Entity<ID> {
    ID getId();
    void setId(ID id);
}
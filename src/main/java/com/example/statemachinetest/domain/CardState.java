package com.example.statemachinetest.domain;

public enum CardState {
    NEW("NEW"),
    PENDING("PENDING"),
    CANCELLED("CANCELLED"),
    READY("READY"),
    DELIVERED("DELIVERED");

    private String name;

    CardState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

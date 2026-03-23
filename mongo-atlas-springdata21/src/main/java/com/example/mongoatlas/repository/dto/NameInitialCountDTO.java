package com.example.mongoatlas.repository.dto;

public class NameInitialCountDTO {

    private String initial;
    private Long total;

    public NameInitialCountDTO() {
    }

    public NameInitialCountDTO(String initial, Long total) {
        this.initial = initial;
        this.total = total;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
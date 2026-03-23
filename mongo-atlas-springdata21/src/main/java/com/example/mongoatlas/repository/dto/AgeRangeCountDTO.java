package com.example.mongoatlas.repository.dto;

public class AgeRangeCountDTO {

    private String range;
    private Long total;

    public AgeRangeCountDTO() {
    }

    public AgeRangeCountDTO(String range, Long total) {
        this.range = range;
        this.total = total;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
package com.example.mongoatlas.repository.dto;
public class YoungestOldestByCourseDTO {

    private String course;
    private Integer youngestAge;
    private Integer oldestAge;

    public YoungestOldestByCourseDTO() {
    }

    public YoungestOldestByCourseDTO(String course, Integer youngestAge, Integer oldestAge) {
        this.course = course;
        this.youngestAge = youngestAge;
        this.oldestAge = oldestAge;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getYoungestAge() {
        return youngestAge;
    }

    public void setYoungestAge(Integer youngestAge) {
        this.youngestAge = youngestAge;
    }

    public Integer getOldestAge() {
        return oldestAge;
    }

    public void setOldestAge(Integer oldestAge) {
        this.oldestAge = oldestAge;
    }
}
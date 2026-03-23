package com.example.mongoatlas.repository.dto;

public class CourseCountDTO {

    private String course;
    private Long total;

    public CourseCountDTO() {
    }

    public CourseCountDTO(String course, Long total) {
        this.course = course;
        this.total = total;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
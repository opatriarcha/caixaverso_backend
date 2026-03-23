package com.example.mongoatlas.repository.dto;

public class CourseAverageAgeDTO {

    private String course;
    private Double averageAge;

    public CourseAverageAgeDTO() {
    }

    public CourseAverageAgeDTO(String course, Double averageAge) {
        this.course = course;
        this.averageAge = averageAge;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(Double averageAge) {
        this.averageAge = averageAge;
    }
}
package com.example.mongoatlas.repository.dto;

public class CourseStatsDTO {

    private String course;
    private Long totalStudents;
    private Double averageAge;
    private Integer minAge;
    private Integer maxAge;

    public CourseStatsDTO() {
    }

    public CourseStatsDTO(String course, Long totalStudents, Double averageAge, Integer minAge, Integer maxAge) {
        this.course = course;
        this.totalStudents = totalStudents;
        this.averageAge = averageAge;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(Double averageAge) {
        this.averageAge = averageAge;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
}
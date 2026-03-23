package com.example.mongoatlas.dto;

public class StudentResponse {

    private String id;
    private String name;
    private String course;
    private Integer age;
    private String email;

    public StudentResponse() {
    }

    public StudentResponse(String id, String name, String course, Integer age, String email) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.age = age;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

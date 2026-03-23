package com.example.mongoatlas.service;

import com.example.mongoatlas.dto.PagedResponse;
import com.example.mongoatlas.dto.StudentRequest;
import com.example.mongoatlas.dto.StudentResponse;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    StudentResponse findById(String id);

    PagedResponse<StudentResponse> findAll(String name, String course, int page, int size, String sortBy, String direction);

    StudentResponse update(String id, StudentRequest request);

    void delete(String id);
}

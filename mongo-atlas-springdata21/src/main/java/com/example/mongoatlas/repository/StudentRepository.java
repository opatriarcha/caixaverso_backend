package com.example.mongoatlas.repository;

import com.example.mongoatlas.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {

    Page<Student> findByCourseContainingIgnoreCase(String course, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(String name, String course, Pageable pageable);
}

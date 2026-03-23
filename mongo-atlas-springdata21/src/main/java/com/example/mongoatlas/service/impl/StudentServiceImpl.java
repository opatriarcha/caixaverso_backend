package com.example.mongoatlas.service.impl;

import com.example.mongoatlas.dto.PagedResponse;
import com.example.mongoatlas.dto.StudentRequest;
import com.example.mongoatlas.dto.StudentResponse;
import com.example.mongoatlas.entity.Student;
import com.example.mongoatlas.exception.ResourceNotFoundException;
import com.example.mongoatlas.repository.StudentRepository;
import com.example.mongoatlas.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = toEntity(request);
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Override
    public StudentResponse findById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return toResponse(student);
    }

    @Override
    public PagedResponse<StudentResponse> findAll(String name, String course, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> result;

        boolean hasName = name != null && !name.isBlank();
        boolean hasCourse = course != null && !course.isBlank();

        if (hasName && hasCourse) {
            result = studentRepository.findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(name, course, pageable);
        } else if (hasName) {
            result = studentRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasCourse) {
            result = studentRepository.findByCourseContainingIgnoreCase(course, pageable);
        } else {
            result = studentRepository.findAll(pageable);
        }

        return new PagedResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Override
    public StudentResponse update(String id, StudentRequest request) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existing.setName(request.getName());
        existing.setCourse(request.getCourse());
        existing.setAge(request.getAge());
        existing.setEmail(request.getEmail());

        Student updated = studentRepository.save(existing);
        return toResponse(updated);
    }

    @Override
    public void delete(String id) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(existing);
    }

    private Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setCourse(request.getCourse());
        student.setAge(request.getAge());
        student.setEmail(request.getEmail());
        return student;
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getCourse(),
                student.getAge(),
                student.getEmail()
        );
    }
}

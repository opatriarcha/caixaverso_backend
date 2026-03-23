package com.example.mongoatlas.controller;

import com.example.mongoatlas.dto.PagedResponse;
import com.example.mongoatlas.dto.StudentRequest;
import com.example.mongoatlas.dto.StudentResponse;
import com.example.mongoatlas.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable String id) {
        return studentService.findById(id);
    }

    @GetMapping
    public PagedResponse<StudentResponse> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String course,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be >= 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be >= 1") @Max(value = 100, message = "size must be <= 100") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc", message = "direction must be asc or desc") String direction
    ) {
        return studentService.findAll(name, course, page, size, sortBy, direction);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable String id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        studentService.delete(id);
    }
}

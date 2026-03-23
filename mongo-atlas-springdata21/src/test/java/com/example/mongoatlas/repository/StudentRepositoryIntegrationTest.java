package com.example.mongoatlas.repository;

import com.example.mongoatlas.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class StudentRepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    public StudentRepositoryIntegrationTest(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        studentRepository.saveAll(List.of(
                new Student(null, "Ana Silva", "IA", 19, "ana.silva@gmail.com"),
                new Student(null, "Bruno Souza", "IA", 22, "bruno.souza@gmail.com"),
                new Student(null, "Carlos Lima", "ADS", 28, "carlos.lima@yahoo.com"),
                new Student(null, "Daniel Costa", "ADS", 31, "daniel.costa@yahoo.com"),
                new Student(null, "Eduarda Santos", "Medicina", 24, "eduarda.santos@outlook.com"),
                new Student(null, "Fernanda Rocha", "Direito", 42, "fernanda.rocha@gmail.com"),
                new Student(null, "Gabriel Nunes", "Direito", 52, "gabriel.nunes@empresa.com"),
                new Student(null, "Amanda Rocha", "IA", 27, "amanda.rocha@gmail.com"),
                new Student(null, "Aline Rocha", "ADS", 18, "aline.rocha@empresa.com")
        ));
    }

    @Test
    void shouldSaveStudent() {
        Student student = new Student(null, "Marcos Pereira", "Engenharia", 26, "marcos.pereira@gmail.com");

        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Marcos Pereira");
    }

    @Test
    void shouldFindById() {
        Student saved = studentRepository.save(
                new Student(null, "Juliana Alves", "Arquitetura", 29, "juliana.alves@gmail.com")
        );

        Optional<Student> result = studentRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("juliana.alves@gmail.com");
    }

    @Test
    void shouldFindAll() {
        List<Student> students = studentRepository.findAll();

        assertThat(students).hasSize(9);
    }

    @Test
    void shouldUpdateStudent() {
        Student saved = studentRepository.save(
                new Student(null, "Paulo Mendes", "ADS", 21, "paulo.mendes@gmail.com")
        );

        saved.setCourse("IA");
        saved.setAge(22);

        Student updated = studentRepository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getCourse()).isEqualTo("IA");
        assertThat(updated.getAge()).isEqualTo(22);
    }

    @Test
    void shouldDeleteStudentById() {
        Student saved = studentRepository.save(
                new Student(null, "Renata Gomes", "Medicina", 30, "renata.gomes@gmail.com")
        );

        studentRepository.deleteById(saved.getId());

        Optional<Student> result = studentRepository.findById(saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByCourseContainingIgnoreCaseWithPagination() {
        Page<Student> page = studentRepository.findByCourseContainingIgnoreCase(
                "ad",
                PageRequest.of(0, 2, Sort.by("name").ascending())
        );

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Student::getName)
                .containsExactly("Aline Rocha", "Carlos Lima");
    }

    @Test
    void shouldFindByNameContainingIgnoreCaseWithPagination() {
        Page<Student> page = studentRepository.findByNameContainingIgnoreCase(
                "rocha",
                PageRequest.of(0, 10, Sort.by("name").ascending())
        );

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(Student::getName)
                .containsExactly("Aline Rocha", "Amanda Rocha", "Fernanda Rocha");
    }

    @Test
    void shouldFindByNameContainingIgnoreCaseAndCourseContainingIgnoreCaseWithPagination() {
        Page<Student> page = studentRepository.findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(
                "rocha",
                "ia",
                PageRequest.of(0, 10, Sort.by("name").ascending())
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Amanda Rocha");
        assertThat(page.getContent().get(0).getCourse()).isEqualTo("IA");
    }

    @Test
    void shouldReturnEmptyPageWhenNoMatchExists() {
        Page<Student> page = studentRepository.findByNameContainingIgnoreCaseAndCourseContainingIgnoreCase(
                "xpto",
                "engenharia",
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }
}
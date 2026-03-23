package com.example.mongoatlas.repository;

import com.example.mongoatlas.entity.Student;
import com.example.mongoatlas.repository.StudentAnalyticsRepository;
import com.example.mongoatlas.repository.StudentAnalyticsRepositoryImpl;
import com.example.mongoatlas.repository.StudentRepository;
import com.example.mongoatlas.repository.dto.AgeRangeCountDTO;
import com.example.mongoatlas.repository.dto.CourseAverageAgeDTO;
import com.example.mongoatlas.repository.dto.CourseCountDTO;
import com.example.mongoatlas.repository.dto.CourseStatsDTO;
import com.example.mongoatlas.repository.dto.EmailDomainCountDTO;
import com.example.mongoatlas.repository.dto.NameInitialCountDTO;
import com.example.mongoatlas.repository.dto.YoungestOldestByCourseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(StudentAnalyticsRepositoryImpl.class)
class StudentAnalyticsRepositoryIntegrationTest {

    private final StudentAnalyticsRepository studentAnalyticsRepository;
    private final StudentRepository studentRepository;

    @Autowired
    StudentAnalyticsRepositoryIntegrationTest(
            StudentAnalyticsRepository studentAnalyticsRepository,
            StudentRepository studentRepository
    ) {
        this.studentAnalyticsRepository = studentAnalyticsRepository;
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
    void shouldCountStudentsByCourse() {
        List<CourseCountDTO> result = studentAnalyticsRepository.countStudentsByCourse();

        assertThat(result).hasSize(4);
        assertThat(result)
                .filteredOn(item -> item.getCourse().equals("IA"))
                .singleElement()
                .extracting(CourseCountDTO::getTotal)
                .isEqualTo(3L);
    }

    @Test
    void shouldCalculateAverageAgeByCourse() {
        List<CourseAverageAgeDTO> result = studentAnalyticsRepository.averageAgeByCourse();

        CourseAverageAgeDTO ia = result.stream()
                .filter(item -> item.getCourse().equals("IA"))
                .findFirst()
                .orElseThrow();

        assertThat(ia.getAverageAge()).isEqualTo((19D + 22D + 27D) / 3D);
    }

    @Test
    void shouldReturnCourseStatistics() {
        List<CourseStatsDTO> result = studentAnalyticsRepository.courseStatistics();

        CourseStatsDTO ads = result.stream()
                .filter(item -> item.getCourse().equals("ADS"))
                .findFirst()
                .orElseThrow();

        assertThat(ads.getTotalStudents()).isEqualTo(3L);
        assertThat(ads.getMinAge()).isEqualTo(18);
        assertThat(ads.getMaxAge()).isEqualTo(31);
    }

    @Test
    void shouldCountStudentsByEmailDomain() {
        List<EmailDomainCountDTO> result = studentAnalyticsRepository.countStudentsByEmailDomain();

        assertThat(result)
                .filteredOn(item -> item.getDomain().equals("gmail.com"))
                .singleElement()
                .extracting(EmailDomainCountDTO::getTotal)
                .isEqualTo(4L);
    }

    @Test
    void shouldCountStudentsByAgeRange() {
        List<AgeRangeCountDTO> result = studentAnalyticsRepository.countStudentsByAgeRange();

        assertThat(result).isNotEmpty();
        assertThat(result.stream().mapToLong(AgeRangeCountDTO::getTotal).sum()).isEqualTo(9L);
    }

    @Test
    void shouldCountStudentsByNameInitial() {
        List<NameInitialCountDTO> result = studentAnalyticsRepository.countStudentsByNameInitial();

        assertThat(result)
                .filteredOn(item -> item.getInitial().equals("A"))
                .singleElement()
                .extracting(NameInitialCountDTO::getTotal)
                .isEqualTo(3L);
    }

    @Test
    void shouldReturnYoungestAndOldestByCourse() {
        List<YoungestOldestByCourseDTO> result = studentAnalyticsRepository.youngestAndOldestStudentByCourse();

        YoungestOldestByCourseDTO direito = result.stream()
                .filter(item -> item.getCourse().equals("Direito"))
                .findFirst()
                .orElseThrow();

        assertThat(direito.getYoungestAge()).isEqualTo(42);
        assertThat(direito.getOldestAge()).isEqualTo(52);
    }

    @Test
    void shouldFilterAggregationByAge() {
        List<CourseCountDTO> result = studentAnalyticsRepository.countStudentsByCourseForAgeGreaterThanEqual(30);

        assertThat(result.stream().mapToLong(CourseCountDTO::getTotal).sum()).isEqualTo(3L);
    }

    @Test
    void shouldFilterAggregationByNameContains() {
        List<CourseCountDTO> result = studentAnalyticsRepository.countStudentsByCourseForNameContains("rocha");

        assertThat(result.stream().mapToLong(CourseCountDTO::getTotal).sum()).isEqualTo(3L);
    }
}
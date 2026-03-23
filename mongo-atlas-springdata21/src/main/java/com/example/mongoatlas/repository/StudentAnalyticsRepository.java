package com.example.mongoatlas.repository;

import com.example.mongoatlas.repository.dto.AgeRangeCountDTO;
import com.example.mongoatlas.repository.dto.CourseAverageAgeDTO;
import com.example.mongoatlas.repository.dto.CourseCountDTO;
import com.example.mongoatlas.repository.dto.CourseStatsDTO;
import com.example.mongoatlas.repository.dto.EmailDomainCountDTO;
import com.example.mongoatlas.repository.dto.NameInitialCountDTO;
import com.example.mongoatlas.repository.dto.YoungestOldestByCourseDTO;

import java.util.List;

public interface StudentAnalyticsRepository {

    List<CourseCountDTO> countStudentsByCourse();

    List<CourseAverageAgeDTO> averageAgeByCourse();

    List<CourseStatsDTO> courseStatistics();

    List<EmailDomainCountDTO> countStudentsByEmailDomain();

    List<AgeRangeCountDTO> countStudentsByAgeRange();

    List<NameInitialCountDTO> countStudentsByNameInitial();

    List<YoungestOldestByCourseDTO> youngestAndOldestStudentByCourse();

    List<CourseCountDTO> countStudentsByCourseForAgeGreaterThanEqual(Integer age);

    List<CourseCountDTO> countStudentsByCourseForNameContains(String token);
}
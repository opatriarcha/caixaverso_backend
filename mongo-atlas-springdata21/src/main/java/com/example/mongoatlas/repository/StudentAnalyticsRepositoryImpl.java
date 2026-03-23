package com.example.mongoatlas.repository;

import com.example.mongoatlas.repository.dto.AgeRangeCountDTO;
import com.example.mongoatlas.repository.dto.CourseAverageAgeDTO;
import com.example.mongoatlas.repository.dto.CourseCountDTO;
import com.example.mongoatlas.repository.dto.CourseStatsDTO;
import com.example.mongoatlas.repository.dto.EmailDomainCountDTO;
import com.example.mongoatlas.repository.dto.NameInitialCountDTO;
import com.example.mongoatlas.repository.dto.YoungestOldestByCourseDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Max.maxOf;
import static org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Min.minOf;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.addFields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.switchCases;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gte.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lt.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator.when;

@Repository
public class StudentAnalyticsRepositoryImpl implements StudentAnalyticsRepository {

    private final MongoTemplate mongoTemplate;

    public StudentAnalyticsRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<CourseCountDTO> countStudentsByCourse() {
        Aggregation aggregation = Aggregation.newAggregation(
                group("course").count().as("total"),
                project("total").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", CourseCountDTO.class).getMappedResults();
    }

    @Override
    public List<CourseAverageAgeDTO> averageAgeByCourse() {
        Aggregation aggregation = Aggregation.newAggregation(
                group("course").avg("age").as("averageAge"),
                project("averageAge").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", CourseAverageAgeDTO.class).getMappedResults();
    }

    @Override
    public List<CourseStatsDTO> courseStatistics() {
        Aggregation aggregation = Aggregation.newAggregation(
                group("course")
                        .count().as("totalStudents")
                        .avg("age").as("averageAge")
                        .min("age").as("minAge")
                        .max("age").as("maxAge"),
                project("totalStudents", "averageAge", "minAge", "maxAge").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", CourseStatsDTO.class).getMappedResults();
    }

    @Override
    public List<EmailDomainCountDTO> countStudentsByEmailDomain() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and(
                                ArrayOperators.ArrayElemAt.arrayOf(
                                        StringOperators.valueOf("email").split("@")
                                ).elementAt(1)
                        )
                        .as("domain"),
                Aggregation.group("domain").count().as("total"),
                Aggregation.project("total").and("_id").as("domain"),
                Aggregation.sort(Sort.Direction.ASC, "domain")
        );

        return mongoTemplate
                .aggregate(aggregation, "students", EmailDomainCountDTO.class)
                .getMappedResults();
    }

//    @Override
//    public List<AgeRangeCountDTO> countStudentsByAgeRange() {
//        Aggregation aggregation = Aggregation.newAggregation(
//                addFields()
//                        .addField("ageRange")
//                        .withValue(
//                                switchCases(
//                                        when(valueOf("age").lessThanValue(20)).then("UNDER_20"),
//                                        when(valueOf("age").greaterThanEqualToValue(20)
//                                                .and(valueOf("age").lessThanValue(30))).then("20_TO_29"),
//                                        when(valueOf("age").greaterThanEqualToValue(30)
//                                                .and(valueOf("age").lessThanValue(40))).then("30_TO_39"),
//                                        when(valueOf("age").greaterThanEqualToValue(40)
//                                                .and(valueOf("age").lessThanValue(50))).then("40_TO_49")
//                                ).defaultTo("50_PLUS")
//                        )
//                        .build(),
//                group("ageRange").count().as("total"),
//                project("total").and("_id").as("range"),
//                sort(Sort.Direction.ASC, "range")
//        );
//
//        return mongoTemplate.aggregate(aggregation, "students", AgeRangeCountDTO.class).getMappedResults();
//    }

    @Override
    public List<AgeRangeCountDTO> countStudentsByAgeRange() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.bucket("age")
                        .withBoundaries(0, 20, 30, 40, 50, 200)
                        .withDefaultBucket("UNKNOWN")
                        .andOutputCount().as("total"),
                Aggregation.project("total")
                        .and(
                                ConditionalOperators.switchCases(
                                        ConditionalOperators.Switch.CaseOperator
                                                .when(ComparisonOperators.Eq.valueOf("_id").equalToValue(0))
                                                .then("UNDER_20"),
                                        ConditionalOperators.Switch.CaseOperator
                                                .when(ComparisonOperators.Eq.valueOf("_id").equalToValue(20))
                                                .then("20_TO_29"),
                                        ConditionalOperators.Switch.CaseOperator
                                                .when(ComparisonOperators.Eq.valueOf("_id").equalToValue(30))
                                                .then("30_TO_39"),
                                        ConditionalOperators.Switch.CaseOperator
                                                .when(ComparisonOperators.Eq.valueOf("_id").equalToValue(40))
                                                .then("40_TO_49"),
                                        ConditionalOperators.Switch.CaseOperator
                                                .when(ComparisonOperators.Eq.valueOf("_id").equalToValue(50))
                                                .then("50_PLUS")
                                ).defaultTo("UNKNOWN")
                        ).as("range"),
                Aggregation.sort(Sort.Direction.ASC, "range")
        );

        return mongoTemplate
                .aggregate(aggregation, "students", AgeRangeCountDTO.class)
                .getMappedResults();
    }

    @Override
    public List<NameInitialCountDTO> countStudentsByNameInitial() {
        Aggregation aggregation = Aggregation.newAggregation(
                project()
                        .and(StringOperators.valueOf("name").substring(0, 1))
                        .as("initial"),
                group("initial").count().as("total"),
                project("total").and("_id").as("initial"),
                sort(Sort.Direction.ASC, "initial")
        );

        return mongoTemplate.aggregate(aggregation, "students", NameInitialCountDTO.class).getMappedResults();
    }

    @Override
    public List<YoungestOldestByCourseDTO> youngestAndOldestStudentByCourse() {
        Aggregation aggregation = Aggregation.newAggregation(
                group("course")
                        .min("age").as("youngestAge")
                        .max("age").as("oldestAge"),
                project("youngestAge", "oldestAge").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", YoungestOldestByCourseDTO.class).getMappedResults();
    }

    @Override
    public List<CourseCountDTO> countStudentsByCourseForAgeGreaterThanEqual(Integer age) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("age").gte(age)),
                group("course").count().as("total"),
                project("total").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", CourseCountDTO.class).getMappedResults();
    }

    @Override
    public List<CourseCountDTO> countStudentsByCourseForNameContains(String token) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("name").regex(token, "i")),
                group("course").count().as("total"),
                project("total").and("_id").as("course"),
                sort(Sort.Direction.ASC, "course")
        );

        return mongoTemplate.aggregate(aggregation, "students", CourseCountDTO.class).getMappedResults();
    }
}
package com.example.mongoatlas.controller;

import com.example.mongoatlas.dto.PagedResponse;
import com.example.mongoatlas.dto.StudentRequest;
import com.example.mongoatlas.dto.StudentResponse;
import com.example.mongoatlas.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void shouldCreateStudent() throws Exception {
        StudentRequest request = new StudentRequest();
        request.setName("Orlando");
        request.setCourse("IA");
        request.setAge(35);
        request.setEmail("orlando@example.com");

        StudentResponse response = new StudentResponse("1", "Orlando", "IA", 35, "orlando@example.com");
        when(studentService.create(any(StudentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Orlando"));
    }

    @Test
    void shouldReturnStudentById() throws Exception {
        when(studentService.findById("1")).thenReturn(new StudentResponse("1", "Maria", "Dados", 28, "maria@example.com"));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.course").value("Dados"));
    }

    @Test
    void shouldReturnPagedStudents() throws Exception {
        PagedResponse<StudentResponse> page = new PagedResponse<>(
                List.of(new StudentResponse("1", "Ana", "IA", 22, "ana@example.com")),
                0,
                10,
                1,
                1,
                true,
                true
        );

        when(studentService.findAll(null, null, 0, 10, "name", "asc")).thenReturn(page);

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ana"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        StudentRequest request = new StudentRequest();
        request.setName("Joao");
        request.setCourse("Backend");
        request.setAge(30);
        request.setEmail("joao@example.com");

        StudentResponse response = new StudentResponse("7", "Joao", "Backend", 30, "joao@example.com");
        when(studentService.update(eq("7"), any(StudentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/students/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7"))
                .andExpect(jsonPath("$.course").value("Backend"));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        doNothing().when(studentService).delete("9");

        mockMvc.perform(delete("/api/students/9"))
                .andExpect(status().isNoContent());
    }
}

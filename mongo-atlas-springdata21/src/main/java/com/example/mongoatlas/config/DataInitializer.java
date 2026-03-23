package com.example.mongoatlas.config;


import com.example.mongoatlas.entity.Student;
import com.example.mongoatlas.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Profile("atlas")
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository repository;

    public DataInitializer(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        repository.deleteAll();
        System.out.println("criando registros!!!");
        List<String> firstNames = List.of(
                "Ana", "Bruno", "Carlos", "Daniela", "Eduardo",
                "Fernanda", "Gabriel", "Helena", "Igor", "Juliana",
                "Kleber", "Larissa", "Marcos", "Natália", "Otávio",
                "Patrícia", "Rafael", "Sabrina", "Thiago", "Vanessa"
        );

        List<String> lastNames = List.of(
                "Silva", "Souza", "Oliveira", "Santos", "Pereira",
                "Costa", "Rodrigues", "Almeida", "Nascimento", "Lima"
        );

        List<String> courses = List.of(
                "IA", "Engenharia", "Medicina", "Direito",
                "Arquitetura", "ADS", "Ciência de Dados"
        );

        Random random = new Random();

        List<Student> students = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> {

                    String firstName = firstNames.get(random.nextInt(firstNames.size()));
                    String lastName = lastNames.get(random.nextInt(lastNames.size()));
                    String name = firstName + " " + lastName;

                    String course = courses.get(random.nextInt(courses.size()));

                    int age = 17 + random.nextInt(44);

                    String email = (firstName + "." + lastName + i + "@email.com")
                            .toLowerCase()
                            .replace("á", "a")
                            .replace("ã", "a")
                            .replace("é", "e")
                            .replace("í", "i")
                            .replace("ó", "o")
                            .replace("ú", "u");

                    return new Student(
                            null,
                            name,
                            course,
                            age,
                            email
                    );
                })
                .toList();

        List<Student> studentsAll = repository.saveAll(students);

        for(Student s : studentsAll){
            System.out.println(s);
        }
    }
}
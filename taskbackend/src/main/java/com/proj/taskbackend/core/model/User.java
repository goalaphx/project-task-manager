package com.proj.taskbackend.core.model;



import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users") // "user" is a reserved word in SQL, so we use "users"
@Data // Lombok: Generates Getters, Setters, toString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // One User can have multiple projects
    // "mappedBy" refers to the 'user' field in the Project class (we will create next)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;
}
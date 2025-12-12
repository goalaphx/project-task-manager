package com.proj.taskbackend.core.model;



import jakarta.persistence.*;
import lombok.*;
import com.proj.taskbackend.core.enums.TaskStatus;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING) // Stores "PENDING" or "COMPLETED" as text in DB
    private TaskStatus status = TaskStatus.PENDING;

    // Link back to Project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
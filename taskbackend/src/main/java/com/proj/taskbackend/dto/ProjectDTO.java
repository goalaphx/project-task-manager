package com.proj.taskbackend.dto;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Double progress; // Calculated field (0.0 to 100.0)
    private int taskCount;   // Total tasks
    private int completedTaskCount;
}
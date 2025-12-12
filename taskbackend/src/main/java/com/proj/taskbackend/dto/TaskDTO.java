package com.proj.taskbackend.dto;



import com.proj.taskbackend.core.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId; // Just the ID is enough
}
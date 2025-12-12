package com.proj.taskbackend.api;


import com.proj.taskbackend.core.enums.TaskStatus;
import com.proj.taskbackend.core.service.TaskService;
import com.proj.taskbackend.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Create a task inside a specific project
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable Long projectId, @RequestBody TaskDTO request) {
        return ResponseEntity.ok(taskService.createTask(projectId, request));
    }

    // Get all tasks for a project (With Search & Pagination)
    // URL example: GET /api/projects/1/tasks?page=0&size=5&search=urgent
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        return ResponseEntity.ok(taskService.getTasks(projectId, search, pageable));
    }

    // Update status (e.g., mark as COMPLETED)
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    // Delete task
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
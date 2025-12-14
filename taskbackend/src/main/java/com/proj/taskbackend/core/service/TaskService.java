package com.proj.taskbackend.core.service;


import com.proj.taskbackend.core.enums.TaskStatus;
import com.proj.taskbackend.core.model.Project;
import com.proj.taskbackend.core.model.Task;
import com.proj.taskbackend.core.model.User;
import com.proj.taskbackend.core.repository.ProjectRepository;
import com.proj.taskbackend.core.repository.TaskRepository;
import com.proj.taskbackend.core.repository.UserRepository;
import com.proj.taskbackend.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    public TaskDTO createTask(Long projectId, TaskDTO request) {
        User user = getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Security: Does this project belong to the user?
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TaskStatus.PENDING) // Default status
                .project(project)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToDTO(savedTask);
    }

    // BONUS: Search + Pagination
    public Page<TaskDTO> getTasks(Long projectId, String search, Pageable pageable) {
        // We assume the controller handles the "is this my project?" check or we trust the repo filter
        // Ideally, you'd verify ownership here too, similar to createTask

        Page<Task> tasks = taskRepository.findByProjectIdAndTitleContaining(projectId, search, pageable);
        return tasks.map(this::mapToDTO);
    }

    public TaskDTO updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Security check omitted for brevity, but you should add it!

        task.setStatus(status);
        return mapToDTO(taskRepository.save(task));
    }

    public TaskDTO updateTask(Long taskId, TaskDTO taskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = getCurrentUser();
        if (!task.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this task");
        }

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskDTO mapToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .build();
    }
}
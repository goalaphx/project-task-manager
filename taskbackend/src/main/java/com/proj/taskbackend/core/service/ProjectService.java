package com.proj.taskbackend.core.service;


import com.proj.taskbackend.core.model.Project;
import com.proj.taskbackend.core.model.Task;
import com.proj.taskbackend.core.model.User;
import com.proj.taskbackend.core.repository.ProjectRepository;
import com.proj.taskbackend.core.repository.UserRepository;
import com.proj.taskbackend.dto.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Helper: Get the currently logged-in user from the JWT Token
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ProjectDTO createProject(ProjectDTO request) {
        User user = getCurrentUser();

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user) // Link project to the logged-in user
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    public List<ProjectDTO> getAllUserProjects() {
        User user = getCurrentUser();
        return projectRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Security Check: Ensure the project belongs to the current user!
        User user = getCurrentUser();
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this project");
        }

        return mapToDTO(project);
    }

    // This handles the "Progress Calculation" technical requirement
    private ProjectDTO mapToDTO(Project project) {
        List<Task> tasks = project.getTasks();
        int totalTasks = (tasks == null) ? 0 : tasks.size();
        int completedTasks = (tasks == null) ? 0 : (int) tasks.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus().name()))
                .count();

        double progress = (totalTasks == 0) ? 0.0 : ((double) completedTasks / totalTasks) * 100;

        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .taskCount(totalTasks)
                .completedTaskCount(completedTasks)
                .progress(progress) // <--- The calculated percentage
                .build();
    }
}
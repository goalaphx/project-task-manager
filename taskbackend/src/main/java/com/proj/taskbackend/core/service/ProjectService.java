package com.proj.taskbackend.core.service;

import java.util.List;
import com.proj.taskbackend.core.model.Project;
import com.proj.taskbackend.core.model.Task;
import com.proj.taskbackend.core.model.User;
import com.proj.taskbackend.core.repository.ProjectRepository;
import com.proj.taskbackend.core.repository.TaskRepository;
import com.proj.taskbackend.core.repository.UserRepository;
import com.proj.taskbackend.dto.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
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
        java.util.List<Project> projects = projectRepository.findByUserId(user.getId());

        // Avoid N+1 by fetching task counts for all projects in a single query
        java.util.List<Long> ids = projects.stream().map(Project::getId).collect(Collectors.toList());
            java.util.Map<Long, Long> totalMap = new java.util.HashMap<>();
            java.util.Map<Long, Long> completedMap = new java.util.HashMap<>();

        if (!ids.isEmpty()) {
            java.util.List<Object[]> rows = taskRepository.countTotalsByProjectIds(ids);
            for (Object[] r : rows) {
                Long projectId = ((Number) r[0]).longValue();
                Long total = ((Number) r[1]).longValue();
                Long completed = ((Number) (r[2] == null ? 0 : r[2])).longValue();
                totalMap.put(projectId, total);
                completedMap.put(projectId, completed);
            }
        }

        return projects.stream().map(p -> {
            int total = totalMap.getOrDefault(p.getId(), 0L).intValue();
            int completed = completedMap.getOrDefault(p.getId(), 0L).intValue();
            double progress = (total == 0) ? 0.0 : ((double) completed / total) * 100;
            return ProjectDTO.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .createdAt(p.getCreatedAt())
                    .taskCount(total)
                    .completedTaskCount(completed)
                    .progress(progress)
                    .build();
        }).collect(Collectors.toList());
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Security Check: Ensure the project belongs to the current user!
        User user = getCurrentUser();
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this project");
        }

        // Avoid loading all Task entities for a single project; use aggregation to compute counts
        java.util.List<Object[]> rows = taskRepository.countTotalsByProjectIds(java.util.List.of(id));
        int total = 0;
        int completed = 0;
        if (!rows.isEmpty()) {
            Object[] r = rows.get(0);
            total = ((Number) r[1]).intValue();
            completed = ((Number) (r[2] == null ? 0 : r[2])).intValue();
        }

        double progress = (total == 0) ? 0.0 : ((double) completed / total) * 100;

        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .taskCount(total)
                .completedTaskCount(completed)
                .progress(progress)
                .build();
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

    // Delete a project (only owner can delete). Tasks are removed via cascade.
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = getCurrentUser();
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this project");
        }

        projectRepository.delete(project);
    }

    public ProjectDTO updateProject(Long id, ProjectDTO projectDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = getCurrentUser();
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this project");
        }

        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());

        Project updatedProject = projectRepository.save(project);
        return getProjectById(updatedProject.getId());
    }
}
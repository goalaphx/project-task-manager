package com.proj.taskbackend.api;


import com.proj.taskbackend.core.service.ProjectService;
import com.proj.taskbackend.dto.ProjectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO request) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getUserProjects() {
        return ResponseEntity.ok(projectService.getAllUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectDetails(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
}
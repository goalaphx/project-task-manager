package com.proj.taskbackend.core.service;

import com.proj.taskbackend.core.enums.TaskStatus;
import com.proj.taskbackend.core.model.Project;
import com.proj.taskbackend.core.model.Task;
import com.proj.taskbackend.core.model.User;
import com.proj.taskbackend.core.repository.ProjectRepository;
import com.proj.taskbackend.core.repository.UserRepository;
import com.proj.taskbackend.dto.ProjectDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        // Mock the Security Context to simulate a logged-in user
        // This prevents the "User not found" error during testing
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCalculateProgressCorrectly() {
        // 1. SETUP: Create fake data
        User mockUser = User.builder().id(1L).email("test@test.com").build();

        Task task1 = Task.builder().status(TaskStatus.COMPLETED).build();
        Task task2 = Task.builder().status(TaskStatus.PENDING).build();
        Task task3 = Task.builder().status(TaskStatus.PENDING).build();
        Task task4 = Task.builder().status(TaskStatus.COMPLETED).build();

        // 2 COMPLETED, 2 PENDING = 50% Progress
        List<Task> tasks = Arrays.asList(task1, task2, task3, task4);

        Project mockProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .user(mockUser)
                .tasks(tasks)
                .build();

        // 2. MOCK: Tell the repo what to return when asked
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        // 3. EXECUTE: Call the actual service method
        ProjectDTO result = projectService.getProjectById(1L);

        // 4. ASSERT: Check if the math is right
        assertEquals(50.0, result.getProgress());
        assertEquals(4, result.getTaskCount());
        assertEquals(2, result.getCompletedTaskCount());
    }
}
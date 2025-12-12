package com.proj.taskbackend.core.repository;



import com.proj.taskbackend.core.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Standard fetch
    // We don't need a list because we usually fetch tasks via the Project entity

    // Bonus: Search tasks within a specific project with Pagination
    Page<Task> findByProjectIdAndTitleContaining(Long projectId, String title, Pageable pageable);
}
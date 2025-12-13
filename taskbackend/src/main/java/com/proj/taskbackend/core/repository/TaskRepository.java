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

    @org.springframework.data.jpa.repository.Query("SELECT t.project.id, COUNT(t), SUM(CASE WHEN t.status = com.proj.taskbackend.core.enums.TaskStatus.COMPLETED THEN 1 ELSE 0 END) FROM Task t WHERE t.project.id IN :ids GROUP BY t.project.id")
    java.util.List<Object[]> countTotalsByProjectIds(@org.springframework.data.repository.query.Param("ids") java.util.List<Long> ids);
}
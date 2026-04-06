package com.example.TaskManager.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByProjectId(String projectId);

    List<Task> findByAssignedUserId(String userId);

    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(String projectId, Priority priority);


    @Query("SELECT t FROM Task t WHERE t.project.owner.id = :userId " +
            "AND t.dueDate BETWEEN :start AND :end " +
            "AND t.status != 'DONE' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasksByOwner(@Param("userId") String userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);


    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId " +
            "AND t.dueDate BETWEEN :start AND :end " +
            "AND t.status != 'DONE' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasksByAssignee(@Param("userId") String userId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);
}
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

    // Yaklaşan taskler — sahibi
    @Query("SELECT t FROM Task t WHERE t.project.owner.id = :userId " +
            "AND t.dueDate BETWEEN :start AND :end " +
            "AND t.status != 'DONE' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasksByOwner(@Param("userId") String userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    // Yaklaşan taskler — assignee
    @Query("SELECT t FROM Task t WHERE t.assignedUser.id = :userId " +
            "AND t.dueDate BETWEEN :start AND :end " +
            "AND t.status != 'DONE' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasksByAssignee(@Param("userId") String userId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    // Proje içinde task arama
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Task> searchInProject(@Param("projectId") String projectId, @Param("query") String query);

    // Kullanıcının tüm erişebildiği tasklerde arama
    @Query("SELECT t FROM Task t WHERE " +
            "(t.project.owner.id = :userId OR t.assignedUser.id = :userId) " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Task> searchAllAccessible(@Param("userId") String userId, @Param("query") String query);

    // Filtreleme — durum + öncelik + assignee kombinasyonu
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:assignedUserId IS NULL OR t.assignedUser.id = :assignedUserId)")
    List<Task> filterByProject(@Param("projectId") String projectId,
                               @Param("status") TaskStatus status,
                               @Param("priority") Priority priority,
                               @Param("assignedUserId") String assignedUserId);
}
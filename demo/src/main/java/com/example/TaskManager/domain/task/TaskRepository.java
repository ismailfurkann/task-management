package com.example.TaskManager.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByProjectId(String projectId);

    List<Task> findByAssignedUserId(String userId);

    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(String projectId, Priority priority);
}
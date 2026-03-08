package com.example.TaskManager.domain.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {
    List<ActivityLog> findByTaskIdOrderByCreatedAtDesc(String taskId);
    List<ActivityLog> findByTask_Project_IdOrderByCreatedAtDesc(String projectId);
}
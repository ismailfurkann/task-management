package com.example.TaskManager.domain.activity;

import com.example.TaskManager.domain.activity.dto.ActivityLogResponseDto;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(Task task, User user, ActivityAction action) {
        ActivityLog log = new ActivityLog();
        log.setTask(task);
        log.setTaskTitle(task.getTitle()); // snapshot
        log.setUser(user);
        log.setAction(action);
        activityLogRepository.save(log);
    }

    public List<ActivityLogResponseDto> getByTask(String taskId) {
        return activityLogRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream().map(ActivityLogResponseDto::from).toList();
    }

    public List<ActivityLogResponseDto> getByProject(String projectId) {
        return activityLogRepository.findByTask_Project_IdOrderByCreatedAtDesc(projectId)
                .stream().map(ActivityLogResponseDto::from).toList();
    }
}
package com.example.TaskManager.domain.activity;

import com.example.TaskManager.domain.activity.dto.ActivityLogResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSOutput;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/tasks/{taskId}/activity")
    public ResponseEntity<List<ActivityLogResponseDto>> getTaskActivity(@PathVariable String taskId) {
        return ResponseEntity.ok(activityLogService.getByTask(taskId));
    }

    @GetMapping("/projects/{projectId}/activity")
    public ResponseEntity<List<ActivityLogResponseDto>> getProjectActivity(@PathVariable String projectId) {
        return ResponseEntity.ok(activityLogService.getByProject(projectId));
    }

}
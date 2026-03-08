package com.example.TaskManager.domain.comment;

import com.example.TaskManager.domain.activity.ActivityAction;
import com.example.TaskManager.domain.activity.ActivityLogService;
import com.example.TaskManager.domain.comment.dto.CommentRequestDto;
import com.example.TaskManager.domain.comment.dto.CommentResponseDto;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskRepository;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody CommentRequestDto dto) {

        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Comment comment = new Comment();
        comment.setMessage(dto.message());
        comment.setTask(task);
        comment.setUser(currentUser);

        Comment saved = commentRepository.save(comment);

        // Activity log
        activityLogService.log(task, currentUser, ActivityAction.ADDED_COMMENT);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponseDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable String taskId) {
        return ResponseEntity.ok(
                commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                        .stream()
                        .map(CommentResponseDto::from)
                        .toList()
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId) {

        String currentUserId = securityUtils.getCurrentUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Sadece kendi yorumunu silebilir
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
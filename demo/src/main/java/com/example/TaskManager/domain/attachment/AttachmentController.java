package com.example.TaskManager.domain.attachment;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.TaskManager.domain.attachment.dto.AttachmentResponseDto;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.task.TaskRepository;
import com.example.TaskManager.domain.user.User;
import com.example.TaskManager.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final Cloudinary cloudinary;
    private final SecurityUtils securityUtils;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AttachmentResponseDto> upload(
            @PathVariable String taskId,
            @RequestParam("file") MultipartFile file) throws IOException {

        User currentUser = securityUtils.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Dosya tipi kontrolü — sadece resim ve PDF
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new RuntimeException("Sadece resim ve PDF dosyaları yüklenebilir");
        }

        // Max 10MB kontrolü
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Dosya boyutu 10MB'ı geçemez");
        }

        // Cloudinary'ye yükle
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "fuce/tasks/" + taskId,
                        "resource_type", "auto"
                )
        );

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileUrl((String) uploadResult.get("secure_url"));
        attachment.setPublicId((String) uploadResult.get("public_id"));
        attachment.setFileType(contentType);
        attachment.setFileSize(file.getSize());
        attachment.setTask(task);
        attachment.setUploadedBy(currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AttachmentResponseDto.from(attachmentRepository.save(attachment)));
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponseDto>> getAttachments(@PathVariable String taskId) {
        return ResponseEntity.ok(
                attachmentRepository.findByTaskId(taskId)
                        .stream()
                        .map(AttachmentResponseDto::from)
                        .toList()
        );
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(
            @PathVariable String taskId,
            @PathVariable String attachmentId) throws IOException {

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Sadece yükleyen kişi veya proje sahibi silebilir
        String currentUserId = securityUtils.getCurrentUserId();
        boolean isUploader = attachment.getUploadedBy().getId().equals(currentUserId);
        boolean isOwner = attachment.getTask().getProject().getOwner().getId().equals(currentUserId);

        if (!isUploader && !isOwner) {
            throw new RuntimeException("Access denied");
        }

        // Cloudinary'den sil
        cloudinary.uploader().destroy(
                attachment.getPublicId(),
                ObjectUtils.asMap("resource_type", "auto")
        );

        attachmentRepository.deleteById(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
package com.example.TaskManager.domain.attachment.dto;

import com.example.TaskManager.domain.attachment.Attachment;

import java.time.LocalDateTime;

public record AttachmentResponseDto(
        String id,
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize,
        String uploadedById,
        String uploadedByName,
        LocalDateTime createdAt
) {
    public static AttachmentResponseDto from(Attachment attachment) {
        return new AttachmentResponseDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getUploadedBy().getId(),
                attachment.getUploadedBy().getName(),
                attachment.getCreatedAt()
        );
    }
}
package com.example.TaskManager.domain.attachment;

import com.example.TaskManager.common.base.BaseEntity;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attachments")
@Getter
@Setter
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;       // Cloudinary URL

    @Column(nullable = false)
    private String publicId;      // Cloudinary public_id (silmek için)

    @Column(nullable = false)
    private String fileType;      // image/png, application/pdf vb.

    @Column(nullable = false)
    private Long fileSize;        // byte cinsinden

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
}
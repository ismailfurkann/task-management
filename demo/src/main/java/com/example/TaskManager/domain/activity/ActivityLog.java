package com.example.TaskManager.domain.activity;

import com.example.TaskManager.common.base.BaseEntity;
import com.example.TaskManager.domain.task.Task;
import com.example.TaskManager.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
public class ActivityLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
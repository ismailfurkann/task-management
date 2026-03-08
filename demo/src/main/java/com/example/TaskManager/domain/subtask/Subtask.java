package com.example.TaskManager.domain.subtask;

import com.example.TaskManager.common.base.BaseEntity;
import com.example.TaskManager.domain.task.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
public class Subtask extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
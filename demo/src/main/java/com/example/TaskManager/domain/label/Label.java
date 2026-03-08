package com.example.TaskManager.domain.label;

import com.example.TaskManager.common.base.BaseEntity;
import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.task.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labels")
@Getter
@Setter
public class Label extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color; // hex renk kodu — örn: #FF5733

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
}
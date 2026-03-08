package com.example.TaskManager.domain.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, String> {
    List<Label> findByProjectId(String projectId);
}
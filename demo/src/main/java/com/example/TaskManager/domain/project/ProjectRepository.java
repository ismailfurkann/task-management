package com.example.TaskManager.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    // Kullanıcının sahibi olduğu projeler
    List<Project> findByOwnerId(String ownerId);

    boolean existsByNameAndOwnerId(String name, String ownerId);

    // Kullanıcının üye olduğu projeler (ACCEPTED)
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.status = 'ACCEPTED'")
    List<Project> findProjectsByMemberId(@Param("userId") String userId);
}
package com.example.TaskManager.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    List<Project> findByOwnerId(String ownerId);

    boolean existsByNameAndOwnerId(String name, String ownerId);

    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.status = 'ACCEPTED'")
    List<Project> findProjectsByMemberId(@Param("userId") String userId);

    // Arama — kullanıcının erişebildiği projeler içinde
    @Query("SELECT p FROM Project p WHERE p.owner.id = :userId " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchByOwner(@Param("userId") String userId, @Param("query") String query);

    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.id = :userId " +
            "AND pm.status = 'ACCEPTED' " +
            "AND (LOWER(pm.project.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(pm.project.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchByMember(@Param("userId") String userId, @Param("query") String query);
}
package com.example.TaskManager.domain.user;

import com.example.TaskManager.common.base.BaseEntity;
import com.example.TaskManager.domain.project.Project;
import com.example.TaskManager.domain.task.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<>();

    // UserDetails metodları
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // şimdilik rol yok
    }

    @Override
    public String getUsername() {
        return email; // Spring Security email'i username olarak kullanacak
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
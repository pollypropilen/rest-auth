package com.pollypropilen.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pollypropilen.web.model.UserPermissionRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.pollypropilen.web.payload.misc.Const.*;

@Entity
@Data
@Table(name = "sy_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String username;

    @Column(length = 4096, nullable = false)
    private String password;

    @ElementCollection(targetClass = UserPermissionRole.class)
    @CollectionTable(name = "sy_user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserPermissionRole> roles = new HashSet<>();

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column
    private LocalDateTime updatedDate;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column
    private LocalDateTime lastVisitedDate;

    @Column(length = 3)
    private String isAccountExpired;

    @Column(length = 3)
    private String isAccountLocked;

    @Column(length = 3)
    private String isCredentialsExpired;

    @Column(length = 3)
    private String isEnabled;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public User() {
    }

    public User(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.isEnabled = user.isEnabled;
        this.isCredentialsExpired = user.isCredentialsExpired;
        this.isAccountLocked = user.isAccountLocked;
        this.isAccountExpired = user.isAccountExpired;
        this.authorities = authorities;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime date = LocalDateTime.now();
        this.createdDate = date;
        this.updatedDate = date;
        this.lastVisitedDate = date;
        //auto
        this.isAccountExpired = NO;
        this.isAccountLocked = NO;
        this.isCredentialsExpired = NO;
        this.isEnabled = YES;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return NO.equals(this.isAccountExpired);
    }

    @Override
    public boolean isAccountNonLocked() {
        return NO.equals(this.isAccountLocked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return NO.equals(this.isCredentialsExpired);
    }

    @Override
    public boolean isEnabled() {
        return YES.equals(this.isEnabled);
    }
}
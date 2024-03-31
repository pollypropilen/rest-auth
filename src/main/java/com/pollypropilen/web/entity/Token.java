package com.pollypropilen.web.entity;

import com.pollypropilen.web.model.TokenStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sy_token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512, nullable = false, unique = true)
    private String token;

    @Column(length = 512, unique = true) //nullable = false,
    private String refresh;

    @Column(nullable = false)
    private TokenStatus status;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Token() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public Token(String token, TokenStatus status, User user) {
        this.token = token;
        this.status = status;
        this.user = user;
    }
}
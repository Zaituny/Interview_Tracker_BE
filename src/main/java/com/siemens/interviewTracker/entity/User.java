package com.siemens.interviewTracker.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.util.Collection;
import java.util.Collections;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(min = 4, max = 50)
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_date")
    private LocalDateTime resetPasswordTokenDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // no roles are assigned to users
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}

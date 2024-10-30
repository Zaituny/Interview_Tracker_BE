package com.siemens.interviewTracker.repository;

import java.util.UUID;
import java.util.Optional;
import com.siemens.interviewTracker.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

}

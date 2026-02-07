package org.example.zupaybackend.repository;

import org.example.zupaybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByUniqueUserId(String uniqueUserId);
}
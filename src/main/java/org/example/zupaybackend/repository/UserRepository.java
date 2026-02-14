package org.example.zupaybackend.repository;

import org.example.zupaybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByUniqueUserId(String uniqueUserId);
    Optional<User> findByUsername(String username);
}
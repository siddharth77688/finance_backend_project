package com.finance.demo.repository;

import com.finance.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query method to find a user by their username, returning an Optional to handle the case where the user may not exist.
    Optional<User> findByUsername(String username);
    
    // Custom query method to find a user by their email, returning an Optional to handle the case where the user may not exist.
    Optional<User> findByEmail(String email);
    
    // Custom query method to check if a user exists with a specific username.
    boolean existsByUsername(String username);
    // Custom query method to check if a user exists with a specific email.
    boolean existsByEmail(String email);
}

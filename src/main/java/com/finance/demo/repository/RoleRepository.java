package com.finance.demo.repository;

import com.finance.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Custom query method to find a role by its name, returning an Optional to handle the case where the role may not exist.
    Optional<Role> findByName(Role.RoleName name);
    boolean existsByName(Role.RoleName name);
}

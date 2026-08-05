package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.AppRole;
import com.abdelrahman027.sbecom2.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRoleName(AppRole roleName);
}

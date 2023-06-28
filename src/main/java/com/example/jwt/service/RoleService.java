package com.example.jwt.service;


import com.example.jwt.model.Role;
import com.example.jwt.model.RoleName;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName name);
}

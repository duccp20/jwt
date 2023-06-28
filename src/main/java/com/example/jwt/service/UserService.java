package com.example.jwt.service;

import com.example.jwt.model.User;


import java.util.Optional;


public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByPhone(String phoneNumber);
    Optional<User> findByUsername(String username);

    User save(User user);
}

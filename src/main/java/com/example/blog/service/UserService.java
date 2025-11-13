package com.example.blog.service;

import com.example.blog.dto.UserDto;
import com.example.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User register(String username, String password, String email);

    Optional<User> getById(Long id);

    Optional<User> getByEmail(String email);

    Page<UserDto> searchByUsername(String query, Pageable pageable);
}

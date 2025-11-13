package com.example.blog.controller;

import com.example.blog.dto.UserDto;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isEmpty()) return ResponseEntity.notFound().build();
        User user = userOptional.get();
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }

    @GetMapping("/search")
    public Page<UserDto> searchByUsername(@RequestParam(name = "q", required = false) String q,
                                          @ParameterObject @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return userService.searchByUsername(q, pageable);
    }
}

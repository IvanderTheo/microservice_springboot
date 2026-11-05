package com.example.user.controller;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.LoginUserRequest;
import com.example.user.dto.UpdateUserRequest;
import com.example.user.dto.UserResponse;
import com.example.user.model.User;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/auth"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> index() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse show(@PathVariable Long id) {
        return userService.findById(id);
    }

    //register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        return userService.register(request);
    }

    //login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoginResponse login(@Valid @RequestBody LoginUserRequest request) {
        return userService.login(request);
    }
    
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {
        return userService.update(id, user);
    }

    @PatchMapping("/{id}")
    public UserResponse patch(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void destroy(@PathVariable Long id) {
        userService.delete(id);
    }
}
package com.example.user.controller;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginUserRequest;
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
    public List<User> index() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User show(@PathVariable Long id) {
        return userService.findById(id);
    }

    //register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody CreateUserRequest request) {
        return userService.register(request);
    }

    //login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User login(@Valid @RequestBody LoginUserRequest request) {
        return userService.login(request);
    }
    
    @PutMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userService.delete(id);
    }
}
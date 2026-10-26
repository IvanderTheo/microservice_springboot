
package com.example.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginUserRequest;
import com.example.user.model.User;
import com.example.user.service.UserService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;

@OpenAPIDefinition(
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "API Gateway"
        )
    }
)
@RestController
@RequestMapping("/api/users")
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

    // Register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(
            @Valid @RequestBody CreateUserRequest request) {
        return userService.register(request);
    }

    // Login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User login(
            @Valid @RequestBody LoginUserRequest request) {
        return userService.login(request);
    }

    // PUT: memperbarui seluruh data profil
    @PutMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @RequestBody User data) {
        return userService.update(id, data);
    }

    // PATCH: memperbarui sebagian data profil
    @PatchMapping("/{id}")
    public User patch(
            @PathVariable Long id,
            @RequestBody User data) {
        return userService.patch(id, data);
    }

    // DELETE: menghapus user berdasarkan ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userService.delete(id);
    }
}

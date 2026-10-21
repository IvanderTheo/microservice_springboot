package com.example.user.service;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginUserRequest;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    public User register(CreateUserRequest request) {

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole("guest"); // default role
        
        return userRepository.save(user);
    }

    public User login(LoginUserRequest request) {
        User user = new User();

        return userRepository.save(user);
    }

    public User update(Long id, User data) {
        User user = findById(id);

        user.setName(data.getName());
        user.setEmail(data.getEmail());
        user.setPassword(data.getPassword());
        user.setRole(data.getRole());

        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
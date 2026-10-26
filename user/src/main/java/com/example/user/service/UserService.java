
package com.example.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.LoginUserRequest;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;

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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User register(CreateUserRequest request) {
        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole("guest");

        return userRepository.save(user);
    }

    public User login(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException(
                        "Email atau password salah"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Email atau password salah");
        }

        return user;
    }

    // PUT: mengganti seluruh data profil
    @Transactional
    public User update(Long id, User data) {
        User user = findById(id);

        if (isBlank(data.getName())
                || isBlank(data.getEmail())
                || isBlank(data.getPassword())
                || isBlank(data.getRole())) {
            throw new IllegalArgumentException(
                    "Name, email, password, and role are required for PUT");
        }

        user.setName(data.getName().trim());
        user.setEmail(data.getEmail().trim());
        user.setPassword(data.getPassword());
        user.setRole(data.getRole().trim());

        return userRepository.save(user);
    }

    // PATCH: hanya memperbarui field yang dikirim
    @Transactional
    public User patch(Long id, User data) {
        User user = findById(id);

        if (data.getName() != null) {
            if (isBlank(data.getName())) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
            user.setName(data.getName().trim());
        }

        if (data.getEmail() != null) {
            if (isBlank(data.getEmail())) {
                throw new IllegalArgumentException("Email cannot be blank");
            }
            user.setEmail(data.getEmail().trim());
        }

        if (data.getPassword() != null) {
            if (isBlank(data.getPassword())) {
                throw new IllegalArgumentException("Password cannot be blank");
            }
            user.setPassword(data.getPassword());
        }

        if (data.getRole() != null) {
            if (isBlank(data.getRole())) {
                throw new IllegalArgumentException("Role cannot be blank");
            }
            user.setRole(data.getRole().trim());
        }

        return userRepository.save(user);
    }

    // DELETE: menghapus user berdasarkan ID
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

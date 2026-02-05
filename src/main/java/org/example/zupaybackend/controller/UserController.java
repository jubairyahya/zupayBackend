package org.example.zupaybackend.controller;

import org.example.zupaybackend.model.User;
import org.example.zupaybackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        // TODO: In production, hash the password before saving
        return userRepository.save(user);
    }


    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
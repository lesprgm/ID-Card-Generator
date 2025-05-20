package com.idcard.idcardsystem.controller;

import java.util.Optional;
import com.idcard.idcardsystem.repository.UserRepository;
import com.idcard.idcardsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserbyId(@PathVariable Long id) {
        return userRepository.findById(id);
    }
}

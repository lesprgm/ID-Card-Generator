package com.idcard.idcardsystem.controller;

import com.idcard.idcardsystem.model.User;
import com.idcard.idcardsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserWithPhoto(
            @RequestParam("fullName") String fullName,
            @RequestParam("age") int age,
            @RequestParam("designation") String designation,
            @RequestParam("joiningDate") String joiningDate,
            @RequestParam("profilePicture") MultipartFile profilePicture
    ) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename(); // I gave the image a unique name because multiple users could have the same exact name and designation
            Path filepath = Paths.get(uploadDir, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, profilePicture.getBytes());

            User user = new User();
            user.setFullName(fullName);
            user.setAge(age);
            user.setDesignation(designation);
            user.setJoiningDate(LocalDate.parse(joiningDate));
            user.setProfilePicturePath("/uploads/" + filename);
            userRepository.save(user);

            return ResponseEntity.ok("User created successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }
    }
}

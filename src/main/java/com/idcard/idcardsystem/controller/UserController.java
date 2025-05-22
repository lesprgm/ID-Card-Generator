package com.idcard.idcardsystem.controller;

import com.idcard.idcardsystem.model.User;
import com.idcard.idcardsystem.repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userRepository.save(user);
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
            String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
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

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getUserPDF(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("ID Card").setBold().setFontSize(18));
            document.add(new Paragraph("Full Name: " + user.getFullName()));
            document.add(new Paragraph("Designation: " + user.getDesignation()));
            document.add(new Paragraph("Age: " + user.getAge()));
            document.add(new Paragraph("Joining Date: " + user.getJoiningDate()));

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=idcard-" + user.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

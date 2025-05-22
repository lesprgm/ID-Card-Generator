package com.idcard.idcardsystem.controller;

import com.idcard.idcardsystem.model.User;
import com.idcard.idcardsystem.repository.UserRepository;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.*;
//import com.itextpdf.layout.properties.HorizontalAlignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
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

            PageSize cardSize = new PageSize(300, 180);
            pdf.setDefaultPageSize(cardSize);

            Document document = new Document(pdf);
            document.setMargins(10, 10, 10, 10);

            pdf.addNewPage();

            PdfCanvas canvasFront = new PdfCanvas(pdf.getFirstPage());
            canvasFront.setFillColor(ColorConstants.BLUE);
            canvasFront.rectangle(0, cardSize.getHeight() - 20, cardSize.getWidth(), 20);
            canvasFront.fill();

            canvasFront.setFillColor(ColorConstants.BLUE);
            canvasFront.rectangle(0, 0, cardSize.getWidth(), 20);
            canvasFront.fill();


            Paragraph header = new Paragraph("\uD83D\uDCBC Employee ID Card")
                    .setFontColor(ColorConstants.WHITE)
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT);
            document.showTextAligned(header, 15, cardSize.getHeight() - 20, TextAlignment.LEFT);

            document.add(new Paragraph("Company Name")
                    .setBold()
                    .setFontSize(15)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(11));

            //document.add(new Paragraph("\n"));

            PdfCanvas roundedBorder = new PdfCanvas(pdf.getFirstPage());
            roundedBorder.setLineWidth(1.5f);
            roundedBorder.setStrokeColor(ColorConstants.GRAY);
            roundedBorder.roundRectangle(5, 5, cardSize.getWidth() - 10, cardSize.getHeight() - 10, 10);
            roundedBorder.stroke();

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();
            table.setBorder(Border.NO_BORDER);

            String imagePath = System.getProperty("user.dir") + user.getProfilePicturePath();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image profileImage = new Image(imageData);
                profileImage.setWidth(70)
                        .setHeight(70)
                        .setBorderRadius(new BorderRadius(35));
                Cell imageCell = new Cell().add(profileImage).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
                table.addCell(imageCell);
            } else {
                table.addCell(new Cell().add(new Paragraph("No Photo")).setBorder(Border.NO_BORDER));
            }

            Paragraph info = new Paragraph()
                    .add(new Text(user.getFullName()).setFontSize(14).setBold())
                    .add("\n")
                    .add("Designation: " + user.getDesignation() + "\n")
                    .add("Age: " + user.getAge() + "\n")
                    .add("Join Date: " + user.getJoiningDate());
            Cell infoCell = new Cell().add(info).setBorder(Border.NO_BORDER);
            table.addCell(infoCell);

            document.add(table);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            PdfCanvas canvasBack = new PdfCanvas(pdf.getLastPage());
            canvasBack.setFillColor(ColorConstants.BLUE);
            canvasBack.rectangle(0, 0, cardSize.getWidth(), cardSize.getHeight());
            canvasBack.fill();

            document.add(new Paragraph("This card is property of the company and must be returned upon request.")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(70));


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

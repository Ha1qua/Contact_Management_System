package com.haiqua.backend.controller;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // ---------------- CREATE CONTACT ----------------
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponseDto>> createContact(
            @RequestBody CreateContactRequestDto request) {

        ContactResponseDto response = contactService.createContact(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Contact created successfully", response)
        );
    }

    // ---------------- GET ALL CONTACTS ----------------
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContactResponseDto>>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        Page<ContactResponseDto> response =
                contactService.getAllContactsByUser(page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Contacts fetched successfully", response)
        );
    }

    // ---------------- UPDATE CONTACT ----------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponseDto>> updateContact(
            @PathVariable Long id,
            @RequestBody UpdateContactRequestDto request) {

        ContactResponseDto response = contactService.updateContact(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Contact updated successfully", response)
        );
    }

    // ---------------- DELETE CONTACT ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {

        contactService.deleteContact(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Contact deleted successfully", null)
        );
    }

    // ---------------- IMPORT CONTACTS ----------------
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importContacts(
            @RequestParam("file") MultipartFile file) {

        contactService.importContacts(file);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Contacts imported successfully", "SUCCESS")
        );
    }

    // ---------------- EXPORT CONTACTS ----------------
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {

        ByteArrayInputStream data = contactService.exportContacts();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=contacts.csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data.readAllBytes());
    }
}
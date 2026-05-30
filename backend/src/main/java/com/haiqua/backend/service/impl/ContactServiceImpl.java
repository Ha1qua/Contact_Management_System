package com.haiqua.backend.service.impl;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.entity.Contact;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.exception.ContactNotFoundException;
import com.haiqua.backend.exception.UnauthorizedAccessException;
import com.haiqua.backend.mapper.ContactMapper;
import com.haiqua.backend.repository.ContactRepository;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.security.SecurityUtils;
import com.haiqua.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    @Autowired
    private SecurityUtils securityUtils;



    // ---------------- CREATE ----------------
    @Override
    public ContactResponseDto createContact(CreateContactRequestDto request) {

        User user = securityUtils.getLoggedInUser();

        Contact contact = ContactMapper.mapToEntity(request, user);

        return ContactMapper.mapToDto(contactRepository.save(contact));
    }

    // ---------------- GET ALL ----------------
    @Override
    public Page<ContactResponseDto> getAllContactsByUser(int page, int size) {

        User user = securityUtils.getLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Contact> contacts = contactRepository.findByUserId(user.getId(), pageable);

        return contacts.map(ContactMapper::mapToDto);
    }

    // ---------------- UPDATE ----------------
    @Override
    public ContactResponseDto updateContact(Long id, UpdateContactRequestDto request) {

        User user = securityUtils.getLoggedInUser();

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You cannot access this contact");
        }

        ContactMapper.updateEntity(contact, request);

        return ContactMapper.mapToDto(contactRepository.save(contact));
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteContact(Long id) {

        User user = securityUtils.getLoggedInUser();

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You cannot access this contact");
        }

        contactRepository.delete(contact);
    }

    // ---------------- IMPORT ----------------
    @Override
    public void importContacts(MultipartFile file) {

        System.out.println("IMPORT STARTED");
        System.out.println("File name: " + file.getOriginalFilename());
        User user = securityUtils.getLoggedInUser();
        System.out.println("User: " + user);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length < 4) continue;

                Contact contact = new Contact();
                contact.setFirstName(data[0].trim());
                contact.setLastName(data[1].trim());
                contact.setEmail(data[2].trim());
                contact.setPhone(data[3].trim());

                // 🔐 IMPORTANT: assign owner (same rule as delete)
                contact.setUser(user);

                contactRepository.save(contact);
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV import failed: " + e.getMessage());
        }
    }
    // ---------------- EXPORT ----------------
    @Override
    public ByteArrayInputStream exportContacts() {

        User user = securityUtils.getLoggedInUser();

        List<Contact> contacts =
                contactRepository.findByUserId(user.getId(), Pageable.unpaged())
                        .getContent();

        StringBuilder sb = new StringBuilder();
        sb.append("FirstName,LastName,Email,Phone\n");

        for (Contact c : contacts) {
            sb.append(c.getFirstName()).append(",")
                    .append(c.getLastName()).append(",")
                    .append(c.getEmail()).append(",")
                    .append(c.getPhone()).append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
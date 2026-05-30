package com.haiqua.backend.service;

import com.haiqua.backend.dto.ContactResponseDto;
import com.haiqua.backend.dto.CreateContactRequestDto;
import com.haiqua.backend.dto.UpdateContactRequestDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;

public interface ContactService {

    ContactResponseDto createContact(CreateContactRequestDto request);

    Page<ContactResponseDto> getAllContactsByUser(int page, int size);

    ContactResponseDto updateContact(Long id, UpdateContactRequestDto request);

    void deleteContact(Long id);

    void importContacts(MultipartFile file);

    ByteArrayInputStream exportContacts();
}
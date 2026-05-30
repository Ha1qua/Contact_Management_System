package com.haiqua.backend.mapper;

import com.haiqua.backend.dto.CreateContactRequestDto;
import com.haiqua.backend.dto.ContactResponseDto;
import com.haiqua.backend.dto.UpdateContactRequestDto;
import com.haiqua.backend.entity.Contact;
import com.haiqua.backend.entity.User;

public class ContactMapper {

    // Entity → DTO (for response)
    public static ContactResponseDto mapToDto(Contact contact) {
        return new ContactResponseDto(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone()
        );
    }

    // Create DTO → Entity
    public static Contact mapToEntity(CreateContactRequestDto dto, User user) {
        Contact contact = new Contact();
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setUser(user);
        return contact;
    }

    // Update DTO → update existing entity
    public static void updateEntity(Contact contact, UpdateContactRequestDto dto) {
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
    }
}
package com.haiqua.backend.service;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.entity.Contact;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.exception.ContactNotFoundException;
import com.haiqua.backend.exception.UnauthorizedAccessException;
import com.haiqua.backend.mapper.ContactMapper;
import com.haiqua.backend.repository.ContactRepository;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.security.SecurityUtils;
import com.haiqua.backend.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @InjectMocks
    private ContactServiceImpl contactService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    private User user;
    private Contact contact;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        contact = new Contact();
        contact.setId(10L);
        contact.setUser(user);
    }

    // ---------------- CREATE ----------------
    @Test
    void createContact_shouldReturnResponse() {

        CreateContactRequestDto request = new CreateContactRequestDto();

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDto responseDto = new ContactResponseDto();

        try (MockedStatic<ContactMapper> mocked = Mockito.mockStatic(ContactMapper.class)) {

            mocked.when(() -> ContactMapper.mapToEntity(request, user))
                    .thenReturn(contact);

            mocked.when(() -> ContactMapper.mapToDto(contact))
                    .thenReturn(responseDto);

            ContactResponseDto result = contactService.createContact(request);

            assertNotNull(result);
            verify(contactRepository).save(any(Contact.class));
        }
    }

    // ---------------- GET ALL ----------------
    @Test
    void getAllContacts_shouldReturnPage() {

        when(securityUtils.getLoggedInUser()).thenReturn(user);

        Page<Contact> page = new PageImpl<>(List.of(contact));

        when(contactRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        Page<ContactResponseDto> result =
                contactService.getAllContactsByUser(0, 5);

        assertEquals(1, result.getContent().size());
    }

    // ---------------- UPDATE SUCCESS ----------------
    @Test
    void updateContact_shouldUpdateSuccessfully() {

        UpdateContactRequestDto request = new UpdateContactRequestDto();

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDto responseDto = new ContactResponseDto();

        try (MockedStatic<ContactMapper> mocked = Mockito.mockStatic(ContactMapper.class)) {

            mocked.when(() -> ContactMapper.updateEntity(contact, request))
                    .thenAnswer(invocation -> null);

            mocked.when(() -> ContactMapper.mapToDto(contact))
                    .thenReturn(responseDto);

            ContactResponseDto result = contactService.updateContact(10L, request);

            assertNotNull(result);
            verify(contactRepository).save(contact);
        }
    }

    // ---------------- UPDATE NOT FOUND (NEW FIXED TEST) ----------------
    @Test
    void updateContact_shouldThrowNotFound() {

        UpdateContactRequestDto request = new UpdateContactRequestDto();

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class,
                () -> contactService.updateContact(10L, request));
    }

    // ---------------- DELETE SUCCESS ----------------
    @Test
    void deleteContact_shouldDeleteSuccessfully() {

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        contactService.deleteContact(10L);

        verify(contactRepository).delete(contact);
    }

    // ---------------- DELETE UNAUTHORIZED ----------------
    @Test
    void deleteContact_shouldThrowUnauthorized() {

        User otherUser = new User();
        otherUser.setId(2L);

        contact.setUser(otherUser);

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        assertThrows(UnauthorizedAccessException.class,
                () -> contactService.deleteContact(10L));
    }

    // ---------------- DELETE NOT FOUND ----------------
    @Test
    void deleteContact_shouldThrowNotFound() {

        when(securityUtils.getLoggedInUser()).thenReturn(user);
        when(contactRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class,
                () -> contactService.deleteContact(10L));
    }

    // ---------------- EXPORT ----------------
    @Test
    void exportContacts_shouldReturnValidCsv() {

        when(securityUtils.getLoggedInUser()).thenReturn(user);

        Page<Contact> page = new PageImpl<>(List.of(contact));

        when(contactRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        ByteArrayInputStream result = contactService.exportContacts();

        String output = new String(result.readAllBytes());

        assertTrue(output.contains("FirstName,LastName,Email,Phone"));
    }
}
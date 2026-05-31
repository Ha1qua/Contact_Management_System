package com.haiqua.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiqua.backend.config.TestSecurityConfig;
import com.haiqua.backend.dto.*;
import com.haiqua.backend.exception.GlobalExceptionHandler;
import com.haiqua.backend.service.ContactService;
import com.haiqua.backend.service.JwtService; // ✅ Added import
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ✅ Updated for Spring Boot 3.4+

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class) // ✅ Ensured test security settings match
@WebMvcTest({ContactController.class, GlobalExceptionHandler.class}) // ✅ Included exception advice context
@AutoConfigureMockMvc(addFilters = false) // ✅ Disabled filters for explicit slice focus
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // ✅ Upgraded deprecated @MockBean to @MockitoBean
    private ContactService contactService;

    @MockitoBean // ✅ Added to prevent JwtAuthenticationFilter context load failures
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= CREATE =================
    @Test
    void createContact_shouldReturnSuccess() throws Exception {
        CreateContactRequestDto request = new CreateContactRequestDto();
        ContactResponseDto response = new ContactResponseDto();

        when(contactService.createContact(any())).thenReturn(response);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contact created successfully"));
    }

    // ================= GET ALL =================
    @Test
    void getAllContacts_shouldReturnPage() throws Exception {
        ContactResponseDto dto = new ContactResponseDto();
        Page<ContactResponseDto> page = new PageImpl<>(List.of(dto));

        when(contactService.getAllContactsByUser(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").exists());
    }

    // ================= UPDATE =================
    @Test
    void updateContact_shouldReturnSuccess() throws Exception {
        UpdateContactRequestDto request = new UpdateContactRequestDto();
        ContactResponseDto response = new ContactResponseDto();

        when(contactService.updateContact(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact updated successfully"));
    }

    // ================= DELETE =================
    @Test
    void deleteContact_shouldReturnSuccess() throws Exception {
        doNothing().when(contactService).deleteContact(1L);

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact deleted successfully"));
    }

    // ================= IMPORT =================
    @Test
    void importContacts_shouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contacts.csv",
                "text/csv",
                "firstName,lastName,email,phone\nAli,Khan,ali@gmail.com,12345".getBytes()
        );

        doNothing().when(contactService).importContacts(any());

        mockMvc.perform(multipart("/api/contacts/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contacts imported successfully"));
    }

    // ================= EXPORT =================
    @Test
    void exportContacts_shouldReturnCsvFile() throws Exception {
        byte[] csvData = "FirstName,LastName,Email,Phone\nAli,Khan,ali@gmail.com,12345".getBytes();

        when(contactService.exportContacts())
                .thenReturn(new ByteArrayInputStream(csvData));

        mockMvc.perform(get("/api/contacts/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=contacts.csv"))
                .andExpect(content().contentType("text/csv"));
    }
}
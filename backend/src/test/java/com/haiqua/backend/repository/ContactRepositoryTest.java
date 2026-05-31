package com.haiqua.backend.repository;

import com.haiqua.backend.entity.Contact;
import com.haiqua.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ================= FIND BY USER ID WITH PAGINATION =================
    @Test
    void findByUserId_shouldReturnPagedContactsForSpecificUser() {
        // 1. Arrange: Set up two distinct users
        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setPassword("123");
        entityManager.persist(user1);

        User user2 = new User();
        user2.setEmail("user2@gmail.com");
        user2.setPassword("123");
        entityManager.persist(user2);

        // 2. Arrange: Create contacts assigned to different owners
        Contact contact1 = new Contact();
        contact1.setFirstName("Ali");
        contact1.setLastName("Khan");
        contact1.setEmail("ali@gmail.com");
        contact1.setPhone("11111");
        contact1.setUser(user1); // Belongs to User 1
        entityManager.persist(contact1);

        Contact contact2 = new Contact();
        contact2.setFirstName("Zain");
        contact2.setLastName("Ahmed");
        contact2.setEmail("zain@gmail.com");
        contact2.setPhone("22222");
        contact2.setUser(user1); // Belongs to User 1
        entityManager.persist(contact2);

        Contact contact3 = new Contact();
        contact3.setFirstName("Stranger");
        contact3.setLastName("Danger");
        contact3.setEmail("stranger@gmail.com");
        contact3.setPhone("33333");
        contact3.setUser(user2); // Belongs to User 2 (Should be filtered out)
        entityManager.persist(contact3);

        entityManager.flush();

        // 3. Act: Query Page 0 with a page size of 5 for User 1
        Pageable pageable = PageRequest.of(0, 5);
        Page<Contact> contactPage = contactRepository.findByUserId(user1.getId(), pageable);

        // 4. Assertions
        assertThat(contactPage).isNotNull();
        assertThat(contactPage.getTotalElements()).isEqualTo(2); // Only User 1's records matched
        assertThat(contactPage.getContent()).extracting(Contact::getFirstName)
                .containsExactlyInAnyOrder("Ali", "Zain")
                .doesNotContain("Stranger");
    }
}
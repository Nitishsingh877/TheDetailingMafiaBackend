package com.thederailingmafia.carwash.user_service.repository;

import com.thederailingmafia.carwash.user_service.model.Admin;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void save_Success() {
        UserModel user = new UserModel();
        user.setEmail("admin@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setUser(user);

        Admin saved = adminRepository.save(admin);

        assertNotNull(saved.getId());
        assertEquals("Admin User", saved.getName());
    }

    @Test
    void findById_Success() {
        UserModel user = new UserModel();
        user.setEmail("admin@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        Admin admin = new Admin();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setUser(user);
        entityManager.persist(admin);
        entityManager.flush();

        Admin found = adminRepository.findById(admin.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Admin User", found.getName());
    }
}

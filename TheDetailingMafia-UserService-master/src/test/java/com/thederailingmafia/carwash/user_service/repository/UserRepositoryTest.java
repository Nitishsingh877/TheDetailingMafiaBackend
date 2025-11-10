package com.thederailingmafia.carwash.user_service.repository;

import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_Success() {
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUserRole(UserRole.CUSTOMER);
        entityManager.persist(user);
        entityManager.flush();

        Optional<UserModel> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_NotFound() {
        Optional<UserModel> found = userRepository.findByEmail("notfound@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmail_True() {
        UserModel user = new UserModel();
        user.setEmail("exists@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_False() {
        boolean exists = userRepository.existsByEmail("notexists@example.com");

        assertFalse(exists);
    }

    @Test
    void findByUserRole_Success() {
        UserModel customer = new UserModel();
        customer.setEmail("customer@example.com");
        customer.setPassword("password");
        customer.setUserRole(UserRole.CUSTOMER);

        UserModel washer = new UserModel();
        washer.setEmail("washer@example.com");
        washer.setPassword("password");
        washer.setUserRole(UserRole.WASHER);

        entityManager.persist(customer);
        entityManager.persist(washer);
        entityManager.flush();

        List<UserModel> customers = userRepository.findByUserRole(UserRole.CUSTOMER);

        assertEquals(1, customers.size());
        assertEquals("customer@example.com", customers.get(0).getEmail());
    }
}

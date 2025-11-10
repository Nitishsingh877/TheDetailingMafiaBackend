package com.thederailingmafia.carwash.user_service.repository;

import com.thederailingmafia.carwash.user_service.model.Customer;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void save_Success() {
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        Customer customer = new Customer("John Doe", "123 Street", "1234567890", "test@example.com");
        customer.setUser(user);

        Customer saved = customerRepository.save(customer);

        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getName());
    }

    @Test
    void findById_Success() {
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        Customer customer = new Customer("John Doe", "123 Street", "1234567890", "test@example.com");
        customer.setUser(user);
        entityManager.persist(customer);
        entityManager.flush();

        Customer found = customerRepository.findById(customer.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }
}

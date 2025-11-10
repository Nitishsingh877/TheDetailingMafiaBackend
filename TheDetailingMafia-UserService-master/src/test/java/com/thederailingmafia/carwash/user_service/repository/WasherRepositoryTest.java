package com.thederailingmafia.carwash.user_service.repository;

import com.thederailingmafia.carwash.user_service.model.Washer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WasherRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WasherRepository washerRepository;

    @Test
    void findByWasherEmail_Success() {
        Washer washer = new Washer("John Washer", "washer@example.com", null, null, true);
        entityManager.persist(washer);
        entityManager.flush();

        Optional<Washer> found = washerRepository.findByWasherEmail("washer@example.com");

        assertTrue(found.isPresent());
        assertEquals("John Washer", found.get().getWasherName());
    }

    @Test
    void findByWasherEmail_NotFound() {
        Optional<Washer> found = washerRepository.findByWasherEmail("notfound@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void findByIsActiveTrue_Success() {
        Washer active = new Washer("Active Washer", "active@example.com", null, null, true);
        Washer inactive = new Washer("Inactive Washer", "inactive@example.com", null, null, false);
        entityManager.persist(active);
        entityManager.persist(inactive);
        entityManager.flush();

        List<Washer> activeWashers = washerRepository.findByIsActiveTrue();

        assertEquals(1, activeWashers.size());
        assertEquals("Active Washer", activeWashers.get(0).getWasherName());
    }
}

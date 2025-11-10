package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.WasherDto;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.Washer;
import com.thederailingmafia.carwash.user_service.repository.WasherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WasherServiceTest {

    @Mock
    private WasherRepository washerRepository;

    @InjectMocks
    private WasherService washerService;

    @Test
    void SaveWasher_Success() {
        UserModel user = new UserModel();
        WasherDto dto = new WasherDto();
        dto.setWasherName("Washer Name");
        dto.setWasherEmail("washer@example.com");
        dto.setIsActive(true);

        when(washerRepository.save(any(Washer.class))).thenReturn(new Washer());

        washerService.SaveWasher(user, dto);

        verify(washerRepository, times(1)).save(any(Washer.class));
    }

    @Test
    void updateWasherStatus_Success() {
        Washer washer = new Washer();
        washer.setWasherEmail("washer@example.com");
        washer.setActive(false);

        when(washerRepository.findByWasherEmail("washer@example.com")).thenReturn(Optional.of(washer));
        when(washerRepository.save(any(Washer.class))).thenReturn(washer);

        Washer result = washerService.updateWasherStatus("washer@example.com", true);

        assertTrue(result.isActive());
        verify(washerRepository, times(1)).save(washer);
    }

    @Test
    void updateWasherStatus_WasherNotFound() {
        when(washerRepository.findByWasherEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            washerService.updateWasherStatus("notfound@example.com", true)
        );
    }
}

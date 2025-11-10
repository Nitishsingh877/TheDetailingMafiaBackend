package com.thederailingmafia.carwash.user_service.dto;



import lombok.Data;
import java.util.List;

@Data
public class WasherResponse {
    private List<String> washers;
    private boolean fallbackTriggered;

    public WasherResponse(List<String> washers, boolean fallbackTriggered) {
        this.washers = washers;
        this.fallbackTriggered = fallbackTriggered;
    }
}

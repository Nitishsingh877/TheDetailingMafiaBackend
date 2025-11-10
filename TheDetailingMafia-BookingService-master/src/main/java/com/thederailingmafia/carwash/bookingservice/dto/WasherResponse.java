package com.thederailingmafia.carwash.bookingservice.dto;

import lombok.Data;
import lombok.Getter;

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

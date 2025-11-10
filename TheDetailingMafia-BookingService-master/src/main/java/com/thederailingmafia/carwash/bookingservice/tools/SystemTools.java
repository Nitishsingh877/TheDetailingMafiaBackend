//package com.thederailingmafia.carwash.bookingservice.tools;
//
//
//
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Component
//public class SystemTools {
//
//    @Tool(name = "getWeather", description = "Get current weather for a city")
//    public String getWeather(String city) {
//        // In real life, call a weather API here
//        return "The weather in " + city + " is sunny, 25°C.";
//    }
//    @Tool(name = "getCurrentDate", description = "Returns today's date in YYYY-MM-DD format")
//    public String getCurrentDate() {
//        return LocalDate.now().toString();
//    }
//
//    @Tool(name = "getCurrentDateTime", description = "Returns the current date and time in a readable format")
//    public String getCurrentDateTime() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");
//        return LocalDateTime.now().format(formatter);
//    }
//}
//

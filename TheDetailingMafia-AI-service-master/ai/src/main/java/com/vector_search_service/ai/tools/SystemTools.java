package com.vector_search_service.ai.tools;

import com.vector_search_service.ai.dto.CarResponse;
import com.vector_search_service.ai.dto.OrderResponse;
import com.vector_search_service.ai.dto.WashRequestResponse;
import com.vector_search_service.ai.service.ChatbotService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class SystemTools {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChatbotService chatbotService;

    private String currentUserToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "bc5993a21b014ef6964111423251809";



    @Tool(name = "getCurrentDate", description = "Returns today's date in YYYY-MM-DD format")
    public String getCurrentDate() {
        return LocalDate.now().toString();
    }

    @Tool(name = "getCurrentDateTime", description = "Returns the current date and time in a readable format")
    public String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @Tool(name = "summarizePDF", description = "Summarize an ingested PDF by filename")
    public String summarizePDF(String filename) {
        String sql = "SELECT content FROM document_embeddings WHERE filename = ? LIMIT 5";
        return jdbcTemplate.queryForList(sql, filename).toString();
    }


    @Tool(name = "getWeather", description = "Get current weather for a city")
    public String getWeather(String city) {
        String url = "https://api.weatherapi.com/v1/current.json?q=" + city + "&key=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("current") && body.containsKey("location")) {
                Map<String, Object> location = (Map<String, Object>) body.get("location");
                Map<String, Object> current = (Map<String, Object>) body.get("current");
                Map<String, Object> condition = (Map<String, Object>) current.get("condition");

                String cityName = location.get("name").toString();
                String region = location.get("region").toString();
                String country = location.get("country").toString();
                String localTime = location.get("localtime").toString();

                double tempC = (Double) current.get("temp_c");
                double feelsLikeC = (Double) current.get("feelslike_c");
                String weatherText = condition.get("text").toString();
                int humidity = (Integer) current.get("humidity");
                double windKph = (Double) current.get("wind_kph");
                String windDir = current.get("wind_dir").toString();

                return String.format(
                        "📍 Weather in %s, %s (%s)\n" +
                                "🕒 Local Time: %s\n" +
                                "🌡️ Temperature: %.1f°C (Feels like %.1f°C)\n" +
                                "🌥️ Condition: %s\n" +
                                "💧 Humidity: %d%%\n" +
                                "💨 Wind: %.1f kph from %s",
                        cityName, region, country,
                        localTime,
                        tempC, feelsLikeC,
                        weatherText,
                        humidity,
                        windKph, windDir
                );
            } else {
                return "Weather data not available for " + city + ".";
            }
        } catch (Exception e) {
            return "Error fetching weather for " + city + ": " + e.getMessage();
        }
    }


    public void setUserToken(String token) {
        this.currentUserToken = token;
    }

    @Tool(name = "getMyCars", description = "Get list of my cars. Use this when user wants to book a wash but hasn't specified which car.")
    public String getMyCars() {
        try {
            List<CarResponse> cars = chatbotService.getUserCars(currentUserToken);
            if (cars.isEmpty()) {
                return "You don't have any cars registered. Please add a car first.";
            }
            StringBuilder result = new StringBuilder("Your cars:\n");
            for (CarResponse car : cars) {
                result.append(String.format(
                        "- Car Brand: %s, Model: %s (License Plate: %s)\n",
                        car.getBrand(),
                        car.getModel(),
                        car.getLicenseNumberPlate()
                ));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error fetching your cars: " + e.getMessage();
        }
    }

    @Tool(name = "getMyBookings", description = "Get list of my current bookings/orders")
    public String getMyBookings() {
        try {
            List<OrderResponse> orders = chatbotService.getUserBookings(currentUserToken);
            if (orders.isEmpty()) {
                return "You don't have any current bookings.";
            }
            StringBuilder result = new StringBuilder("Your bookings:\n");
            for (OrderResponse order : orders) {
                result.append(String.format("- Order ID: %d, Customer: %s, Status: %s, Washer: %s\n",
                        order.getId(), order.getCustomerEmail(), order.getStatus(), order.getWasherEmail()));
                if (order.getScheduledTime() != null) {
                    result.append(String.format("  Scheduled: %s\n", order.getScheduledTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))));
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "Error fetching your bookings: " + e.getMessage();
        }
    }

    @Tool(name = "findWashers", description = "Find available washers for booking")
    public String findWashers() {
        try {
            List<String> washers = chatbotService.getActiveWashers(currentUserToken);
            if (washers.isEmpty()) {
                return "No washers are currently available.";
            }
            StringBuilder result = new StringBuilder("Available washers:\n");
            for (String washer : washers) {
                result.append("- ").append(washer).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error fetching available washers: " + e.getMessage();
        }
    }

    @Tool(name = "bookWashNow", description = "Book an immediate car wash. Parameters: carId (Long)")
    public String bookWashNow(Long carId) {
        try {
            OrderResponse order = chatbotService.bookWashNow(carId, currentUserToken);
            return String.format("✅ Wash booked successfully! Order ID: %d, Status: %s",
                    order.getId(), order.getStatus());
        } catch (Exception e) {
            return "❌ Failed to book wash: " + e.getMessage();
        }
    }



    @Tool(name = "cancelMyBooking", description = "Cancel my booking by order ID. Parameter: orderId (Long)")
    public String cancelMyBooking(Long orderId) {
        try {
            chatbotService.cancelBooking(orderId, currentUserToken);
            return "✅ Booking cancelled successfully!";
        } catch (Exception e) {
            return "❌ Failed to cancel booking: " + e.getMessage();
        }
    }

    @Tool(name = "checkOrderStatus", description = "Check status and details of my order by ID. Parameter: orderId (Long)")
    public String checkOrderStatus(Long orderId) {
        try {
            OrderResponse order = chatbotService.getOrderDetails(orderId, currentUserToken);
            return String.format("Order Details:\n- ID: %d\n- Car ID: %d\n- Status: %s\n- Washer: %s\n- Payment: %s\n- Created: %s",
                    order.getId(), order.getCarId(), order.getStatus(), order.getWasherEmail(),
                    order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
        } catch (Exception e) {
            return "❌ Failed to get order details: " + e.getMessage();
        }
    }

    // Washer-specific tools
    @Tool(name = "showMyRequests", description = "Show my pending wash requests (washer role only)")
    public String showMyRequests() {
        try {
            List<WashRequestResponse> requests = chatbotService.getWashRequests(currentUserToken);
            if (requests.isEmpty()) {
                return "No pending wash requests.";
            }
            StringBuilder result = new StringBuilder("Pending wash requests:\n");
            for (WashRequestResponse request : requests) {
                result.append(String.format("- Order ID: %d, Customer: %s, Car ID: %d, Status: %s\n",
                        request.getOrderId(), request.getCustomerEmail(), request.getCarId(), request.getStatus()));
            }
            return result.toString();
        } catch (Exception e) {
            System.out.println("exception in fetching request "  + e.getMessage() + e);
            return "Error fetching wash requests: " + e.getMessage();
        }
    }

    @Tool(name = "acceptWashRequest", description = "Accept a wash request by order ID (washer role only). Parameter: orderId (Long)")
    public String acceptWashRequest(Long orderId) {
        try {
            OrderResponse order = chatbotService.acceptWashRequest(orderId, currentUserToken);
            return String.format("✅ Wash request accepted! Order ID: %d, Status: %s", order.getId(), order.getStatus());
        } catch (Exception e) {
            return "❌ Failed to accept wash request: " + e.getMessage();
        }
    }

    @Tool(name = "declineWashRequest", description = "Decline a wash request by order ID (washer role only). Parameter: orderId (Long)")
    public String declineWashRequest(Long orderId) {
        try {
            OrderResponse order = chatbotService.declineWashRequest(orderId, currentUserToken);
            return String.format("✅ Wash request declined! Order ID: %d, Status: %s", order.getId(), order.getStatus());
        } catch (Exception e) {
            return "❌ Failed to decline wash request: " + e.getMessage();
        }
    }

    @Tool(name = "markOrderCompleted", description = "Mark an order as completed (washer role only). Parameter: orderId (Long)")
    public String markOrderCompleted(Long orderId) {
        try {
            OrderResponse order = chatbotService.markOrderCompleted(orderId, currentUserToken);
            return String.format("✅ Order marked as completed! Order ID: %d, Status: %s", order.getId(), order.getStatus());
        } catch (Exception e) {
            return "❌ Failed to mark order as completed: " + e.getMessage();
        }
    }
}

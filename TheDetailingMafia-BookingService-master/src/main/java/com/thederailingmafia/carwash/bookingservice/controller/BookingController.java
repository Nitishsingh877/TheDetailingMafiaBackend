package com.thederailingmafia.carwash.bookingservice.controller;

import com.thederailingmafia.carwash.bookingservice.dto.OrderRequest;
import com.thederailingmafia.carwash.bookingservice.dto.OrderResponse;

import com.thederailingmafia.carwash.bookingservice.service.OrderService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api/order")
public class BookingController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/health")
    public String health() {
        return "OK";
    }

//    @GetMapping("/list/washer")
//    public WasherResponse getWasherList() {
//        return  userServiceClient.getWashers();
//    }

    @PostMapping("/wash-now")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public OrderResponse bookWashNow(@RequestBody OrderRequest orderRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return orderService.bookWashNow(orderRequest, email);
    }

    @PostMapping("/schdule/wash")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public OrderResponse schduleBookWash(Authentication authentication, @RequestBody OrderRequest orderRequest) {
        String email = authentication.getName();
        return orderService.scheduleWashNow(orderRequest, email);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<OrderResponse> getPendingOrder() {
        return orderService.getPendingOrders();
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'WASHER', 'ADMIN')")
    public List<OrderResponse> getCurrentOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        return orderService.getCurrentOrders(email, role);
    }

    @GetMapping("/past")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'WASHER', 'ADMIN')")
    public List<OrderResponse> getPastOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        return orderService.getPastOrders(email, role);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid order ID: " + id);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String userRole = auth.getAuthorities().stream().findFirst().get().getAuthority();

        return orderService.getOrder(id, userEmail, userRole);
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(@PathVariable Long id, @RequestBody OrderResponse request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
        return orderService.updateOrder(id, request, userEmail, role);
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid order ID for cancellation: " + id);
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String userRole = auth.getAuthorities().stream().findFirst().get().getAuthority();

        return orderService.cancelOrder(id, userEmail, userRole);
    }

    @GetMapping("/validate/{id}")
    public Boolean validateOrderExists(@PathVariable Long id) {
        return orderService.orderExists(id);
    }

//    stats dashboards
    @GetMapping("/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Long getTotalBookingsCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerEmail = auth.getName();
        return orderService.getCustomerBookingsCount(customerEmail);
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Long getPendingOrdersCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerEmail = auth.getName();
        return orderService.getCustomerPendingCount(customerEmail);
    }



//    // AI integration with tool calling
//    @GetMapping("/answer")
//    public String getAnswer(@RequestParam String query) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = auth.getName();
//
//        // Add user message to memory
//        UserMessage userMessage = new UserMessage(query);
//        chatMemory.add(userEmail, userMessage);
//
////        edit part for tool calling
//        ChatClient client = ChatClient.create(chatModel);
//        toolBeans.forEach(t -> System.out.println("Tool: " + t.getClass().getName()));
//
//        String result = client
//                .prompt()
//                .messages(chatMemory.get(userEmail))
//                .tools(toolBeans.toArray())
//                .call()
//                .content();
//
//        // Call the model with chat history
//    //    ChatResponse response = chatModel.call(new Prompt(chatMemory.get(userEmail)));
//
//        // Add model output to memory
//      //  chatMemory.add(userEmail, response.getResult().getOutput());
//        chatMemory.add(userEmail, new AssistantMessage(result));
//
////        if no tools called
//        return result;
//    }


//    @GetMapping("/answer")
//    public String getAnswer(@RequestParam String query) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = auth.getName();
//        System.out.println("user email is " + userEmail);
//        System.out.println("query is " + query);
//
//        // 1) Save the user’s message in memory
//        chatMemory.add(userEmail, new UserMessage(query));
//
//        // 2) Create a ChatClient on top of your ChatModel
//        ChatClient client = ChatClient.create(chatModel);
//
//        // 3) Send the full history + all @Tool beans; Spring AI auto-executes any tool calls
//        String result = client
//                .prompt()
//                .messages(chatMemory.get(userEmail))
//                .tools(toolBeans.toArray())
//                .call()
//                .content();
//
//        // 4) Save the assistant’s reply (or tool output) in memory
//        chatMemory.add(userEmail, new AssistantMessage(result));
//
//        return result;
//    }

//
//    @GetMapping("/answer")
//    public String getAnswer(@RequestParam String query) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = auth.getName();
//
//        // Add user message to memory
//        UserMessage userMessage = new UserMessage(query);
//        chatMemory.add(userEmail, userMessage);
//
//        // 2) Discover only the beans that have at least one @Tool method
//        Object[] toolBeans = Stream.concat(
//                        allBeans.stream(),
//                        // also consider this controller’s own beans if it had any @Tool
//                        Stream.of(this)
//                )
//                .filter(bean ->
//                        Arrays.stream(bean.getClass().getMethods())
//                                .anyMatch(m -> m.isAnnotationPresent(Tool.class))
//                )
//                .toArray();
//
////        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
////                .searchRequest(SearchRequest.builder()
////                        .similarityThreshold(0.8d)
////                        .topK(5)
////                        .build())
////                .build();
//
//
//        // 3) Build and call the ChatClient, registering only your real tools
//        String result = ChatClient.create(chatModel)
//                .prompt()
//                .messages(chatMemory.get(userEmail))
//                .tools(toolBeans)
////                .advisors(qaAdvisor)
//                .call()
//                .content();
//
//        // Save assistant output to memory
//        chatMemory.add(userEmail, new AssistantMessage(result));
//
//        return result;
//    }
//
//}
}

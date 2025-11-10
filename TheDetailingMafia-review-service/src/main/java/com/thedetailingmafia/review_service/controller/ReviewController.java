package com.thedetailingmafia.review_service.controller;




import com.thedetailingmafia.review_service.model.Review;
import com.thedetailingmafia.review_service.repository.ReviewRepository;
import com.thedetailingmafia.review_service.service.ReviewService;
import com.thedetailingmafia.review_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;
    private final JwtUtil jwtUtil;
    @Autowired
    private ReviewRepository reviewRepository;

    public ReviewController(ReviewService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Review review, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.getRoleFromToken(token);

        if (!"CUSTOMER".equals(role)) {
            return ResponseEntity.status(403).body("Only customers can add reviews.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        review.setCustomerEmail(email);

        try {
            return ResponseEntity.ok(service.addReview(review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Review>> getReviewsByOrderId(@PathVariable Long orderId) {
        List<Review> reviews = reviewRepository.findByOrderId(orderId);
        return ResponseEntity.ok(reviews);
    }


    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateReview(@PathVariable Long orderId, @RequestBody Review review, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.getRoleFromToken(token);

        if (!"CUSTOMER".equals(role)) {
            return ResponseEntity.status(403).body("Only customers can update reviews.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        review.setCustomerEmail(email);

        try {
            Review updatedReview = service.updateReview(orderId, review);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.getRoleFromToken(token);

        if (!"CUSTOMER".equals(role)) {
            return ResponseEntity.status(403).body("Only customers can delete reviews.");
        }

        String email = jwtUtil.getEmailFromToken(token);

        try {
            service.deleteReview(orderId, email);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}


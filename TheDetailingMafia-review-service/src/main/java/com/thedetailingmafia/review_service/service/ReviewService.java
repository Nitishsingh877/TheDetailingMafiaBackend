package com.thedetailingmafia.review_service.service;



import com.thedetailingmafia.review_service.dto.OrderResponse;
import com.thedetailingmafia.review_service.feign.OrderClient;
import com.thedetailingmafia.review_service.model.Review;
import com.thedetailingmafia.review_service.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repo;
    private final OrderClient orderClient;

    public ReviewService(ReviewRepository repo, OrderClient orderClient) {
        this.repo = repo;
        this.orderClient = orderClient;
    }

    public Review addReview(Review review) {

        OrderResponse order = orderClient.getOrderById(review.getOrderId());

        if (order == null || !"COMPLETED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("You can only review completed orders.");
        }

        if (!order.getCustomerEmail().equals(review.getCustomerEmail())) {
            throw new SecurityException("You can only review your own orders.");
        }

        return repo.save(review);
    }

    public List<Review> getAllReviews() {
        return repo.findAll();
    }

    public List<Review> getReviewsByCustomer(String email) {
        return repo.findByCustomerEmail(email);
    }

    public Review updateReview(Long orderId, Review review) {
        Review existingReview = repo.findByOrderId(orderId).stream()
                .filter(r -> r.getCustomerEmail().equals(review.getCustomerEmail()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Review not found."));

        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());

        return repo.save(existingReview);
    }

    public void deleteReview(Long orderId, String email) {
        Review review = repo.findByOrderId(orderId).stream()
                .filter(r -> r.getCustomerEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Review not found."));

        repo.delete(review);
    }
}

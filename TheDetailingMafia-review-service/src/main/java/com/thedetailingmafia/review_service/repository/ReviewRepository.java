package com.thedetailingmafia.review_service.repository;




import com.thedetailingmafia.review_service.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, Long> {
    List<Review> findByCustomerEmail(String email);
    List<Review> findByOrderId(Long orderId);

}

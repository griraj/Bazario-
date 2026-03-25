package com.bazario.service;

import com.bazario.dto.request.AddReviewRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse addReview(Long userId, AddReviewRequest request);

    PagedResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable);
}

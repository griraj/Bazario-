package com.bazario.service.impl;

import com.bazario.dto.request.AddReviewRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ReviewResponse;
import com.bazario.entity.Product;
import com.bazario.entity.Review;
import com.bazario.entity.User;
import com.bazario.exception.ConflictException;
import com.bazario.exception.ForbiddenException;
import com.bazario.exception.ResourceNotFoundException;
import com.bazario.repository.OrderItemRepository;
import com.bazario.repository.ProductRepository;
import com.bazario.repository.ReviewRepository;
import com.bazario.repository.UserRepository;
import com.bazario.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(Long userId, AddReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!orderItemRepository.hasUserPurchasedProduct(userId, product.getId())) {
            throw new ForbiddenException("You can only review products you have purchased and received.");
        }

        if (reviewRepository.existsByProductIdAndUserId(product.getId(), userId)) {
            throw new ConflictException("You have already reviewed this product.");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        Page<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);

        return PagedResponse.<ReviewResponse>builder()
                .content(reviews.getContent().stream().map(this::mapToReviewResponse).toList())
                .pageNumber(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .last(reviews.isLast())
                .build();
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

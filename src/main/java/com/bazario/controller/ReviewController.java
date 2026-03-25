package com.bazario.controller;

import com.bazario.dto.request.AddReviewRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.ReviewResponse;
import com.bazario.entity.User;
import com.bazario.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Reviews", description = "Endpoints for product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Add a review for a product")
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(user.getId(), request));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<PagedResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }
}

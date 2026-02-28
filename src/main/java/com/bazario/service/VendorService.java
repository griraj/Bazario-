package com.bazario.service;

import com.bazario.dto.request.StorefrontRegisterRequest;
import com.bazario.dto.response.PagedResponse;
import com.bazario.dto.response.StorefrontResponse;
import com.bazario.dto.response.UserResponse;

public interface VendorService {

    StorefrontResponse registerStorefront(Long vendorId, StorefrontRegisterRequest request);

    PagedResponse<UserResponse> getAllVendors(int page, int size);
}

package com.ecommerce.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    EMAIL_ALREADY_EXISTS(400, "Email is already registered", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(401, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_VERIFIED(403, "Account has not been email-verified", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(403, "Account is locked", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(401, "Invalid or expired token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(401, "Token has expired", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(401, "You must be signed in to perform this action", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCH(400, "Password confirmation does not match", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD(400, "New password must be different from the old password", HttpStatus.BAD_REQUEST),
    USER_LOGIN(403, "You are not allowed to sign in through the admin portal", HttpStatus.FORBIDDEN),
    ADMIN_LOGIN(403, "Please sign in through the admin portal", HttpStatus.FORBIDDEN),

    // Product & Category
    PRODUCT_NOT_FOUND(404, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_IMAGE_NOT_FOUND(404, "Product image not found", HttpStatus.NOT_FOUND),
    PRODUCT_SLUG_EXISTS(400, "Product slug already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(404, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(400, "Category name already exists", HttpStatus.BAD_REQUEST),

    // Cart
    CART_ITEM_NOT_FOUND(404, "Product not found in cart", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(400, "Insufficient stock", HttpStatus.BAD_REQUEST),

    // Wishlist
    WISHLIST_ITEM_NOT_FOUND(404, "Product not found in wishlist", HttpStatus.NOT_FOUND),

    // Order
    ORDER_NOT_FOUND(404, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL(400, "Order cannot be canceled in its current status", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS_TRANSITION(400, "Invalid order status transition", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_PAID(400, "Order has already been paid", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD(400, "Invalid payment method for this operation", HttpStatus.BAD_REQUEST),
    CART_EMPTY(400, "Cart is empty", HttpStatus.BAD_REQUEST),

    // Coupon
    COUPON_NOT_FOUND(404, "Coupon does not exist or has been deactivated", HttpStatus.NOT_FOUND),
    COUPON_ALREADY_EXISTS(400, "Coupon code already exists", HttpStatus.BAD_REQUEST),
    COUPON_EXPIRED(400, "Coupon has expired", HttpStatus.BAD_REQUEST),
    COUPON_USAGE_EXCEEDED(400, "Coupon usage limit has been reached", HttpStatus.BAD_REQUEST),
    COUPON_MIN_ORDER_NOT_MET(400, "Order does not meet the minimum amount required for this coupon", HttpStatus.BAD_REQUEST),

    // User
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),

    // Address
    ADDRESS_NOT_FOUND(404, "Address not found", HttpStatus.NOT_FOUND),

    // Generic
    FORBIDDEN(403, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR(500, "System error. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

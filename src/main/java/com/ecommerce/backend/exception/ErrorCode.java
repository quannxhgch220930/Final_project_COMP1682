package com.ecommerce.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    EMAIL_ALREADY_EXISTS    (400, "Email đã được đăng ký",              HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS     (401, "Email hoặc mật khẩu không đúng",     HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_VERIFIED    (403, "Tài khoản chưa được xác thực email", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED          (403, "Tài khoản đã bị khóa",              HttpStatus.FORBIDDEN),
    INVALID_TOKEN           (401, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED           (401, "Token đã hết hạn",                    HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED            (401, "Bạn cần đăng nhập để thực hiện",     HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCH  (400, "Mật khẩu xác nhận không khớp",           HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD(400, "Mật khẩu mới không được trùng mật khẩu cũ", HttpStatus.BAD_REQUEST),
    USER_LOGIN(403, "Bạn không có quyền đăng nhập tại trang quản trị", HttpStatus.FORBIDDEN),
    ADMIN_LOGIN(403, "Hãy đăng nhập tại trang quản trị", HttpStatus.FORBIDDEN),

    // Product & Category
    PRODUCT_NOT_FOUND       (404, "Không tìm thấy sản phẩm",            HttpStatus.NOT_FOUND),
    PRODUCT_SLUG_EXISTS     (400, "Slug sản phẩm đã tồn tại",           HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND      (404, "Không tìm thấy danh mục",            HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS (400, "Tên danh mục đã tồn tại",            HttpStatus.BAD_REQUEST),

    // Cart
    CART_ITEM_NOT_FOUND  (404, "Không tìm thấy sản phẩm trong giỏ hàng", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK   (400, "Sản phẩm không đủ tồn kho",              HttpStatus.BAD_REQUEST),

    // Wishlist
    WISHLIST_ITEM_NOT_FOUND (404, "Sản phẩm không có trong wishlist",    HttpStatus.NOT_FOUND),

    // Order
    ORDER_NOT_FOUND       (404, "Không tìm thấy đơn hàng",              HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL   (400, "Không thể hủy đơn hàng ở trạng thái này", HttpStatus.BAD_REQUEST),
    CART_EMPTY            (400, "Giỏ hàng đang trống",                  HttpStatus.BAD_REQUEST),

    // Coupon
    COUPON_NOT_FOUND      (404, "Mã coupon không tồn tại hoặc đã bị vô hiệu", HttpStatus.NOT_FOUND),
    COUPON_ALREADY_EXISTS (400, "Mã coupon đã tồn tại",                 HttpStatus.BAD_REQUEST),
    COUPON_EXPIRED        (400, "Mã coupon đã hết hạn",                 HttpStatus.BAD_REQUEST),
    COUPON_USAGE_EXCEEDED (400, "Mã coupon đã hết lượt sử dụng",        HttpStatus.BAD_REQUEST),
    COUPON_MIN_ORDER_NOT_MET(400,"Đơn hàng chưa đạt giá trị tối thiểu để dùng coupon", HttpStatus.BAD_REQUEST),

    // User
    USER_NOT_FOUND          (404, "Không tìm thấy người dùng",          HttpStatus.NOT_FOUND),

    // Address
    ADDRESS_NOT_FOUND (404, "Không tìm thấy địa chỉ", HttpStatus.NOT_FOUND),

    // Generic
    FORBIDDEN               (403, "Bạn không có quyền thực hiện",       HttpStatus.FORBIDDEN),
    INTERNAL_ERROR          (500, "Lỗi hệ thống, vui lòng thử lại",     HttpStatus.INTERNAL_SERVER_ERROR);


    private final int        code;
    private final String     message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code       = code;
        this.message    = message;
        this.httpStatus = httpStatus;
    }
}
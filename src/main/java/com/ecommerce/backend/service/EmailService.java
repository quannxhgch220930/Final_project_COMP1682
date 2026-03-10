package com.ecommerce.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService {

    private final Optional<JavaMailSender> mailSender;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerifyEmail(String to, String fullName, String token) {
        if (mailSender.isEmpty()) {
            System.out.println("==================================");
            System.out.println("[DEV] Gửi email xác thực:");
            System.out.println("  To    : " + to);
            System.out.println("  Token : " + token);
            System.out.println("  Link  : http://localhost:" + serverPort
                    + "/api/v1/auth/verify?token=" + token);
            System.out.println("==================================");
            return;
        }

        String verifyUrl = "http://localhost:" + serverPort
                + "/api/v1/auth/verify?token=" + token;

        String htmlContent = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            padding:32px;border:1px solid #e0e0e0;border-radius:8px">
                  <h2 style="color:#2d7a2d">Xin chào, %s! 👋</h2>
                  <p style="color:#444;font-size:15px">
                    Cảm ơn bạn đã đăng ký tài khoản.<br>
                    Nhấn nút bên dưới để xác thực email của bạn:
                  </p>
                  <div style="text-align:center;margin:32px 0">
                    <a href="%s"
                       style="display:inline-block;padding:14px 32px;
                              background:#6db33f;color:#fff;font-size:16px;
                              text-decoration:none;border-radius:6px;
                              font-weight:bold">
                      ✅ Xác thực tài khoản
                    </a>
                  </div>
                  <p style="color:#999;font-size:13px">
                    Link có hiệu lực trong <strong>24 giờ</strong>.<br>
                    Nếu bạn không đăng ký tài khoản này, hãy bỏ qua email này.
                  </p>
                  <hr style="border:none;border-top:1px solid #eee;margin:24px 0">
                  <p style="color:#bbb;font-size:12px;text-align:center">
                    © 2026 E-Commerce. All rights reserved.
                  </p>
                </div>
                """.formatted(fullName, verifyUrl);

        System.out.println("[MAIL] Đang gửi email đến: " + to);

        try {
            MimeMessage msg = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[E-Commerce] Xác thực tài khoản của bạn");
            helper.setText(htmlContent, true);
            mailSender.get().send(msg);
            System.out.println("[MAIL] Gửi thành công đến: " + to);
        } catch (MessagingException e) {
            System.out.println("[MAIL] Lỗi gửi mail: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String to, String fullName, String token) {
        if (mailSender.isEmpty()) {
            System.out.println("==================================");
            System.out.println("[DEV] Gửi email reset password:");
            System.out.println("  To    : " + to);
            System.out.println("  Token : " + token);
            System.out.println("  Link  : http://localhost:" + serverPort
                    + "/api/v1/auth/reset-password?token=" + token);
            System.out.println("==================================");
            return;
        }

        String resetUrl = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

        String htmlContent = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                        padding:32px;border:1px solid #e0e0e0;border-radius:8px">
              <h2 style="color:#d9534f">Xin chào, %s! 🔐</h2>
              <p style="color:#444;font-size:15px">
                Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.<br>
                Nhấn nút bên dưới để đặt lại mật khẩu:
              </p>
              <div style="text-align:center;margin:32px 0">
                <a href="%s"
                   style="display:inline-block;padding:14px 32px;
                          background:#d9534f;color:#fff;font-size:16px;
                          text-decoration:none;border-radius:6px;
                          font-weight:bold">
                  🔑 Đặt lại mật khẩu
                </a>
              </div>
              <p style="color:#999;font-size:13px">
                Link có hiệu lực trong <strong>15 phút</strong>.<br>
                Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.
              </p>
              <hr style="border:none;border-top:1px solid #eee;margin:24px 0">
              <p style="color:#bbb;font-size:12px;text-align:center">
                © 2026 E-Commerce. All rights reserved.
              </p>
            </div>
            """.formatted(fullName, resetUrl);

        System.out.println("[MAIL] Đang gửi email reset password đến: " + to);

        try {
            MimeMessage msg = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[E-Commerce] Đặt lại mật khẩu");
            helper.setText(htmlContent, true);
            mailSender.get().send(msg);
            System.out.println("[MAIL] Gửi thành công đến: " + to);
        } catch (MessagingException e) {
            System.out.println("[MAIL] Lỗi gửi mail: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
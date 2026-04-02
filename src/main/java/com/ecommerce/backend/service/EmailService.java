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

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.frontend.verify-path:/verify-email}")
    private String verifyPath;

    @Value("${app.frontend.reset-password-path:/reset-password}")
    private String resetPasswordPath;

    public EmailService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerifyEmail(String to, String fullName, String token) {
        String verifyUrl = buildFrontendUrl(verifyPath, token);

        if (mailSender.isEmpty()) {
            System.out.println("==================================");
            System.out.println("[DEV] Gui email xac thuc:");
            System.out.println("  To    : " + to);
            System.out.println("  Token : " + token);
            System.out.println("  Link  : " + verifyUrl);
            System.out.println("==================================");
            return;
        }

        String htmlContent = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                            padding:32px;border:1px solid #e0e0e0;border-radius:8px">
                  <h2 style="color:#2d7a2d">Xin chao, %s!</h2>
                  <p style="color:#444;font-size:15px">
                    Cam on ban da dang ky tai khoan.<br>
                    Nhan nut ben duoi de xac thuc email cua ban:
                  </p>
                  <div style="text-align:center;margin:32px 0">
                    <a href="%s"
                       style="display:inline-block;padding:14px 32px;
                              background:#6db33f;color:#fff;font-size:16px;
                              text-decoration:none;border-radius:6px;
                              font-weight:bold">
                      Xac thuc tai khoan
                    </a>
                  </div>
                  <p style="color:#999;font-size:13px">
                    Link co hieu luc trong <strong>24 gio</strong>.<br>
                    Neu ban khong dang ky tai khoan nay, hay bo qua email nay.
                  </p>
                  <hr style="border:none;border-top:1px solid #eee;margin:24px 0">
                  <p style="color:#bbb;font-size:12px;text-align:center">
                    © 2026 E-Commerce. All rights reserved.
                  </p>
                </div>
                """.formatted(fullName, verifyUrl);

        System.out.println("[MAIL] Dang gui email den: " + to);

        try {
            MimeMessage msg = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[E-Commerce] Xac thuc tai khoan cua ban");
            helper.setText(htmlContent, true);
            mailSender.get().send(msg);
            System.out.println("[MAIL] Gui thanh cong den: " + to);
        } catch (MessagingException e) {
            System.out.println("[MAIL] Loi gui mail: " + e.getMessage());
            throw new RuntimeException("Khong the gui email: " + e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordEmail(String to, String fullName, String token) {
        String resetUrl = buildFrontendUrl(resetPasswordPath, token);

        if (mailSender.isEmpty()) {
            System.out.println("==================================");
            System.out.println("[DEV] Gui email reset password:");
            System.out.println("  To    : " + to);
            System.out.println("  Token : " + token);
            System.out.println("  Link  : " + resetUrl);
            System.out.println("==================================");
            return;
        }

        String htmlContent = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                        padding:32px;border:1px solid #e0e0e0;border-radius:8px">
              <h2 style="color:#d9534f">Xin chao, %s!</h2>
              <p style="color:#444;font-size:15px">
                Chung toi nhan duoc yeu cau dat lai mat khau cho tai khoan cua ban.<br>
                Nhan nut ben duoi de dat lai mat khau:
              </p>
              <div style="text-align:center;margin:32px 0">
                <a href="%s"
                   style="display:inline-block;padding:14px 32px;
                          background:#d9534f;color:#fff;font-size:16px;
                          text-decoration:none;border-radius:6px;
                          font-weight:bold">
                  Dat lai mat khau
                </a>
              </div>
              <p style="color:#999;font-size:13px">
                Link co hieu luc trong <strong>15 phut</strong>.<br>
                Neu ban khong yeu cau dat lai mat khau, hay bo qua email nay.
              </p>
              <hr style="border:none;border-top:1px solid #eee;margin:24px 0">
              <p style="color:#bbb;font-size:12px;text-align:center">
                © 2026 E-Commerce. All rights reserved.
              </p>
            </div>
            """.formatted(fullName, resetUrl);

        System.out.println("[MAIL] Dang gui email reset password den: " + to);

        try {
            MimeMessage msg = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[E-Commerce] Dat lai mat khau");
            helper.setText(htmlContent, true);
            mailSender.get().send(msg);
            System.out.println("[MAIL] Gui thanh cong den: " + to);
        } catch (MessagingException e) {
            System.out.println("[MAIL] Loi gui mail: " + e.getMessage());
            throw new RuntimeException("Khong the gui email: " + e.getMessage());
        }
    }

    private String buildFrontendUrl(String path, String token) {
        return frontendBaseUrl + path + "?token=" + token;
    }
}
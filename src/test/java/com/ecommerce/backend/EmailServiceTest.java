package com.ecommerce.backend;

import com.ecommerce.backend.service.EmailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    @Test
    public void sendVerifyEmail_shouldCallJavaMailSenderAndPopulateMessage() throws Exception {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailService service = new EmailService(Optional.of(mailSender));
        ReflectionTestUtils.setField(service, "fromEmail", "from@example.com");
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(service, "verifyPath", "/verify-email");

        service.sendVerifyEmail("to@example.com", "Full Name", "token123");

        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.eq(mimeMessage));
        assertNotNull(mimeMessage.getAllRecipients());
        assertEquals("to@example.com", mimeMessage.getAllRecipients()[0].toString());
        assertEquals("[E-Commerce] Xac thuc tai khoan cua ban", mimeMessage.getSubject());
    }

    @Test
    public void sendResetPasswordEmail_shouldCallJavaMailSenderAndPopulateMessage() throws Exception {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailService service = new EmailService(Optional.of(mailSender));
        ReflectionTestUtils.setField(service, "fromEmail", "from@example.com");
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(service, "resetPasswordPath", "/reset-password");

        service.sendResetPasswordEmail("to2@example.com", "Full Name", "tokenABC");

        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.eq(mimeMessage));
        assertNotNull(mimeMessage.getAllRecipients());
        assertEquals("to2@example.com", mimeMessage.getAllRecipients()[0].toString());
        assertEquals("[E-Commerce] Dat lai mat khau", mimeMessage.getSubject());
    }
}

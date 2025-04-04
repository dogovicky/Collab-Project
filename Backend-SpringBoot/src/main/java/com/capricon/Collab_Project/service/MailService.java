package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class MailService {

    private final RestTemplate restTemplate;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${app.email.from.address}")
    private String fromEmail;

    @Value("${app.email.from.name}")
    private String fromName;

    @Value("${brevo.api.url}")
    private String brevoApiUrl;

    @Value("${app.verification.expiry-minutes:15}")
    private int expiryInMinutes;

    public MailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public void sendVerificationCode(String recipientEmail, String fullName, String verificationCode) {

        String subject = "Verify your account";
        String htmlContent = createVerificationEmailHtml(fullName, verificationCode, expiryInMinutes);
        String textContent = createVerificationEmailText(fullName, verificationCode, expiryInMinutes);

        try {
            sendEmailViaBravo(recipientEmail, fullName, subject, htmlContent, textContent);
            log.info("Verification email sent to: {}",recipientEmail);
        } catch (Exception ex) {
            log.error("Failed to send verification email to {}: {}", recipientEmail, ex.getMessage());
            throw new BusinessException("Failed to send verification email", HttpStatus.BAD_REQUEST);
        }

    }

    public void sendResetPasswordLink(String email, String resetLink, String expiryTime) {
        String subject = "Reset your password";
        String htmlContent = createResetPasswordHtmlContent(email, resetLink, expiryTime);
        String textContent = createResetPasswordTextContent(email, resetLink, expiryTime);

        try {
            sendEmailViaBravo(email, null, subject, htmlContent, textContent);
            log.info("Reset password link sent via email to {}", email);
        } catch (Exception ex) {
            throw new BusinessException("Failed to send password reset email", HttpStatus.BAD_REQUEST);
        }

    }

    private String createVerificationEmailHtml(String fullName, String code, int expiryMinutes) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;\">" +
                "<h2>Account Verification</h2>" +
                "<p>Hi " + (fullName != null ? fullName : "there") + ",</p>" +
                "<p>Thank you for creating an account. To complete your registration, please use the following verification code:</p>" +
                "<div style=\"background-color: #f4f4f4; padding: 12px; text-align: center;" +
                " font-size: 24px; letter-spacing: 5px; font-weight: bold;\">" +
                code +
                "</div>" +
                "<p>This code will expire in " + expiryMinutes + " minutes.</p>" +
                "<p>If you didn't create an account, you can safely ignore this email.</p>" +
                "<p>Best regards,<br>Nexus</p>" +
                "</div>";
    }

    private String createVerificationEmailText(String fullName, String code, int expiryMinutes) {
        return "Account Verification\n\n" +
                "Hi " + (fullName != null ? fullName : "there") + ",\n\n" +
                "Thank you for creating an account. To complete your registration, please use the following verification code:\n\n" +
                code + "\n\n" +
                "This code will expire in " + expiryMinutes + " minutes.\n\n" +
                "If you didn't create an account, you can safely ignore this email.\n\n" +
                "Best regards,\nNexus";
    }

    private static String createResetPasswordHtmlContent(String email, String resetLink, String expirationTime) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <meta charset=\"UTF-8\">"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "    <title>Reset Your Password</title>"
                + "    <style>"
                + "        body {"
                + "            font-family: Arial, sans-serif;"
                + "            line-height: 1.6;"
                + "            color: #333333;"
                + "            max-width: 600px;"
                + "            margin: 0 auto;"
                + "            padding: 20px;"
                + "        }"
                + "        .content {"
                + "            padding: 20px 0;"
                + "        }"
                + "        .button {"
                + "            display: inline-block;"
                + "            background-color: #4285f4;"
                + "            color: white !important;"
                + "            text-decoration: none;"
                + "            padding: 12px 24px;"
                + "            border-radius: 4px;"
                + "            font-weight: bold;"
                + "            margin: 20px 0;"
                + "        }"
                + "        .expiration {"
                + "            font-size: 14px;"
                + "            color: #666666;"
                + "            margin-top: 20px;"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class=\"content\">"
                + "        <h2>Password Reset Request</h2>"
                + "        <p>Hello " + email + ",</p>"
                + "        <p>We received a request to reset your password. If you didn't make this request, you can safely ignore this email.</p>"
                + "        <p>To reset your password, click the button below:</p>"
                + "        <a href=\"" + resetLink + "\" class=\"button\">Reset Password</a>"
                + "        <p>Or copy and paste this link into your browser:</p>"
                + "        <p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>"
                + "        <p class=\"expiration\">This link will expire on " + expirationTime + ".</p>"
                + "    </div>"
                + "</body>"
                + "</html>";
    }

    private static String createResetPasswordTextContent(String email, String resetLink, String expirationTime) {
        return "PASSWORD RESET REQUEST\n\n"
                + "Hello " + email + ",\n\n"
                + "We received a request to reset your password. If you didn't make this request, you can safely ignore this email.\n\n"
                + "To reset your password, please visit this link:\n"
                + resetLink + "\n\n"
                + "This link will expire on " + expirationTime + ".\n\n"
                + "---\n"
                + "This email was sent to " + email + " because a password reset was requested for your account.\n"
                + "© " + LocalDateTime.now().getYear() + " YourCompany. All rights reserved.\n"
                + "YourCompany, Inc. • 123 Main Street • Anytown, USA 12345";
    }

    public void sendEmailViaBravo(String toEmail, String toName, String subject, String htmlContent, String textContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> toMap = new HashMap<>();
        toMap.put("email", toEmail);
        if (toName != null && !toMap.isEmpty()) {
            toMap.put("fullName", toName);
        }

        Map<String, Object> sender = new HashMap<>();
        sender.put("email", fromEmail);
        sender.put("name", fromName);

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("sender", sender);
        emailData.put("to", Collections.singleton(toMap));
        emailData.put("subject", subject);
        emailData.put("htmlContent", htmlContent);
        emailData.put("textContent", textContent);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);

        try {
            restTemplate.exchange(
                    brevoApiUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Error sending email: {} - {}", ex.getStatusCode(), ex.getMessage());
            throw new BusinessException("Failed to send email", HttpStatus.BAD_REQUEST);
        }

    }

}

package com.capricon.Collab_Project.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class MailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.api.email}")
    private String senderEmail;



    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%6d", random.nextInt(1000000));
    }

    public void sendVerificationCode(String recipientEmail, String fullName) {

        Email from = new Email(senderEmail);
        String subject = "Verify your account";
        Email to = new Email(recipientEmail);

        String verificationCode = generateVerificationCode();

        String htmlContent = generateVerificationEmailContent(fullName, verificationCode);

        Content content = new Content("content/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private String generateVerificationEmailContent(String fullName, String verificationCode) {
        return "<div>" +
                "<h2>Welcome, " + fullName + "!</h2>"
                + "<p> Thank you for signing up. Please use the verification code below to activate your account: </p>"
                + "<h1>" + verificationCode + "</h2>"
                + "<p>This code is valid for 10 minutes. </p>"
                + "<p>If you didn't request this please ignore this email.</p>"
                + "<p>Best regards</p>"
                + "<p><strong>Nexus</strong></p>"
                + "</div>";
    }

}

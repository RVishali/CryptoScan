package com.example.demo.service;

import java.io.File;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Simple text email
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // NEW: Email with PDF attachment
    public void sendReportWithAttachment(String to, String subject, String text, File pdf) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true); // multipart = true

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            helper.addAttachment(pdf.getName(), pdf);

            mailSender.send(msg);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send report email: " + e.getMessage(), e);
        }
    }
}

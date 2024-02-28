package com.example.androidproject;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTask extends AsyncTask<Void, Void, Void> {
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_USERNAME = "toan07122019@gmail.com";
    private static final String EMAIL_PASSWORD = "hdkcnlacxkhahexa";

    private String recipientEmail;
    private String subject;
    private String body;

    public SendEmailTask(String recipientEmail, String subject, String body) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EMAIL_HOST);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
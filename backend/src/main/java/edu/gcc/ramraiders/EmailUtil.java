package edu.gcc.ramraiders;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendScheduleEmail(
            String toEmail,
            String username,
            String semester,
            String year,
            byte[] pdfBytes
    ) throws Exception {

        final String fromEmail = "yourappemail@gmail.com";
        final String password = "your_app_password_here"; // Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(toEmail));
        message.setSubject("Schedule from " + username);

        BodyPart textPart = new MimeBodyPart();
        textPart.setText("Attached is the schedule for " + semester + " " + year);

        MimeBodyPart pdfPart = new MimeBodyPart();
        pdfPart.setFileName("schedule.pdf");
        pdfPart.setContent(pdfBytes, "application/pdf");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(pdfPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
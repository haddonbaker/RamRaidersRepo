package edu.gcc.ramraiders;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendScheduleEmail(
            String to,
            String username,
            String semester,
            String year,
            byte[] pdfBytes
    ) throws Exception {

        String from = System.getenv("EMAIL");
        String password = System.getenv("EMAIL_PASS");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Schedule - " + username + " (" + semester + " " + year + ")");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Attached is the student's schedule.");

        MimeBodyPart attachment = new MimeBodyPart();
        attachment.setFileName("schedule.pdf");
        attachment.setContent(pdfBytes, "application/pdf");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachment);

        message.setContent(multipart);

        Transport.send(message);
    }
}
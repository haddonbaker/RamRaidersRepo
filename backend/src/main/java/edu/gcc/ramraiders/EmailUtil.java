package edu.gcc.ramraiders;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class EmailUtil {

    public static void sendScheduleEmail(
            String toEmail,
            String username,
            String semester,
            String year,
            byte[] pdfBytes
    ) throws Exception {

        final String fromEmail = "blanksj673@gmail.com";
        final String password = "nsotlxevjzrbttil";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Schedule from " + username);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Attached is the schedule for " + semester + " " + year);

        MimeBodyPart pdfPart = new MimeBodyPart();

        DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
        pdfPart.setDataHandler(new DataHandler(source));
        pdfPart.setFileName("schedule.pdf");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(pdfPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("Email successfully sent to " + toEmail);
    }
}
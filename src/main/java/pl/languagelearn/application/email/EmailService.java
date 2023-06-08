package pl.languagelearn.application.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {
    private static final String SUBJECT = "Potwierdzenie rejestracji użytkownika.";
    private static final String TEXT = "Aby aktywować konto kliknij w link: ";
    @Value("${custom.link}")
    private  String link;
    private static final String CHARS = "qwertyuiopasdfghjklzxcvbnm0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    public static final int TOKEN_SIZE = 15;

    public void sendEmail(String subject, String recipient, String msg) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.debug", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.host", "smtp-mail.outlook.com");
        prop.put("mail.smtp.ssl.Enable", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.port", 587);
        String username = "mariusztest@outlook.es";
        String password = "Examplepassword1!";
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        session.setDebug(true);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        }

    public void sendConfirmationEmail(String recipient, String userToken) throws MessagingException {
        String msg = TEXT + link + userToken;
        sendEmail(SUBJECT, recipient, msg);
}

    public String generateRegistrationToken(){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < TOKEN_SIZE ; i++ ){
            stringBuilder.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return stringBuilder.toString();
    }


   }


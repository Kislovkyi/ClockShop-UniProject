package clockshop.extras;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails using the configured JavaMailSender.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Constructor for the EmailService class.
     *
     * @param mailSender The JavaMailSender to be used for sending emails.
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with the specified recipient, subject, and text.
     *
     * @param to The recipient of the email.
     * @param subject The subject of the email.
     * @param text The text of the email.
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
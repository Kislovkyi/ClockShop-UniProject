package clockshop.extras;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link EmailService}
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender mailSenderMock;

	@InjectMocks
	private EmailService emailService;

	@Test
	void sendEmail_ShouldSendEmailSuccessfully() {

		String to = "recipient@example.com";
		String subject = "Test Subject";
		String text = "Test Body";

		emailService.sendEmail(to, subject, text);

		verify(mailSenderMock).send(any(SimpleMailMessage.class));
	}
}

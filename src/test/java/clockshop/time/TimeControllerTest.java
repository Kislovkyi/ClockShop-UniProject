package clockshop.time;

import clockshop.time.TimeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.time.BusinessTime;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link TimeController}
 */
class TimeControllerTest {

	@Mock
	private BusinessTime businessTime;

	private TimeController timeController;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		timeController = new TimeController(businessTime);
	}

	@Test
	void testTimeSkip_1Hour() {
		String result = timeController.timeSkip(3600);
		verify(businessTime).forward(Duration.ofSeconds(3600));
		assertEquals("redirect:/accounting", result);
	}

	@Test
	void testTimeSkip_1Day() {
		String result = timeController.timeSkip(86400);
		verify(businessTime, times(24)).forward(Duration.ofSeconds(3600));
		assertEquals("redirect:/accounting", result);
	}

	@Test
	void testTimeSkip_DefaultCase() {
		String result = timeController.timeSkip(12345);
		verify(businessTime).forward(Duration.ofSeconds(12345));
		assertEquals("redirect:/accounting", result);
	}
}

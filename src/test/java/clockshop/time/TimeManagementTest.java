package clockshop.time;

import clockshop.time.TimeController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.time.BusinessTime;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TimeManagement}
 */
class TimeManagementTest {

	@Test
	void testTimeSkipFor3600Seconds() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		TimeController timeController = new TimeController(businessTimeMock);

		String result = timeController.timeSkip(3600);

		Mockito.verify(businessTimeMock).forward(Duration.ofSeconds(3600));
		assertEquals("redirect:/accounting", result);
	}

	@Test
	void testTimeSkipFor86400Seconds() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		TimeController timeController = new TimeController(businessTimeMock);

		String result = timeController.timeSkip(86400);

		Mockito.verify(businessTimeMock, Mockito.times(24)).forward(Duration.ofSeconds(3600));
		assertEquals("redirect:/accounting", result);
	}


	@Test
	void testTimeSkipForCustomTime() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		TimeController timeController = new TimeController(businessTimeMock);

		String result = timeController.timeSkip(12345);

		Mockito.verify(businessTimeMock).forward(Duration.ofSeconds(12345));
		assertEquals("redirect:/accounting", result);
	}

	@Test
	void dateStringTest(){
		LocalDateTime now = LocalDateTime.now();
		BusinessTime businessTime = mock(BusinessTime.class);
		when(businessTime.getTime()).thenReturn(now);
		TimeManagement timeManagement = new TimeManagement(businessTime);
		String result = timeManagement.dateString();
		String expected = now.getDayOfMonth() + "." +
			now.getMonthValue() + "." + now.getYear();
		assertEquals(expected, result);
	}

	@Test
	void timeStringTest(){
		LocalDateTime now = LocalDateTime.now();
		BusinessTime businessTime = mock(BusinessTime.class);
		when(businessTime.getTime()).thenReturn(now);
		TimeManagement timeManagement = new TimeManagement(businessTime);
		String result = timeManagement.timeString();
		String expected = now.getHour() + ":" + now.getMinute();
		assertEquals(expected, result);
	}
}

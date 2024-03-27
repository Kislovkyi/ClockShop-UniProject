package clockshop.grandfatherclock;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.salespointframework.core.Currencies.EURO;

/**
 * Tests for {@link GrandfatherClock}
 */
class GrandfatherClockTest {

	private GrandfatherClock grandfatherClock;

	@Mock
	private GFCCatalog GFCCatalog;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		Money price = Money.of(100, EURO);
		String companyName = "Example Clocks Inc.";
		String clockName = "Antique Grandfather Clock";
		String description = "Stuff";
		double discount = 0.1;

		grandfatherClock = new GrandfatherClock(clockName, price, companyName,description,discount);
	}

	@Test
	void testGrandfatherClockConstructor() {

		assertNotNull(grandfatherClock);
		assertEquals("Antique Grandfather Clock", grandfatherClock.getName());
		assertEquals(Money.of(100, EURO), grandfatherClock.getPrice());
		assertEquals("Example Clocks Inc.", grandfatherClock.getCompanyName());
	}

	@Test
	void testEmptyClockConstructor() {
		GrandfatherClock emptyGFC = new GrandfatherClock();
		assertNotNull(emptyGFC);
	}



}

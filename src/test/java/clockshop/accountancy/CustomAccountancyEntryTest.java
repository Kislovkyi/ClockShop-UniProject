package clockshop.accountancy;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link CustomAccountancyEntry}
 */
class CustomAccountancyEntryTest {

	private CustomAccountancyEntry entry;
	@BeforeEach
	void setUp() {
		entry = new CustomAccountancyEntry(Money.of(1, EURO),"Description");
	}

	@Test
	void testCustomAccountancyEntryConstructor(){
		assertNotNull(entry);
	}

	@Test
	void emptyCustomAccountancyEntryConstructorTest(){
		CustomAccountancyEntry empty = new CustomAccountancyEntry();
		assertNotNull(empty);
	}
}
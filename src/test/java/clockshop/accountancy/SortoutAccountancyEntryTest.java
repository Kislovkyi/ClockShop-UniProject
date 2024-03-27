package clockshop.accountancy;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link SortoutAccountancyEntry}
 */
class SortoutAccountancyEntryTest {

	@Mock
	Product.ProductIdentifier productIdentifier;
	private SortoutAccountancyEntry entry;

	@BeforeEach
	void setUp() {
		entry = new SortoutAccountancyEntry(Money.of(1, EURO),"Description",productIdentifier, Quantity.of(1));
	}
	@Test
	void testSortOutAccountancyEntryConstructor(){
		assertNotNull(entry);
	}

	@Test
	void getQuantity() {
		assertEquals(Quantity.of(1),entry.getQuantity());
	}

	@Test
	void getProductId() {
		assertEquals(productIdentifier,entry.getProductId());
	}

	@Test
	void emptySortOutAccountancyEntryConstructorTest(){
		SortoutAccountancyEntry empty = new SortoutAccountancyEntry();
		assertNotNull(empty);
		assertNull(empty.getProductId());
		assertNull(empty.getQuantity());
	}

	@Test
	void equalsTest(){
		SortoutAccountancyEntry empty = new SortoutAccountancyEntry();
		boolean resultFalse = empty.equals(null);
		boolean resultFalse2 = empty.equals(new Object());
		boolean resultTrue = empty.equals(empty);

		assertTrue(resultTrue);
		assertFalse(resultFalse);
		assertFalse(resultFalse2);
	}

}
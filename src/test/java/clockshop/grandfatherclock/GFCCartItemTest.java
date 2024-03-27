package clockshop.grandfatherclock;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link GFCCartItem}
 */
class GFCCartItemTest {

	private GFCCartItem gfcCartItem;

	@BeforeEach
	void setUp() {
		gfcCartItem = new GFCCartItem("Test", Money.of(10, EURO), "Test", Quantity.of(1), Product.ProductIdentifier.of("123"));
	}

	@Test
	void testGFCCartItemConstructor() {
		assertNotNull(gfcCartItem);
		assertEquals("Test", gfcCartItem.getName());
		assertEquals(Money.of(10, EURO), gfcCartItem.getPrice());
		assertEquals("Test", gfcCartItem.getCompanyName());
		assertEquals(Quantity.of(1), gfcCartItem.getQuantity());
		assertEquals(Product.ProductIdentifier.of("123"), gfcCartItem.getId());
	}

	@Test
	void testSetters() {
		//gfcCartItem.setName("Updated Name");
		//assertEquals("Updated Name", gfcCartItem.getName());

		gfcCartItem.setPrice(Money.of(15.0, EURO));
		assertEquals(Money.of(15.0, EURO), gfcCartItem.getPrice());

		gfcCartItem.setCompanyName("Updated Company");
		assertEquals("Updated Company", gfcCartItem.getCompanyName());

		gfcCartItem.setQuantity(Quantity.of(10));
		assertEquals(Quantity.of(10), gfcCartItem.getQuantity());

	}
}

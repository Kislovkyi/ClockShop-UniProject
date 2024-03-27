package clockshop.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ShopOrder}
 */
class ShopOrderTest {

	private ShopOrder shopOrder;



	@BeforeEach
	void setUp() {
		UserAccount.UserAccountIdentifier userAccountIdentifier = mock(UserAccount.UserAccountIdentifier.class);

		shopOrder = new ShopOrder(userAccountIdentifier,
			Cash.CASH,
			"Willi",
			"Wissen",
			"KäseStraße 5",
			"543908545432",
			"uhrenladenswt@gmail.com");

	}

	@Test
	void getForename() {
		assertEquals("Willi",shopOrder.getForename());
	}

	@Test
	void getName() {
		assertEquals("Wissen", shopOrder.getName());
	}

	@Test
	void getAddress() {
		assertEquals("KäseStraße 5", shopOrder.getAddress());
	}

	@Test
	void getTelephone() {
		assertEquals("543908545432", shopOrder.getTelephone());
	}

	@Test
	void getEmail() {
		assertEquals("uhrenladenswt@gmail.com", shopOrder.getEmail());
	}
}
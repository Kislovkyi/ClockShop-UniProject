package clockshop.grandfatherclock;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link GFCManagement}
 */
class GFCManagementTest {
	@Mock
	private GFCCatalog gfcCatalog;

	private GFCManagement gfcManagement;

	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		gfcManagement = new GFCManagement(gfcCatalog);
	}

	@Test
	void calculateTotalPriceTest(){
		GFCCartItem cartItem = mock(GFCCartItem.class);
		List<GFCCartItem> list = List.of(cartItem);

		when(cartItem.getPrice()).thenReturn(Money.of(10, "EUR"));
		when(cartItem.getQuantity()).thenReturn(Quantity.of(10));

		MonetaryAmount result = GFCManagement.calculateTotalPrice(list);

		assertEquals(Money.of(100, "EUR"),result);
	}

	@Test
	void findALlTest(){
		when(gfcCatalog.findAll()).thenReturn(Streamable.empty());
		Iterable<GrandfatherClock> result = gfcManagement.findAll();
	}

	@Test
	void findByIdTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		GrandfatherClock grandfatherClock = mock(GrandfatherClock.class);

		when(grandfatherClock.getId()).thenReturn(id);
		when(gfcCatalog.findById(any())).thenReturn(Optional.of(grandfatherClock));

		Optional<GrandfatherClock> result = gfcManagement.findById(id);

		assertEquals(grandfatherClock,result.get());
	}

	@Test
	void addClockTest(){

		gfcManagement.addClock("An",200,"An Uhren","HUU'M'AN", 0.1);

		verify(gfcCatalog,times(1)).save(any());
	}

}

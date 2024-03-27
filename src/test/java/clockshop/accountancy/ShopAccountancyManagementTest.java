package clockshop.accountancy;

import clockshop.catalog.Article;
import clockshop.maintenance.MaintenanceContract;
import clockshop.staff.Employee;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link ShopAccountancyManagement}
 */
@ExtendWith(MockitoExtension.class)
class ShopAccountancyManagementTest {

	@Mock
	private Accountancy accountancy;


	@InjectMocks
	private ShopAccountancyManagement shopAccountancyManagement;

	private AccountancyEntry accountancyEntry;

	@BeforeEach
	void setUp() {
		shopAccountancyManagement = new ShopAccountancyManagement(accountancy);
		accountancyEntry = mock(AccountancyEntry.class);
	}

	@Test
	void findAllByOrderByDateDescTest() {
		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));

		shopAccountancyManagement.findAllByOrderByDateDesc();

		verify(accountancy, times(1)).findAll();
	}

	@Test
	void findOrderByMonthAndYearTest() {
		LocalDateTime date1 = LocalDateTime.now();

		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));
		when(accountancyEntry.getDate()).thenReturn(Optional.of(date1));
		var result = shopAccountancyManagement.findOrderByMonthAndYear(date1.getYear(), date1.getMonthValue());

		assertTrue(result.contains(accountancyEntry));
		verify(accountancy, times(1)).findAll();
	}

	@Test
	void findOrderByMonthAndYearWhenDateIsNotPresentTest() {
		LocalDateTime date1 = LocalDateTime.now();

		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));
		when(accountancyEntry.getDate()).thenReturn(Optional.empty());
		var result = shopAccountancyManagement.findOrderByMonthAndYear(date1.getYear(), date1.getMonthValue());

		assertFalse(result.contains(accountancyEntry));
		verify(accountancy, times(1)).findAll();
	}

	@Test
	void findOrderByMonthAndYearWhenDateDoesNotMatchTest() {
		LocalDateTime date1 = LocalDateTime.now();

		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));
		when(accountancyEntry.getDate()).thenReturn(Optional.of(date1));
		var result = shopAccountancyManagement.findOrderByMonthAndYear(0, date1.getMonthValue());
		var result2 = shopAccountancyManagement.findOrderByMonthAndYear(date1.getYear(), 0);
		assertFalse(result.contains(accountancyEntry));
		assertFalse(result2.contains(accountancyEntry));
		verify(accountancy, times(2)).findAll();
	}

	@Test
	void salesVolumeOfMonthWhenEmptyTest() {
		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));

		shopAccountancyManagement.salesVolumeOfMonth(2022, 1);

		verify(accountancy, times(1)).findAll();
	}

	@Test
	void salesVolumeOfMonthTest() {
		LocalDateTime now = LocalDateTime.now();
		when(accountancyEntry.getDate()).thenReturn(Optional.of(now));
		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));

		when(accountancyEntry.getValue()).thenReturn(Money.of(200, "EUR"));
		float result = shopAccountancyManagement.salesVolumeOfMonth(now.getYear(), now.getMonthValue());

		assertEquals(200, result);
		verify(accountancy, times(1)).findAll();
	}

	@Test
	void findAllTest() {
		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry));

		shopAccountancyManagement.findAll();

		verify(accountancy, times(1)).findAll();
	}

	@Test
	void getEntryTest() {
		when(accountancy.get(any())).thenReturn(Optional.of(accountancyEntry));
		AccountancyEntry result = shopAccountancyManagement.getEntry(accountancyEntry.getId());
		assertEquals(accountancyEntry, result);
	}

	@Test
	void paySalaryTest() {
		Employee.EmployeeIdentifier id = mock(Employee.EmployeeIdentifier.class);

		shopAccountancyManagement.paySalary(Money.of(20, "EUR"), "salary", id);

		verify(accountancy, times(1)).add(any(SalaryAccountancyEntry.class));
	}

	@Test
	void sortOutItemTest() {
		Article article = mock(Article.class);
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);

		when(article.getId()).thenReturn(id);

		shopAccountancyManagement.sortOutItem(Money.of(20, "EUR"), "sortOut", article, Quantity.of(1));

		verify(accountancy, times(1)).add(any(SortoutAccountancyEntry.class));
	}

	@Test
	void payContractTest(){
		MaintenanceContract contract = mock(MaintenanceContract.class);

		shopAccountancyManagement.payContract(Money.of(20,"EUR"), "Contract","Da","ri","us");

		verify(accountancy, times(1)).add(any(MaintenanceAccountancyEntry.class));
	}

	@Test
	void calculateTotalsTest(){
		ArrayList<AccountancyEntry> entries = new ArrayList<>();
		AccountancyEntry entry = new AccountancyEntry(Money.of(100,"EUR"),"BlahBlah");
		AccountancyEntry entry2 = new AccountancyEntry(Money.of(-50,"EUR"),"BlahBlahBlah");

		entries.add(entry);
		entries.add(entry2);

		when(shopAccountancyManagement.findAll()).thenReturn(Streamable.of(entries));

		MonetaryAmount[] amount = new MonetaryAmount[]{Money.of(50,EURO), Money.of(-50,EURO)};

		assertEquals(amount[0],shopAccountancyManagement.calculateTotals()[0]);
		assertEquals(amount[1],shopAccountancyManagement.calculateTotals()[1]);
	}

	@Test
	void searchTest(){
		AccountancyEntry accountancyEntry1 = mock(AccountancyEntry.class);
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		when(accountancyEntry1.getId()).thenReturn(id);
		when(id.toString()).thenReturn("Hallo");
		when(shopAccountancyManagement.findAll()).thenReturn(Streamable.of(accountancyEntry1));

		List<AccountancyEntry> result = shopAccountancyManagement.search("Hallo");

		assertEquals(List.of(accountancyEntry1), result);
	}

	@Test
	void getAccountancyPageTest(){
		AccountancyEntry accountancyEntry1 = mock(AccountancyEntry.class);
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		when(accountancy.findAll()).thenReturn(Streamable.of(accountancyEntry1));


		var result = shopAccountancyManagement.getAllOrderPage(0, 5);

		verify(accountancy,times(1)).findAll();
	}
}
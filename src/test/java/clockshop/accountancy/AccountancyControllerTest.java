package clockshop.accountancy;

import clockshop.catalog.Article;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;
import clockshop.staff.EmployeeManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.accountancy.OrderPaymentEntry;
import org.salespointframework.order.ChargeLine;
import org.salespointframework.order.Order;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.ui.Model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link AccountancyController}
 */
class AccountancyControllerTest {
	@Mock
	private ShopInventoryManagement inventory;

	@Mock
	private ShopAccountancyManagement accountancy;

	@Mock
	private BusinessTime businessTime;

	@Mock
	private ShopOrderManagement orderManagement;

	private AccountancyController accountancyController;

	@Mock
	private Model model;

	@Mock
	private StatisticsManagement statisticsManagement;

	@Mock
	private EmployeeManagement employeeManagement;



	@BeforeEach
	void setUp() {
		inventory = mock(ShopInventoryManagement.class);
		accountancy = mock(ShopAccountancyManagement.class);
		orderManagement = mock(ShopOrderManagement.class);
		businessTime = mock(BusinessTime.class);
		statisticsManagement = mock(StatisticsManagement.class);
		employeeManagement = mock(EmployeeManagement.class);
		model = mock(Model.class);

		accountancyController = new AccountancyController(inventory,
			accountancy,
			orderManagement,
			employeeManagement,
			businessTime,
			statisticsManagement);
	}

	@Test
	void accountingTest() {
		Model m = mock(Model.class);

		AccountancyEntry entry1 = mock(AccountancyEntry.class);
		AccountancyEntry entry2 = mock(AccountancyEntry.class);
		AccountancyEntry entry3 = mock(AccountancyEntry.class);
		AccountancyEntry entry4 = mock(AccountancyEntry.class);

		LocalDateTime date1 = LocalDateTime.of(2012,12,25,5,12,13);
		LocalDateTime date2 = LocalDateTime.of(2013,12,25,5,12,13);

		when(entry1.getDate()).thenReturn(Optional.of(date1));
		when(entry2.getDate()).thenReturn(Optional.of(date2));
		when(entry3.getDate()).thenReturn(Optional.of(date2));
		when(entry4.getDate()).thenReturn(Optional.of(date1));
		when(businessTime.getTime()).thenReturn(LocalDateTime.now());

		when(entry1.getValue()).thenReturn(Money.of(-10,EURO));
		when(entry2.getValue()).thenReturn(Money.of(-10,EURO));
		when(entry3.getValue()).thenReturn(Money.of(10,EURO));
		when(entry4.getValue()).thenReturn(Money.of(10,EURO));

		List<AccountancyEntry> list = List.of(entry1, entry2, entry3,entry4);
		Streamable<AccountancyEntry> streamable = Streamable.of(entry1, entry2, entry3,entry4);
		when(accountancy.findAllByOrderByDateDesc()).thenReturn(list);
		when(accountancy.findAll()).thenReturn(streamable);
		when(entry1.isExpense()).thenReturn(false);
		when(entry2.isExpense()).thenReturn(false);
		when(entry3.isExpense()).thenReturn(true);
		when(entry4.isExpense()).thenReturn(true);
		when(accountancy.calculateTotals()).thenReturn(new MonetaryAmount[]{Money.of(0,EURO), Money.of(0,EURO)});


		String result = accountancyController.accounting(m,0,10);

		assertEquals("Accounting/accounting", result);
		verify(m, times(14)).addAttribute(any(),any());
	}


	@Test
	void detailsWhenPaymentEntryTest() {
		AccountancyEntry entry1 = mock(OrderPaymentEntry.class);
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);
		UserAccount.UserAccountIdentifier userID = mock(UserAccount.UserAccountIdentifier.class);
		Order order = new Order(userID, Cash.CASH);
		Article article = mock(Article.class);


		OrderPaymentEntry defaultOrderEntry = mock(OrderPaymentEntry.class);
		OrderPaymentEntry repairOrderEntry = mock(OrderPaymentEntry.class);
		OrderPaymentEntry gfcOrderEntry = mock(OrderPaymentEntry.class);
		OrderPaymentEntry buyOrderEntry = mock(OrderPaymentEntry.class);
		ChargeLine defaultChargeLine = new ChargeLine(Money.of(0,EURO),"ORDER");
		ChargeLine repairChargeLine = new ChargeLine(Money.of(0,EURO),"ORDER");
		ChargeLine gfcChargeLine = new ChargeLine(Money.of(0,EURO),"ORDER");
		ChargeLine buyChargeLine = new ChargeLine(Money.of(0,EURO),"ORDER");
		AccountancyEntry.AccountancyEntryIdentifier defaultId = mock(AccountancyEntry.AccountancyEntryIdentifier.class);
		AccountancyEntry.AccountancyEntryIdentifier repairId = mock(AccountancyEntry.AccountancyEntryIdentifier.class);
		AccountancyEntry.AccountancyEntryIdentifier gfcId = mock(AccountancyEntry.AccountancyEntryIdentifier.class);
		AccountancyEntry.AccountancyEntryIdentifier buyId = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		when(defaultOrderEntry.getId()).thenReturn(defaultId);
		when(repairOrderEntry.getId()).thenReturn(defaultId);
		when(gfcOrderEntry.getId()).thenReturn(defaultId);
		when(buyOrderEntry.getId()).thenReturn(defaultId);

		ArrayList<OrderPaymentEntry> entryList = new ArrayList<>();
		entryList.add(defaultOrderEntry);
		entryList.add(repairOrderEntry);
		entryList.add(gfcOrderEntry);
		entryList.add(buyOrderEntry);

		when(article.supports(any())).thenReturn(true);
		when(article.getPrice()).thenReturn(Money.of(200, "EUR"));

		order.addOrderLine(article, Quantity.of(20));

		when(accountancy.getEntry(id)).thenReturn(entry1);
		when(orderManagement.getOrder(any())).thenReturn(order);
		when(entry1.getDescription()).thenReturn("Example Description");


		String viewName = accountancyController.details(model, id);

		verify(model,times(10)).addAttribute(any(),any());
		assertEquals("Accounting/accountingEntry", viewName);
	}

	@Test
	void detailsWhenEntryNotFoundTest() {
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		String viewName = accountancyController.details(model, id);

		assertEquals("Accounting/accountingEntry", viewName);
	}

	@Test
	void detailsWhenSortOutEntryFoundTest() {
		AccountancyEntry entry1 = mock(SortoutAccountancyEntry.class);
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		when(accountancy.getEntry(id)).thenReturn(entry1);

		String viewName = accountancyController.details(model, id);

		assertEquals("Accounting/accountingEntry", viewName);
		verify(model,times(7)).addAttribute(any(),any());
	}

	@Test
	void detailsWhenSalaryAccountancyEntryFoundTest() {
		AccountancyEntry entry1 = mock(SalaryAccountancyEntry.class);
		AccountancyEntry.AccountancyEntryIdentifier id = mock(AccountancyEntry.AccountancyEntryIdentifier.class);

		when(accountancy.getEntry(id)).thenReturn(entry1);

		String viewName = accountancyController.details(model, id);

		assertEquals("Accounting/accountingEntry", viewName);
		verify(model,times(6)).addAttribute(any(),any());
	}

	@Test
	void cancelOrder() {

		String viewName = accountancyController.cancelOrder(model, "orderIdString", "Reason");



		assertEquals("redirect:/accounting", viewName);
	}

	@Test
	void getOverview() {
		when(accountancy.findOrderByMonthAndYear(any(Integer.class), any(Integer.class))).thenReturn(new ArrayList<>());
		when(accountancy.salesVolumeOfMonth(any(Integer.class), any(Integer.class))).thenReturn(0.0f);

		String viewName = accountancyController.getOverview("2023.04", model);

		assertEquals("Accounting/accountingOverview", viewName);
		verify(model).addAttribute(eq("entries"), anyList());
		verify(model).addAttribute(eq("sum"), any(Float.class));
	}

	@Test
	void getAllOrder(){
		when(accountancy.findAll()).thenReturn(Streamable.empty());

		assertEquals("Accounting/accountingAllOrder",accountancyController.getAllOrder(model,1,1,""));
		assertEquals("Accounting/accountingAllOrder",accountancyController.getAllOrder(model,null,null,null));
		assertEquals("Accounting/accountingAllOrder",accountancyController.getAllOrder(model,1,-1,"LOL"));
	}

}
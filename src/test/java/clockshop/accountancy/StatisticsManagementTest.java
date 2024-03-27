package clockshop.accountancy;

import clockshop.catalog.Article;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;
import clockshop.repair.Repair;
import clockshop.repair.RepairManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.accountancy.OrderPaymentEntry;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.order.Order;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link StatisticsManagement}
 */
class StatisticsManagementTest {
	@Mock
	private BusinessTime businessTime;
	@Mock
	private ShopOrderManagement shopOrderManagement;
	@Mock
	private ShopAccountancyManagement shopAccountancyManagement;
	@Mock
	private ShopInventoryManagement shopInventoryManagement;

	@Mock
	private RepairManagement repairManagement;

	private StatisticsManagement statisticsManagement;

	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		statisticsManagement = new StatisticsManagement(businessTime,
			shopOrderManagement,
			shopInventoryManagement,
			shopAccountancyManagement,
                repairManagement);
	}

	@Test
	void findNonSellersTest(){
		OrderPaymentEntry orderPaymentEntry = mock(OrderPaymentEntry.class);
		Order.OrderIdentifier orderID = mock(Order.OrderIdentifier.class);
		Article article = mock(Article.class);
		Product.ProductIdentifier productID = mock(Product.ProductIdentifier.class);
		Order order = mock(Order.class);
		UserAccount.UserAccountIdentifier userID = mock(UserAccount.UserAccountIdentifier.class);
		Order testOrder = new Order(userID, Cash.CASH);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);
		InventoryItems<ShopInventoryItem> items = InventoryItems.of(Streamable.of(shopInventoryItem));


		when(businessTime.getTime()).thenReturn(LocalDateTime.now());
		when(article.getId()).thenReturn(productID);
		when(shopAccountancyManagement.findOrderByMonthAndYear(any(),any())).thenReturn(List.of(orderPaymentEntry));
		when(shopInventoryManagement.findAllArticles()).thenReturn(Streamable.of(article));
		when(shopOrderManagement.getOrder(any())).thenReturn(order);
		when(order.getOrderLines()).thenReturn(testOrder.getOrderLines());
		when(orderPaymentEntry.getOrderIdentifier()).thenReturn(orderID);
		when(shopInventoryManagement.findProduct(any())).thenReturn(article);
		when(shopInventoryManagement.findByArticleID(any())).thenReturn(items);

		statisticsManagement.findNonSellers();

        verify(orderPaymentEntry, times(1)).getOrderIdentifier();
		verify(shopInventoryItem, times(1)).setNonSeller(true);
		verify(shopInventoryManagement, times(2)).save(shopInventoryItem);
    }

	@Test
	void sellDataTest(){
		Article article = mock(Article.class);

		StatisticsManagement.SellData result = new StatisticsManagement.SellData(article, 10);

		assertEquals(article, result.getArticle());
		assertEquals(10, result.getNumber());
	}

	@Test
	void getFinishedRepairCount(){
		Repair repair = mock(Repair.class);


		when(repair.isFinished()).thenReturn(true);
		when(repairManagement.findAll()).thenReturn(Streamable.of(repair));


		long result = statisticsManagement.getFinishedRepairsCount();

		assertEquals(1, result);
	}

	@Test
	void getMostSellsTest() {

		UserAccount.UserAccountIdentifier id = mock(UserAccount.UserAccountIdentifier.class);
		Article article = new Article("LOL",Money.of(0,"EUR"), Article.ArticleType.CLOCK,"",0.5);
		AccountancyEntry accountancyEntry = mock(OrderPaymentEntry.class);
		Order order = new Order(id, Cash.CASH);
		order.addOrderLine(article, Quantity.of(5));
		order.addOrderLine(article, Quantity.of(2));
		when(businessTime.getTime()).thenReturn(LocalDateTime.now());
		when(shopAccountancyManagement.findOrderByMonthAndYear(any(), any())).thenReturn(Streamable.of(accountancyEntry).toList());
		when(shopOrderManagement.getOrder(any())).thenReturn(order);


		List<StatisticsManagement.SellData> result = statisticsManagement.getMostSells();

		ArrayList<StatisticsManagement.SellData> expected = new ArrayList<>();
		expected.add(new StatisticsManagement.SellData(article,7));


		assertEquals(expected.get(0).getNumber(),result.get(0).getNumber());


		verify(shopInventoryManagement,times(2)).findProduct(any());
	}

	@Test
	void getRecentOrdersTest() {

		OrderPaymentEntry orderPaymentEntry = mock(OrderPaymentEntry.class);
		var accountancyEntries = new ArrayList<AccountancyEntry>();
		accountancyEntries.add(new CustomAccountancyEntry(Money.of(10, "EUR"),"TEST"));
		accountancyEntries.add(orderPaymentEntry);

		when(shopAccountancyManagement.findAll()).thenReturn(Streamable.of(accountancyEntries));

		assertEquals(List.of(orderPaymentEntry) ,statisticsManagement.getRecentOrders());
	}

	@Test
	void getPieChartDataTest() {
		Map<String, Double> pieData = new LinkedHashMap<>();
		pieData.put("Normal",0.0);
		pieData.put("Repair",0.0);
		pieData.put("Maintenance",0.0);
		pieData.put("GFC",0.0);

		OrderPaymentEntry orderPaymentEntry = mock(OrderPaymentEntry.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);
		Order order = mock(Order.class);
		Totalable<ChargeLine> totalable = mock(Totalable.class);

		var accountancyEntries = new ArrayList<AccountancyEntry>();
		accountancyEntries.add(new CustomAccountancyEntry(Money.of(10, "EUR"),"TEST"));
		accountancyEntries.add(orderPaymentEntry);

		when(shopAccountancyManagement.findAll()).thenReturn(Streamable.of(accountancyEntries));
		when(shopOrderManagement.getOrder(orderPaymentEntry.getOrderIdentifier())).thenReturn(order);
		when(order.getChargeLines()).thenReturn(totalable);


		assertEquals(pieData,statisticsManagement.getPieChartData());
	}

	@Test
	void getGraphDataTest() {
		Map<String, Integer> graphData = new LinkedHashMap<>() {{
			put("Jan", 0);
			put("Feb", 0);
			put("Mar", 0);
			put("Apr", 0);
			put("May", 0);
			put("Jun", 0);
			put("Jul", 0);
			put("Aug", 0);
			put("Sep", 0);
			put("Oct", 0);
			put("Nov", 0);
			put("Dec", 0);
		}};

		when(businessTime.getTime()).thenReturn(LocalDateTime.of(2024,1,1,1, 1));

		assertEquals(graphData, statisticsManagement.getGraphData());

	}
}

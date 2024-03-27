package clockshop.order;

import clockshop.catalog.Article;
import clockshop.grandfatherclock.GFCCartItem;
import clockshop.grandfatherclock.GFCManagement;
import clockshop.grandfatherclock.GrandfatherClock;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.repair.Repair;
import clockshop.staff.Employee;
import clockshop.staff.EmployeeManagement;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Tests for {@link ShopOrderManagement}
 */
class ShopOrderManagementTest {

	@Mock
	private EmployeeManagement employeeManagement;
	@Mock
	private ShopInventoryManagement shopInventoryManagement;
	@Mock
	private OrderManagement<Order> orderManagement;

	@Mock
	private GFCManagement gfcManagement;

	private ShopOrderManagement shopOrderManagement;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		shopOrderManagement = new ShopOrderManagement(orderManagement, employeeManagement, shopInventoryManagement,gfcManagement);
	}

	@Test
	void cancelOrderTest() {
		Order testOrder = mock(Order.class);
		shopOrderManagement.cancelOrder(testOrder, "Bitte mit Sahne");
		verify(orderManagement, times(1)).cancelOrder(testOrder, "Bitte mit Sahne");
	}

	@Test
	void getOrderTest() {
		Order testOrder = mock(Order.class);
		Order.OrderIdentifier id = mock(Order.OrderIdentifier.class);
		when(testOrder.getId()).thenReturn(id);
		when(orderManagement.get(id)).thenReturn(Optional.of(testOrder));

		Order result = shopOrderManagement.getOrder(id);
		verify(orderManagement, times(1)).get(id);
		assertEquals(testOrder, result);
	}



	@Test
	void orderMoreItemsTest() {
		UserAccount userAccount = mock(UserAccount.class);
		Employee employee = mock(Employee.class);
		Principal principal = mock(Principal.class);
		UserAccount.UserAccountIdentifier id = mock(UserAccount.UserAccountIdentifier.class);
		Article article = mock(Article.class);
		ShopInventoryItem storedItem = mock(ShopInventoryItem.class);
		when(principal.getName()).thenReturn("boss");
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.of(employee));
		when(employee.getUserAccount()).thenReturn(userAccount);
		when(userAccount.getId()).thenReturn(id);
		when(article.supports(any())).thenReturn(true);
		when(article.getPrice()).thenReturn(Money.of(10, "EUR"));
		when(shopInventoryManagement.findStoredItem(article.getId())).thenReturn(storedItem);


		shopOrderManagement.orderMoreItems(article, 5, principal);
		verify(orderManagement, times(1)).payOrder(any());
		verify(orderManagement, times(1)).completeOrder(any());
	}

	@Test
	void payAllSalaryTest() {
		Employee employee = mock(Employee.class);

		when(employee.getUsername()).thenReturn("Leon");
		when(employeeManagement.findAll()).thenReturn(Streamable.of(employee));

		shopOrderManagement.payAllSalary();
		verify(employeeManagement, times(1)).paySalaryByUsername(any());
	}

	@Test
	void completeRepairOrderTest() {
		Repair repair = mock(Repair.class);
		Employee employee = mock(Employee.class);
		Principal principal = mock(Principal.class);
		UserAccount userAccount = mock(UserAccount.class);
		Article article = mock(Article.class);
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		UserAccount.UserAccountIdentifier userAccountIdentifier = mock(UserAccount.UserAccountIdentifier.class);
		ShopInventoryItem item = mock(ShopInventoryItem.class);

		when(article.supports(any())).thenReturn(true);
		when(article.getPrice()).thenReturn(Money.of(1, "EUR"));
		when(userAccount.getId()).thenReturn(userAccountIdentifier);
		when(principal.getName()).thenReturn("boss");
		when(employee.getUserAccount()).thenReturn(userAccount);
		when(shopInventoryManagement.findProduct(any())).thenReturn(article);
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.of(employee));
		when(shopInventoryManagement.findSaleItem(any())).thenReturn(item);

		Map<Product.ProductIdentifier, Integer> map = new HashMap<>();
		map.put(id, 10);

		Order.OrderIdentifier result = shopOrderManagement.completeRepairOrder(repair, map, principal);

		verify(orderManagement, times(1)).payOrder(any());
		verify(orderManagement, times(1)).completeOrder(any());
		assertNotNull(result);
	}

	@Test
	void orderGFCTest() throws DocumentException, IOException, WriterException {
		GFCCartItem gfcCartItem = mock(GFCCartItem.class);
		List<GFCCartItem> gfcCartItems = new ArrayList<>();
		gfcCartItems.add(gfcCartItem);
		Principal principal = mock(Principal.class);
		Employee employee = mock(Employee.class);
		UserAccount userAccount = mock(UserAccount.class);
		UserAccount.UserAccountIdentifier userAccountIdentifier = mock(UserAccount.UserAccountIdentifier.class);
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);
		GrandfatherClock grandfatherClock = mock(GrandfatherClock.class);

		when(principal.getName()).thenReturn("boss");
		when(gfcCartItem.getPrice()).thenReturn(Money.of(10, "EUR"));
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.of(employee));
		when(employee.getUserAccount()).thenReturn(userAccount);
		when(userAccount.getId()).thenReturn(userAccountIdentifier);
		when(gfcCartItem.getId()).thenReturn(productIdentifier);
		when(gfcManagement.findById(any())).thenReturn(Optional.of(grandfatherClock));
		when(gfcCartItem.getQuantity()).thenReturn(Quantity.of(1));
		when(grandfatherClock.supports(any())).thenReturn(true);
		when(grandfatherClock.getPrice()).thenReturn(Money.of(20, "EUR"));

		shopOrderManagement.orderGFC(gfcCartItems, principal, "", "", "", "", "");



		verify(orderManagement, times(1)).payOrder(any());
		verify(orderManagement, times(1)).completeOrder(any());
	}

	@Test
	void defaultOrderTest() throws DocumentException, IOException, WriterException {
		Cart cart = mock(Cart.class);
		UserAccount userAccount = mock(UserAccount.class);
		UserAccount.UserAccountIdentifier id = mock(UserAccount.UserAccountIdentifier.class);
		Order order = mock(Order.class);

		when(cart.addItemsTo(any())).thenReturn(order);
		when(userAccount.getId()).thenReturn(id);

		Order result = shopOrderManagement.defaultOrder(cart,
			userAccount,
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com");

		verify(userAccount,times(1)).getId();
		verify(cart,times(1)).clear();
		verify(orderManagement,times(1)).payOrder(any());
		verify(orderManagement,times(1)).completeOrder(any());
	}

	@Test
	void getOrderItemsTest(){
		List<GFCCartItem> gfcCartItemList = List.of(mock(GFCCartItem.class));
		shopOrderManagement.setOrderItems(gfcCartItemList);

		List<GFCCartItem> result = shopOrderManagement.getOrderItems();
		assertEquals(gfcCartItemList, result);
	}

	@Test
	void searchGFCCartItemByNameTest(){
		GFCCartItem gfcCartItem = mock(GFCCartItem.class);

		when(gfcCartItem.getName()).thenReturn("An");

		List<GFCCartItem> gfcCartItemList = List.of(gfcCartItem);
		shopOrderManagement.setOrderItems(gfcCartItemList);

		List<GFCCartItem> result = shopOrderManagement.searchGFCCartItemByName("An");

		assertEquals(gfcCartItem,result.get(0));
	}

	@Test
	void getGFCOrdersPageTest(){
		List<GFCCartItem> gfcCartItemList = List.of(mock(GFCCartItem.class));
		shopOrderManagement.setOrderItems(gfcCartItemList);

		var result = shopOrderManagement.getGFCOrdersPage(0, 5);

		assertEquals(gfcCartItemList,result.get().toList());

	}
}

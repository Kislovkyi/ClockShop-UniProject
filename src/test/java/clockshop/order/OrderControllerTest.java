package clockshop.order;


import clockshop.catalog.Article;
import clockshop.grandfatherclock.GFCCartItem;
import clockshop.grandfatherclock.GrandfatherClock;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link OrderController}
 */
class OrderControllerTest {
	@Mock
	OrderManagement<Order> orderManagement;
	@Mock
	ShopInventoryManagement shopInventoryManagement;
	@Mock
	ShopOrderManagement shopOrderManagement;

	OrderController orderController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		orderController = new OrderController(shopOrderManagement, shopInventoryManagement);
	}

	@Test
	void cartInitTest(){
		Cart result = orderController.initializeCart();
        assertTrue(result.isEmpty());
	}

	@Test
	void basketTest(){
		Model m = new ExtendedModelMap();
		String result = orderController.basket(m);
		assertEquals("cart", result);
	}
	@Test
	void orderTest(){
		Model m = new ExtendedModelMap();
		String result = orderController.orders(m);
		assertEquals("orders", result);
	}

	@Test
	void addItemTest(){
		Article article = mock(Article.class);
		ShopInventoryItem item = mock(ShopInventoryItem.class);
		Cart cart = mock(Cart.class);
		CartItem cartItem = mock(CartItem.class);

		when(cart.getQuantity(article)).thenReturn(Quantity.of(5));
		when(item.getQuantity()).thenReturn(Quantity.of(50));
		when(cart.addOrUpdateItem(any(),any())).thenReturn(Optional.of(cartItem));
		when(shopInventoryManagement.findSaleItem(any())).thenReturn(item);

		String result = orderController.addItem(article, 5, cart);

		assertEquals("redirect:/catalog", result);
		verify(cart, times(1)).addOrUpdateItem(any(),any());
	}

	@Test
	void addItemWhenQuantityInInventoryIsLessTest(){
		Article article = mock(Article.class);
		ShopInventoryItem item = mock(ShopInventoryItem.class);
		Cart cart = mock(Cart.class);
		CartItem cartItem = mock(CartItem.class);

		when(cart.getQuantity(article)).thenReturn(Quantity.of(5));
		when(item.getQuantity()).thenReturn(Quantity.of(0));
		when(cart.addOrUpdateItem(any(),any())).thenReturn(Optional.of(cartItem));
		when(shopInventoryManagement.findSaleItem(any())).thenReturn(item);

		String result = orderController.addItem(article, 5, cart);

		assertEquals("redirect:/catalog", result);
		verify(cart, times(0)).addOrUpdateItem(any(),any());
	}


	@Test
	void buyTest() throws DocumentException, IOException, WriterException {
		Cart cart = new Cart();

		Principal principal = mock(Principal.class);
		UserAccount userAccount = mock(UserAccount.class);
		Order order = mock(Order.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);

		GrandfatherClock grandfatherClock = mock(GrandfatherClock.class);
		when(grandfatherClock.getPrice()).thenReturn(Money.of(1,"EUR"));

		Article article = new Article("Article",Money.of(20,"EUR"), Article.ArticleType.CLOCK,"des",0.5);

		List<GFCCartItem> gfcCartItemList = new ArrayList<>();


		cart.addOrUpdateItem(article,Quantity.of(1));
		cart.addOrUpdateItem(grandfatherClock,Quantity.of(1));


		when(shopOrderManagement.defaultOrder(cart,
			userAccount,
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com")).thenReturn(order);


		when(shopOrderManagement.orderGFC(gfcCartItemList,
			principal,
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com")).thenReturn(orderIdentifier);


		String result = orderController.buy(cart,
			principal,
			Optional.of(userAccount),
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com");

		assertEquals("redirect:/cart", result);
		verify(shopOrderManagement,times(1)).defaultOrder(cart,
			userAccount,
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com");

		verify(shopOrderManagement,times(1)).orderGFC(gfcCartItemList,
			principal,
			"Klaus",
			"Werner",
			"Morganstreet 21",
			"01645151234",
			"uhrenladenswt@gmail.com");
	}

	@Test
	void deleteCartItemTest(){
		Cart cart = mock(Cart.class);
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		UserAccount userAccount = mock(UserAccount.class);
		Article article = mock(Article.class);
		CartItem cartItem = mock(CartItem.class);

		when(shopInventoryManagement.findProduct(id)).thenReturn(article);
		when(cart.addOrUpdateItem(any(),any())).thenReturn(Optional.of(cartItem));

		String result = orderController.deleteCartItem(cart, id, Optional.of(userAccount));

		assertEquals("cart", result);
		verify(cart, times(0)).addOrUpdateItem(article,-1);
	}

}

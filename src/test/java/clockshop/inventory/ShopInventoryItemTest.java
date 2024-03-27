package clockshop.inventory;

import clockshop.catalog.Article;
import clockshop.repair.Repair;
import clockshop.repair.RepairType;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;

import javax.money.Monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.salespointframework.core.Currencies.EURO;

/**
 * Tests for {@link ShopInventoryItem}
 */
class ShopInventoryItemTest {

	private ShopInventoryItem shopInventoryItem;

	private  ShopInventoryItem shopInventoryItem1;
	@Mock
	private Article article;

	@BeforeEach
	void setUp() {
		article = mock(Article.class);
		Product product = mock(Product.class);
		when(article.getName()).thenReturn("name");
		when(article.getPrice()).thenReturn(Money.of(1, EURO));
		when(article.getCategories()).thenReturn(Streamable.of("CLOCK"));

		shopInventoryItem = new ShopInventoryItem(article, Quantity.of(1),"L0815");

		shopInventoryItem1 = new ShopInventoryItem(product,Quantity.of(1),"L:654");
	}

	@AfterEach
	void tearDown() {
	}
	@Test
	void getWarehouseId() {
		assertEquals("L0815",shopInventoryItem.getWarehouseId());
	}

	@Test
	void setWarehouseId() {
		assertEquals("L0815",shopInventoryItem.getWarehouseId());
		shopInventoryItem.setWarehouseId("L0816");
		assertEquals("L0816",shopInventoryItem.getWarehouseId());
	}

	@Test
	void testShopInventoryItemConstructorWithParameters() {
		Product product = new Product("name", Money.of(1, EURO));
		Quantity quantity = Quantity.of(1);
		String warehouseId = "L0815";

		ShopInventoryItem shopInventoryItem = new ShopInventoryItem(product, quantity, warehouseId);

		assertEquals(quantity, shopInventoryItem.getQuantity());
		assertEquals(warehouseId, shopInventoryItem.getWarehouseId());
	}

	@Test
	void testShopInventoryItemConstructorWithoutParameters() {
		ShopInventoryItem shopInventoryItem = new ShopInventoryItem();

		assertNotNull(shopInventoryItem.getQuantity());
		assertEquals("0", shopInventoryItem.getWarehouseId());
	}

	@Test
	void getNonSeller() {
        assertFalse(shopInventoryItem.getNonSeller());
	}

	@Test
	void setNonSeller() {
		shopInventoryItem.setNonSeller(true);
		assertTrue(shopInventoryItem.getNonSeller());
		shopInventoryItem.setNonSeller(false);
		assertFalse(shopInventoryItem.getNonSeller());
	}

	@Test
	void getArticleTest(){

		assertEquals(article,shopInventoryItem.getArticle());

		assertNull(shopInventoryItem1.getArticle());
	}



	@Test
	void testEqualsAndHashCode() {
		ShopInventoryItem inventoryItem = new ShopInventoryItem(article,Quantity.of(1),"L:100");
		ShopInventoryItem inventoryItem2 = new ShopInventoryItem(article,Quantity.of(1),"L:100");
		ShopInventoryItem inventoryItem3 = new ShopInventoryItem(article,Quantity.of(100),"L:100");


		assertEquals(inventoryItem, inventoryItem2);
		assertNotEquals(inventoryItem, inventoryItem3);
		assertEquals(inventoryItem.hashCode(), inventoryItem2.hashCode());
		assertNotEquals(inventoryItem.hashCode(), inventoryItem3.hashCode());
	}


}
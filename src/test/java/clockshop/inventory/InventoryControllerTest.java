package clockshop.inventory;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.catalog.Article;
import clockshop.order.ShopOrderManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link InventoryController}
 */
class InventoryControllerTest {

	@Mock
	ShopOrderManagement shopOrderManagement;

	@Mock
	ShopAccountancyManagement accountancy;

	@Mock
	ShopInventoryManagement inventory;


	@Mock
	Model model;

	@Mock
	ItemForm form;

	@Mock
	BindingResult bindingResult;


	@InjectMocks
	InventoryController inventoryController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testStockWithNull() {
		when(inventory.findAllArticles()).thenReturn(Streamable.empty());
		when(inventory.findAll()).thenReturn(Streamable.empty());

		inventoryController.inventory(model, null, null, null);
		verify(model, times(4)).addAttribute(anyString(), any());
	}

	@Test
	void testStock(){
		Article article = mock(Article.class);

		when(inventory.findAllArticles()).thenReturn(Streamable.of(article));
		when(inventory.findAll()).thenReturn(Streamable.empty());

		String result = inventoryController.inventory(model, 5, 0, null);
		String notEmptyResult = inventoryController.inventory(model, 5, 0, "NOT EMPTY");


		assertEquals("Warehouse/inventory", result);
		assertEquals("Warehouse/inventory", notEmptyResult);
		verify(model, times(8)).addAttribute(any(), any());



	}

	@Test
	void testDecreaseStockTest() {
		Principal principal = mock(Principal.class);
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);
		Article article = mock(Article.class);
		ShopInventoryItem item = mock(ShopInventoryItem.class);

		when(inventory.findProduct(any())).thenReturn(article);
		InventoryItems<ShopInventoryItem> items = InventoryItems.of(Streamable.of(item));
		when(inventory.findByArticleID(any())).thenReturn(items);
		when(principal.getName()).thenReturn("boss");
		when(item.getQuantity()).thenReturn(Quantity.of(10));
		when(inventory.findStoredItem(any())).thenReturn(item);

		String result = inventoryController.decreaseStock(productIdentifier, -10, principal);

		assertEquals("redirect:/inventory", result);
		verify(item, times(1)).decreaseQuantity(Quantity.of(10));
	}

	@Test
	void decreaseStockWhenMultipleLocationTest(){
		Principal principal = mock(Principal.class);
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);
		Article article = mock(Article.class);
		ShopInventoryItem item = mock(ShopInventoryItem.class);
		ShopInventoryItem item2 = mock(ShopInventoryItem.class);

		when(inventory.findProduct(any())).thenReturn(article);
		InventoryItems<ShopInventoryItem> items = InventoryItems.of(Streamable.of(item, item2));
		when(inventory.findByArticleID(any())).thenReturn(items);
		when(principal.getName()).thenReturn("boss");
		when(item.getQuantity()).thenReturn(Quantity.of(10));
		when(item2.getQuantity()).thenReturn(Quantity.of(3));
		when(inventory.findStoredItem(any())).thenReturn(item);
		when(inventory.findSaleItem(any())).thenReturn(item2);

		String result = inventoryController.decreaseStock(productIdentifier, -11, principal);

		assertEquals("redirect:/inventory", result);

	}

	@Test
	void testDecreaseStockWhenNumberPositiveTest() {
		Principal principal = mock(Principal.class);
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);

		String result = inventoryController.decreaseStock(productIdentifier, 10, principal);

		assertEquals("redirect:/inventory", result);
	}

	@Test
	void testInventoryControllerConstructor() {
		ShopOrderManagement shopOrderManagement = mock(ShopOrderManagement.class);
		ShopAccountancyManagement accountancy = mock(ShopAccountancyManagement.class);
		ShopInventoryManagement inventory = mock(ShopInventoryManagement.class);

		InventoryController inventoryController = new InventoryController(shopOrderManagement, accountancy, inventory);

		assertNotNull(inventoryController);
	}

	@Test
	void registerNewItemAlreadyExistsTest(){
		ItemForm form = mock(ItemForm.class);

		when(inventory.createArticle(any())).thenReturn(false);

		String result = inventoryController.registerNew(form, model);

		assertEquals("redirect:/inventory-newItem", result);
	}

	@Test
	void registerNewTest(){
		ItemForm form = mock(ItemForm.class);

		when(inventory.createArticle(any())).thenReturn(true);

		String result = inventoryController.registerNew(form, model);

		assertEquals("redirect:/inventory", result);
	}

	@Test
	void displayDetailsTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		Article article = mock(Article.class);
		InventoryItem inventoryItem = mock(InventoryItem.class);
		InventoryItems inventoryItems = InventoryItems.of(Streamable.of(inventoryItem));

		when(inventory.findProduct(any())).thenReturn(article);
		when(inventory.findByArticleID(any())).thenReturn(inventoryItems);
		when(inventoryItem.getQuantity()).thenReturn(Quantity.of(10));


		String result = inventoryController.displayDetails(id,model);

		verify(model,times(4)).addAttribute(any(), any());
		assertEquals("Warehouse/detailsInventoryItem", result);
	}

	@Test
	void createItemFormTest(){

		String result = inventoryController.createItemForm();

		assertEquals("Warehouse/addInventoryItem", result);
	}

	@Test
	void moveItemsTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);

		String result = inventoryController.moveItems(id, model);

		verify(model, times(3)).addAttribute(any(), any());
		assertEquals("Warehouse/moveInventoryItems", result);
	}

	@Test
	void moveItemsSecondTest(){
		ShopInventoryItem item1 = mock(ShopInventoryItem.class);
		ShopInventoryItem item2 = mock(ShopInventoryItem.class);

		when(inventory.findByWarehouseID("newLocation")).thenReturn(item1);
		when(inventory.findByWarehouseID("location")).thenReturn(item2);

		String result = inventoryController.moveItems("newLocation", "location", 10);

		assertEquals("redirect:/inventory",result);
	}

	@Test
	void deleteArticleTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);

		Mockito.doNothing().when(inventory).deleteArticle(any());
		Mockito.doNothing().when(inventory).delete(any());

		String result = inventoryController.deleteArticle(id);

		assertEquals("redirect:/inventory", result);
	}

	@Test
	void editItemTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		Article article = mock(Article.class);

		when(inventory.findProduct(any())).thenReturn(article);
		when(article.getCategories()).thenReturn(Streamable.of("Hell"));


		String result = inventoryController.editItem("Henry", "Henry", "Henry", Float.valueOf(20),0.1 , id);

		verify(inventory, times(6)).saveArticle(any());
		assertEquals("redirect:/inventory", result);
	}

	@Test
	void editItemTestWithNull(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		Article article = mock(Article.class);

		when(inventory.findProduct(any())).thenReturn(article);
		when(article.getCategories()).thenReturn(Streamable.of("Hell"));


		String result = inventoryController.editItem("", "", "", null,null, id);

		verify(inventory, times(0)).saveArticle(any());
		assertEquals("redirect:/inventory", result);
	}


}
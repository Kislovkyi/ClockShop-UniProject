package clockshop.inventory;

import clockshop.catalog.Article;
import clockshop.catalog.Article.ArticleType;
import clockshop.catalog.ArticleCatalog;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.MultiInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ShopInventoryManagement}
 */
class ShopInventoryManagementTest {
    @Mock
    private MultiInventory<ShopInventoryItem> inventory;

    @Mock
    private ArticleCatalog articleCatalog;

    private ShopInventoryManagement shopInventoryManagement;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        shopInventoryManagement = new ShopInventoryManagement(inventory, articleCatalog);
    }
    @Test
    void testSave() {
        ShopInventoryItem item = mock(ShopInventoryItem.class);
        shopInventoryManagement.save(item);

        verify(inventory, times(1)).save(item);
    }

    @Test
    void testFindAll() {
        shopInventoryManagement.findAllInventoryItems();

        verify(inventory, times(1)).findAll();
    }


    @Test
    void testFindAllMaterials() {
        ShopInventoryItem mockedItem = mock(ShopInventoryItem.class);
        Article article = mock(Article.class);
		when(mockedItem.getArticle()).thenReturn(article);

		when(article.getCategories()).thenReturn(Streamable.of("MATERIAL"));
		when(inventory.findAll()).thenReturn(Streamable.of(mockedItem));



		Streamable<ShopInventoryItem> itemStreamable = Streamable.of(mockedItem);
        Streamable<ShopInventoryItem> result = shopInventoryManagement.findAllMaterials();


        assertTrue(result.toList().contains(mockedItem));
        verify(inventory, times(1)).findAll();
    }


    @Test
    void testCreateArticle() {
        ItemForm itemForm = mock(ItemForm.class);
        when(itemForm.getArticleName()).thenReturn("Test Article");
        when(itemForm.getPrice()).thenReturn(Money.of(1, "EUR"));
        when(itemForm.getType()).thenReturn(ArticleType.MATERIAL);
        when(itemForm.getLocationL()).thenReturn("L:0000");
		when(itemForm.getLocationV()).thenReturn("V:0000");
		when(articleCatalog.findByName(anyString())).thenReturn(Streamable.empty());

        boolean result = shopInventoryManagement.createArticle(itemForm);

        assertTrue(result);
        verify(articleCatalog, times(1)).save(any(Article.class));
        verify(inventory, times(2)).save(any(ShopInventoryItem.class));
    }

    @Test
    void testFindProduct() {
        Product.ProductIdentifier productId = Mockito.mock(Product.ProductIdentifier.class);
        when(articleCatalog.findById(productId)).thenReturn(Optional.of(mock(Article.class)));

        shopInventoryManagement.findProduct(productId);

        verify(articleCatalog, times(1)).findById(productId);
    }

    @Test
    void testFindByArticleID() {
        Product.ProductIdentifier id = Mockito.mock(Product.ProductIdentifier.class);
        InventoryItems<ShopInventoryItem> items = InventoryItems.of(Streamable.of(mock(ShopInventoryItem.class)));
		when(inventory.findByProductIdentifier(id)).thenReturn(items);

        shopInventoryManagement.findByArticleID(id);

        verify(inventory, times(1)).findByProductIdentifier(id);
    }

    @Test
    void testFindByItemId() {
        InventoryItem.InventoryItemIdentifier id = Mockito.mock(InventoryItem.InventoryItemIdentifier.class);
        when(inventory.findById(id)).thenReturn(Optional.of(mock(ShopInventoryItem.class)));

        shopInventoryManagement.findByItemId(id);

        verify(inventory, times(1)).findById(id);
    }

	@Test
	void findByProductNameIgnoreCaseTest(){
		ShopInventoryItem mockedItem = mock(ShopInventoryItem.class);
		Article article = mock(Article.class);
		when(articleCatalog.findByNameContainingIgnoreCase(any())).thenReturn(Streamable.of(article).toList());

		List<Article> result = shopInventoryManagement.findByNameContainingIgnoreCase("Henry");

		verify(articleCatalog,times(1)).findByNameContainingIgnoreCase(any());
	}

	@Test
	void getCatalogPageTest(){

		Article article = mock(Article.class);

		when(articleCatalog.findAll()).thenReturn(Streamable.of(article, article, article, article, article));

		Page<Article> result = shopInventoryManagement.getCatalogAndInventoryPage(1, 5);

		verify(articleCatalog,times(1)).findAll();
	}

	@Test
	void deleteItemTest(){
		ShopInventoryItem item = mock(ShopInventoryItem.class);

		Mockito.doNothing().when(inventory).delete(any());

		shopInventoryManagement.delete(item);

		verify(inventory,times(1)).delete(any());
	}

	@Test
	void deleteArticleTest(){
		Article article = mock(Article.class);

		Mockito.doNothing().when(articleCatalog).delete(any());

		shopInventoryManagement.deleteArticle(article);

		verify(articleCatalog,times(1)).delete(any());
	}

	@Test
	void findWarehouseIdsTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);

		when(shopInventoryItem.getWarehouseId()).thenReturn("Tim");

		InventoryItems<ShopInventoryItem> items = InventoryItems.of(Streamable.of(shopInventoryItem));

		when(shopInventoryManagement.findByArticleID(id)).thenReturn(items);

		List<String> result = shopInventoryManagement.findWarehouseIds(id);
		assertTrue(result.contains("Tim"));
	}

	@Test
	void findByWarehouseIDTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);

		when(shopInventoryItem.getWarehouseId()).thenReturn("Tim");
		when(inventory.findAll()).thenReturn(Streamable.of(shopInventoryItem));

		ShopInventoryItem result = shopInventoryManagement.findByWarehouseID("Tim");

		assertEquals(shopInventoryItem, result);

	}

	@Test
	void findSaleItemTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);
		InventoryItems<ShopInventoryItem> inventoryItems = InventoryItems.of(Streamable.of(shopInventoryItem));

		when(shopInventoryItem.getWarehouseId()).thenReturn("V");
		when(inventory.findByProductIdentifier(id)).thenReturn(inventoryItems);

		ShopInventoryItem result = shopInventoryManagement.findSaleItem(id);

		assertEquals(shopInventoryItem, result);
	}

	@Test
	void findSaleItemEmptyStreamTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);
		InventoryItems<ShopInventoryItem> inventoryItems = InventoryItems.of(Streamable.empty());

		when(shopInventoryItem.getWarehouseId()).thenReturn("V");
		when(inventory.findByProductIdentifier(id)).thenReturn(inventoryItems);

		ShopInventoryItem result = shopInventoryManagement.findSaleItem(id);

		assertEquals(null, result);
	}

	@Test
	void findStoredItemTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);
		InventoryItems<ShopInventoryItem> inventoryItems = InventoryItems.of(Streamable.of(shopInventoryItem));

		when(shopInventoryItem.getWarehouseId()).thenReturn("L");
		when(inventory.findByProductIdentifier(id)).thenReturn(inventoryItems);

		ShopInventoryItem result = shopInventoryManagement.findStoredItem(id);

		assertEquals(shopInventoryItem, result);
	}

	@Test
	void findStoredItemEmptyStreamTest(){
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		ShopInventoryItem shopInventoryItem = mock(ShopInventoryItem.class);
		InventoryItems<ShopInventoryItem> inventoryItems = InventoryItems.of(Streamable.empty());

		when(shopInventoryItem.getWarehouseId()).thenReturn("L");
		when(inventory.findByProductIdentifier(id)).thenReturn(inventoryItems);

		ShopInventoryItem result = shopInventoryManagement.findStoredItem(id);

		assertEquals(null, result);
	}

	@Test
	void createArticleTest(){
		Article article = mock(Article.class);
		ItemForm itemForm = new ItemForm("ada",20,"MATERIAL","asd","dd","sfsdf", 20);

		when(articleCatalog.findByName(any())).thenReturn(Streamable.empty());

		shopInventoryManagement.createArticle(itemForm);

		verify(inventory, times(2)).save(any());
	}

	@Test
	void createArticleAlreadyExistsTest(){
		Article article = mock(Article.class);

		ItemForm itemForm = new ItemForm("ada",20,"MATERIAL","asd","dd","sfsdf", 20);

		when(articleCatalog.findByName(any())).thenReturn(Streamable.of(article));

		shopInventoryManagement.createArticle(itemForm);

		verify(inventory, times(0)).save(any());
	}
}
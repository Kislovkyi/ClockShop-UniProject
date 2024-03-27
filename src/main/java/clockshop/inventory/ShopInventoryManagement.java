package clockshop.inventory;

import clockshop.catalog.Article;
import clockshop.catalog.Article.ArticleType;
import clockshop.catalog.ArticleCatalog;
import clockshop.grandfatherclock.GrandfatherClock;
import clockshop.extras.PageManager;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.inventory.InventoryItem.InventoryItemIdentifier;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.MultiInventory;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShopInventoryManagement implements PageManager<Article> {

	private final MultiInventory<ShopInventoryItem> inventory;
	private final ArticleCatalog articleCatalog;

	/**
	 * Constructs a new instance of ShopInventoryManagement with the provided inventory and article catalog.
	 *
	 * @param inventory The multi-inventory system for managing shop inventory items.
	 * @param articleCatalog The catalog containing articles to be managed by the shop.
	 */
	public ShopInventoryManagement(MultiInventory<ShopInventoryItem> inventory, ArticleCatalog articleCatalog) {

		this.articleCatalog = articleCatalog;
		this.inventory = inventory;

	}

	/**
	 * Saves {@link ShopInventoryItem} to inventory
	 *
	 * @param item ShopInventoryItem
	 */
	public void save(ShopInventoryItem item) {
		inventory.save(item);
	}


	/**
	 * Returns all {@link ShopInventoryItem} in inventory
	 *
	 * @return Streamable<ShopInventoryItem>
	 */
	public Streamable<ShopInventoryItem> findAllInventoryItems() {
		return inventory.findAll();
	}


	/**
	 * Returns {@link ShopInventoryItem} in inventory with the type ArticleType.MATERIAL
	 *
	 * @return Streamable<ShopInventoryItem>
	 */
	public Streamable<ShopInventoryItem> findAllMaterials() {
		return inventory.findAll().filter(item ->
			(item.getArticle()).getCategories().toList().contains
				(ArticleType.MATERIAL.toString()));
	}

	/**
	 * Creates a new Article in Catalog if Name doesn't already exist
	 *
	 * @param itemForm contains String articleName, int price, String type, String location
	 * @return boolean based on success
	 */
	public boolean createArticle(ItemForm itemForm) {
		if (articleCatalog.findByName(itemForm.getArticleName()).isEmpty()) {
			Article newArticle = new Article(itemForm.getArticleName(),
				itemForm.getPrice(),
				itemForm.getType(),
				itemForm.getDescription(),
				itemForm.getDiscount());
			articleCatalog.save(newArticle);
			ShopInventoryItem newItemV = new ShopInventoryItem(newArticle,
				Quantity.of(0),
				"V:" + itemForm.getLocationV());
			ShopInventoryItem newItemL = new ShopInventoryItem(newArticle,
				Quantity.of(0),
				"L:" + itemForm.getLocationL());
			inventory.save(newItemV);
			inventory.save(newItemL);
			return true;
		}
		return false;
	}

	/**
	 * Searches articleCatalog for Article with ProductIdentifier
	 *
	 * @param productId Product.ProductIdentifier
	 * @return Article
	 */
	public Article findProduct(ProductIdentifier productId) {
		Optional<Article> article = articleCatalog.findById(productId);
		return article.orElse(null);
	}

	/**
	 * Finds ShopInventoryItem in {@link UniqueInventory} based of {@link ProductIdentifier}
	 *
	 * @param id Product.ProductIdentifier of Article
	 * @return {@link ShopInventoryItem}
	 */
	public InventoryItems<ShopInventoryItem> findByArticleID(ProductIdentifier id) {
		InventoryItems<ShopInventoryItem> shopInventoryItems = inventory.findByProductIdentifier(id);
		return shopInventoryItems;
	}

	/**
	 * Finds ShopInventoryItem in {@link UniqueInventory} based of {@link InventoryItemIdentifier}
	 *
	 * @param id InventoryItemIdentifier of Item
	 * @return {@link ShopInventoryItem}
	 */
	public ShopInventoryItem findByItemId(InventoryItemIdentifier id) {
		Optional<ShopInventoryItem> inventoryItem = inventory.findById(id);
		return inventoryItem.orElse(null);
	}


	/**
	 * Returns Streamable<Article> with all Articles in articleCatalog
	 *
	 * @return {@link Streamable<Article>}
	 */
	public Streamable<Article> findAllArticles() {
		return articleCatalog.findAll();
	}

	/**
	 * Saves Article to articleCatalog
	 *
	 * @param article {@link Article}  to be saved
	 */
	public void saveArticle(Article article) {
		articleCatalog.save(article);
	}

	/**
	 * Accesses the method findByNameContainingIgnoreCase from {@link ArticleCatalog}
	 *
	 * @param name Text input for search
	 * @return {@link Streamable<Article>}
	 */
	public List<Article> findByNameContainingIgnoreCase(String name) {
		return articleCatalog.findByNameContainingIgnoreCase(name);
	}

	/**
	 * Calculates the Page for Catalog and Inventory based on Parameters
	 *
	 * @param page {@link Integer} Page Number
	 * @param size {@link Integer} Size of the Page
	 * @return {@link Page<Article>}
	 */
	public Page<Article> getCatalogAndInventoryPage(Integer page, Integer size) {

		Pageable pageRequest = PageRequest.of(page, size);

		List<Article> articleList = articleCatalog.findAll().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()), articleList.size());

		List<Article> pageContent = articleList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, articleList.size());
	}

	/**
	 * Deletes a given {@link ShopInventoryItem} from inventory
	 *
	 * @param inventoryItem {@link ShopInventoryItem}
	 */
	public void delete(ShopInventoryItem inventoryItem) {
		inventory.delete(inventoryItem);
	}

	/**
	 * Deletes a given {@link Article} from catalog
	 *
	 * @param article {@link Article}
	 */
	public void deleteArticle(Article article) {
		articleCatalog.delete(article);
	}


	/**
	 * Finds all WarehouseIds connected to a {@link ProductIdentifier}
	 *
	 * @param id {@link ProductIdentifier}
	 * @return {@link List<String>} warehouseIds
	 */
	public List<String> findWarehouseIds(ProductIdentifier id) {
		List<String> warehouseIdList = new ArrayList<>();
		var items = findByArticleID(id);
		for (ShopInventoryItem item : items) {
			warehouseIdList.add(item.getWarehouseId());
		}
		return warehouseIdList;
	}

	/**
	 * Finds a {@link ShopInventoryItem} based on WarehouseId
	 *
	 * @param warehouseID {@link String}
	 * @return {@link ShopInventoryItem}
	 */
	public ShopInventoryItem findByWarehouseID(String warehouseID) {
		for (ShopInventoryItem item : inventory.findAll()) {
			if (item.getWarehouseId().equals(warehouseID)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Finds the SalesItem for a  given {@link ProductIdentifier}
	 *
	 * @param articleID {@link ProductIdentifier}
	 * @return {@link ShopInventoryItem}
	 */
	public ShopInventoryItem findSaleItem(ProductIdentifier articleID) {
		for (ShopInventoryItem item : inventory.findByProductIdentifier(articleID)) {
			if (item.getWarehouseId().contains("V")) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Finds the StoredItem for a  given {@link ProductIdentifier}
	 *
	 * @param articleID {@link ProductIdentifier}
	 * @return {@link ShopInventoryItem}
	 */
	public ShopInventoryItem findStoredItem(ProductIdentifier articleID) {
		for (ShopInventoryItem item : inventory.findByProductIdentifier(articleID)) {
			if (item.getWarehouseId().contains("L")) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @param searchTerm searches for same name in all Articles except GFC category type
	 * @return List of {@link Article}
	 */
	@Override
	public List<Article> search(String searchTerm) {

		List<Article> list = articleCatalog.findByNameContainingIgnoreCase(searchTerm);
		List<Article> filteredList = new ArrayList<>();
		list.forEach(article -> {if(!(article instanceof GrandfatherClock)){
		filteredList.add(article);
		}
		});
		return filteredList;
	}

	/**
	 * @return all articles except GFC Category Type
	 */
	@Override
	public Streamable<Article> findAll() {
		List<Article> list = articleCatalog.findAll().stream().toList();
		List<Article> filteredList = new ArrayList<>();
		list.forEach(article -> {if(!(article instanceof GrandfatherClock)){
			filteredList.add(article);
		}
		});

		return Streamable.of(filteredList);
	}
}

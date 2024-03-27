package clockshop.inventory;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.catalog.Article;
import clockshop.order.ShopOrderManagement;
import clockshop.extras.PageData;
import clockshop.extras.PageService;
import jakarta.validation.Valid;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.Currencies;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;


@Controller
class InventoryController {

	private final ShopOrderManagement shopOrderManagement;

	private final ShopAccountancyManagement accountancy;

	private final ShopInventoryManagement inventory;


	private static final String INVENTORY_ATTRIBUTE = "inventory";

	private static final String ARTICLE_ATTRIBUTE = "articles";
	private static final String INVENTORY_TEMPLATE = "Warehouse/inventory";

	/**
	 * Constructs a new instance of InventoryController with the specified dependencies.
	 *
	 * @param shopOrderManagement  The management component for handling shop orders.
	 * @param accountancy          The management component for shop accountancy.
	 * @param inventory            The management component for shop inventory.
	 */
	InventoryController(ShopOrderManagement shopOrderManagement,
						ShopAccountancyManagement accountancy,
						ShopInventoryManagement inventory) {
		this.shopOrderManagement = shopOrderManagement;
		this.accountancy = accountancy;
		this.inventory = inventory;
	}


	/**
	 * Displays all {@link ShopInventoryItem}s in the system
	 *
	 * @param model will never be {@literal null}.
	 * @return the view name.
	 */
	@GetMapping(value = "/inventory")
	@Secured({"BOSS", "WATCH", "SALE"})
	String inventory(Model model, @RequestParam(required = false) Integer size,
					 @RequestParam(required = false) Integer page,
					 @RequestParam(required = false) String searchTerm) {

		PageData<Article> pageData = PageService.calculatePageData(size,page,searchTerm,inventory);


		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());
		model.addAttribute(INVENTORY_ATTRIBUTE, inventory);

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute(ARTICLE_ATTRIBUTE, inventory.getCatalogAndInventoryPage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute(ARTICLE_ATTRIBUTE, new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}
		return INVENTORY_TEMPLATE;
	}

	/**
	 * Handels the edit item from submit
	 *
	 * @param articleId {@link Product.ProductIdentifier}
	 * @param number    int containing the number-change
	 * @return the template name.
	 */
	@PostMapping(value = "/inventory/edit")
	@Secured({"BOSS"})
	String decreaseStock(
						 @RequestParam("articleId") Product.ProductIdentifier articleId,
						 @RequestParam("quantity") int number, Principal principal) {
		if (-number > 0) {
			InventoryItems<ShopInventoryItem> items = inventory.findByArticleID(articleId);
			int remainingQuantity;
			ShopInventoryItem storedItem = inventory.findStoredItem(articleId);
			ShopInventoryItem saleItem = inventory.findSaleItem(articleId);
			if (items.getTotalQuantity().getAmount().intValue() >= -number) {
				if (storedItem.getQuantity().getAmount().intValue() < -number) {
					remainingQuantity = -number - storedItem.getQuantity().getAmount().intValue();
					ShopInventoryItem item = (ShopInventoryItem) storedItem.decreaseQuantity(storedItem.getQuantity());
					inventory.save(item);
					ShopInventoryItem item2 = (ShopInventoryItem) saleItem.decreaseQuantity(Quantity.of(remainingQuantity));
					inventory.save(item2);
				} else {
					ShopInventoryItem item = (ShopInventoryItem) storedItem.decreaseQuantity(Quantity.of(-number));
					inventory.save(item);
				}
				accountancy.sortOutItem(Money.of(0, Currencies.EURO),
					"Sort-out",
					inventory.findProduct(articleId),
					Quantity.of(number));
			}
		} else {
			shopOrderManagement.orderMoreItems(inventory.findProduct(articleId), -number, principal);
		}
		return "redirect:/" + INVENTORY_ATTRIBUTE;
	}

	/**
	 * Handels {@link ShopInventoryItem} creation
	 *
	 * @param form  contains data for creation
	 * @param model will never be {@literal null}.
	 * @return the template name.
	 */
	@PostMapping("/inventory")
	String registerNew(@Valid ItemForm form, Model model) {
		if(!inventory.createArticle(form)){
			return "redirect:/inventory-newItem";
		}

		return "redirect:/" + INVENTORY_ATTRIBUTE;
	}


	/**
	 * Displays the Site with createItemForm
	 *
	 * @return the template name.
	 */
	@GetMapping("/inventory-newItem")
	String createItemForm() {
		return "Warehouse/addInventoryItem";
	}


	/**
	 * Display the details of selected Product
	 * @param id Product.ProductIdentifier
	 * @param model will never be {@literal null}.
	 * @return the template name.
	 */
	@PostMapping("inventory-details")
	public String displayDetails(@RequestParam("articleId") Product.ProductIdentifier id, Model model) {

		model.addAttribute("article", inventory.findProduct(id));
		model.addAttribute("total", inventory.findByArticleID(id).getTotalQuantity());
		model.addAttribute("locationObjects", inventory.findByArticleID(id));
		model.addAttribute("locations", inventory.findWarehouseIds(id));

		return "Warehouse/detailsInventoryItem";
	}


	/**
	 * Edit the given Product
	 * @param name {@link String} not required
	 * @param type {@link String} not required
	 * @param des {@link String} not required
	 * @param price {@link Float} not required
	 * @param price {@link Double} not required
	 * @param id {@link Product.ProductIdentifier}
	 * @return "redirect:/inventory"
	 */
	@PostMapping("/editItem")
	public String editItem(@RequestParam(value = "name", required = false) String name,
						   @RequestParam(value = "type", required = false) String type,
						   @RequestParam(value = "des", required = false) String des,
						   @RequestParam(value = "price", required = false) Float price,
						   @RequestParam(value = "discount", required = false) Double discount,
						   @RequestParam("id") Product.ProductIdentifier id) {

		Article article = inventory.findProduct(id);

		if (!name.isEmpty()) {
			article.setName(name);
			inventory.saveArticle(article);
		}
		if (!type.isEmpty()) {
			article.removeCategory(article.getCategories().stream().toList().get(0));
			article.addCategory(type);
			inventory.saveArticle(article);
		}
		if (!des.isEmpty()) {
			article.setDescription(des);
			inventory.saveArticle(article);
		}
		if (price != null) {
			article.setPrice(Money.of(price, "EUR"));
			inventory.saveArticle(article);
		}
		if (price != null) {
			article.setPrice(Money.of(price, "EUR"));
			inventory.saveArticle(article);
		}
		if (discount != null) {
			article.setDiscount(discount);
			inventory.saveArticle(article);
		}


		return "redirect:/" + INVENTORY_ATTRIBUTE;
	}

	/**
	 * Displays the site to move item quantities between warehouses
	 * @param articleId {@link Product.ProductIdentifier}
	 * @param model will never be {@literal null}.
	 * @return the template name.
	 */
	@GetMapping("/moveItems")
	public String moveItems(@RequestParam("articleId")Product.ProductIdentifier articleId, Model model){
		model.addAttribute("article", inventory.findProduct(articleId));
		model.addAttribute("locationObjects", inventory.findByArticleID(articleId));
		model.addAttribute("locations", inventory.findWarehouseIds(articleId));
		return "Warehouse/moveInventoryItems";
	}

	/**
	 * move item quantities between warehouses
	 * @param newLocation {@link String}
	 * @param location {@link String}
	 * @param quantityChange {@link Integer}
	 * @return "redirect:/inventory"
	 */
	@PostMapping("/moveItems")
	public String moveItems(@RequestParam("loc") String newLocation ,
							@RequestParam("warehouseId") String location,
							@RequestParam("quantityChange") int quantityChange){
		ShopInventoryItem oldItem = inventory.findByWarehouseID(location);
		ShopInventoryItem newItem = inventory.findByWarehouseID(newLocation);

		oldItem.decreaseQuantity(Quantity.of(quantityChange));
		inventory.save(oldItem);

		newItem.increaseQuantity(Quantity.of(quantityChange));
		inventory.save(newItem);

		return "redirect:/" + INVENTORY_ATTRIBUTE;
	}

	/**
	 * Deletes given {@link Article} from {@link ShopInventoryManagement}
	 * @param articleId {@link Product.ProductIdentifier}
	 * @return "redirect:/inventory"
	 */
	@GetMapping("/delete-article")
	public String deleteArticle(@RequestParam("articleId") Product.ProductIdentifier articleId){
		inventory.delete(inventory.findSaleItem(articleId));
		inventory.delete(inventory.findStoredItem(articleId));
		inventory.deleteArticle(inventory.findProduct(articleId));
		return "redirect:/" + INVENTORY_ATTRIBUTE;
	}
}
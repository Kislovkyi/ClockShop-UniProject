package clockshop.catalog;

import clockshop.catalog.Article.ArticleType;
import clockshop.inventory.ShopInventoryManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Data initializer class responsible for creating example catalog entries and saving them
 * using the provided ShopInventoryManagement service.
 */
@Component
class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private final ShopInventoryManagement shopInventoryManagement;

	CatalogDataInitializer(ShopInventoryManagement shopInventoryManagement) {

		this.shopInventoryManagement = shopInventoryManagement;
	}

	/**
	 * Initializes the catalog with example entries by saving them using the ShopInventoryManagement service.
	 */
	@Override
	public void initialize() {
		LOG.info("Creating example catalog entries.");

		shopInventoryManagement.saveArticle(
			new Article("Sanduhr",
				Money.of(10000, EURO),
				ArticleType.CLOCK,
				"Uhr aus Sand", 0.1) );

		shopInventoryManagement.saveArticle(
			new Article("Smartwatch",
				Money.of(300, EURO),
				ArticleType.CLOCK,
				"Schlau", 0.1 ));

		shopInventoryManagement.saveArticle(
			new Article("Armband",
				Money.of(50, EURO),
				ArticleType.MATERIAL,
				"Band ohne Arm", 0.1) );

		shopInventoryManagement.saveArticle(
			new Article("Gold-Necklace",
				Money.of(1000, EURO),
				ArticleType.ACCESSORY,
				"Goldig", 0.1) );

		shopInventoryManagement.saveArticle(
			new Article("Glock-18",
				Money.of(200, EURO),
				ArticleType.ACCESSORY,
				"Standard T-side weapon", 0.1));
	}

}

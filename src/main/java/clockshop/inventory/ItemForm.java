package clockshop.inventory;

import clockshop.catalog.Article;
import org.javamoney.moneta.Money;

import static org.salespointframework.core.Currencies.EURO;

/**
 * Form containing all Data used for creating a new Item. This class is used to simplify taking data from the html form.
 */
public class ItemForm {

	private final String articleName;
	private final String locationV;

	private final String locationL;
	private final Money price;
	private final Article.ArticleType type;

	private final String description;

	private final double discount;

	public ItemForm(String articleName,
					int price,
					String type,
					String locationL,
					String locationV,
					String description,
					double discount) {
		this.articleName = articleName;
		this.price = Money.of(price, EURO);
		this.type = Article.ArticleType.valueOf(type);
		this.locationL = locationL;
		this.locationV = locationV;
        this.description = description;
		this.discount = discount;
    }

	public String getArticleName() {
		return articleName;
	}

	public Money getPrice() {
		return price;
	}

	public Article.ArticleType getType() {
		return type;
	}

	public String getLocationL() {
		return locationL;
	}

	public String getLocationV() {
		return locationV;
	}

	public String getDescription() {
		return description;
	}

	public double getDiscount() {
		return discount;
	}
}

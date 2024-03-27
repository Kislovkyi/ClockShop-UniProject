package clockshop.catalog;

import jakarta.persistence.Entity;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

@Entity
public class Article extends Product {

	public enum ArticleType {
		CLOCK, MATERIAL, ACCESSORY,GFC
	}

	private ArticleType type;
	private String description;

	private double discount;

	public Article(String name, Money price, ArticleType type, String description, double discount) {
		super(name, price);
        this.description = description;
		this.discount = discount;
        addCategory(type.toString());

	}

	@SuppressWarnings({"unused", "deprecation"})
	public Article() {
		description = null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
}

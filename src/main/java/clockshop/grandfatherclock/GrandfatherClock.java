package clockshop.grandfatherclock;

import clockshop.catalog.Article;
import jakarta.persistence.Entity;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

/**
 * Represents a Grandfather Clock entity, extending the {@link Product} class.
 */
@Entity
public class GrandfatherClock extends Article {

	private String companyName;

	/**
	 * Constructs a new instance of GrandfatherClock with the specified attributes.
	 *
	 * @param name        The name of the Grandfather Clock.
	 * @param price       The price of the Grandfather Clock.
	 * @param companyName The name of the company associated with the Grandfather Clock.
	 * @param description A description of the Grandfather Clock.
	 * @param discount    The discount applied to the Grandfather Clock.
	 */
	GrandfatherClock(String name, Money price, String companyName, String description, double discount) {
		super(name, price,ArticleType.GFC,description,discount);
		this.companyName = companyName;
	}

	@SuppressWarnings({"unused", "deprecation"})
	public GrandfatherClock() {
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}

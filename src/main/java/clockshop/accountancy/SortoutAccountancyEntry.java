package clockshop.accountancy;

import jakarta.persistence.Entity;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;

/**
 * AccountancyEntry for sorting out items
 */
@Entity
public class SortoutAccountancyEntry extends AccountancyEntry {

	private final Product.ProductIdentifier productId;

	private final Quantity quantity;

	/**
	 * Constructs a new SortoutAccountancyEntry with the specified monetary amount, description, product identifier,
	 * and quantity.
	 *
	 * @param amount The monetary amount associated with the sortout accountancy entry.
	 * @param description The description providing additional information about the sortout entry.
	 * @param productId The unique identifier of the product associated with the sortout entry.
	 * @param quantity The quantity of the product associated with the sortout entry.
	 */
	public SortoutAccountancyEntry(MonetaryAmount amount,
								   String description,
								   Product.ProductIdentifier productId,
								   Quantity quantity) {
		super(amount, description);
		this.productId = productId;
		this.quantity = quantity;

	}

	@SuppressWarnings({"unused", "deprecation"})
	public SortoutAccountancyEntry() {
		this.productId = null;
		this.quantity = null;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public Product.ProductIdentifier getProductId() {
		return productId;
	}

	public Order.OrderIdentifier getOrderIdentifier(){
		return null;
	}
}

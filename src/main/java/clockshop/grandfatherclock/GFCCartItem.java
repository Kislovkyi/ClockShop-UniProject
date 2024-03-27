package clockshop.grandfatherclock;

import jakarta.persistence.Embeddable;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;



public class GFCCartItem {
	private String name;
	private Money price;
	private String companyName;
	private Quantity quantity;

	private StatusType statusType;

	private Product.ProductIdentifier id;

	private Order.OrderIdentifier orderId;

	public GFCCartItem(String name, Money price, String companyName, Quantity quantity, Product.ProductIdentifier id) {
		this.name = name;
		this.price = price;
		this.companyName = companyName;
		this.quantity = quantity;
		this.id = id;
		statusType = StatusType.SELECTED;
		orderId = null;
	}

	GFCCartItem() {}

	public void setOrderId(Order.OrderIdentifier orderId) {
		this.orderId = orderId;
	}

	public Order.OrderIdentifier getOrderId() {
		return orderId;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public void setStatusType(StatusType statusType) {
		this.statusType = statusType;
	}

	public Product.ProductIdentifier getId() {
		return id;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setId(Product.ProductIdentifier id) {
		this.id = id;
	}
}

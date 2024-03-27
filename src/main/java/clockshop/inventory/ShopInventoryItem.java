package clockshop.inventory;

import clockshop.catalog.Article;
import clockshop.repair.Repair;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.MultiInventoryItem;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;

import java.util.Objects;

import static org.salespointframework.core.Currencies.EURO;

/**
 * {@link UniqueInventoryItem} with warehouseId and nonSeller
 */
@Entity
public class ShopInventoryItem extends MultiInventoryItem {

	private String warehouseId;
	private boolean nonSeller;

	/**
	 * Constructs a new instance of ShopInventoryItem with the specified product, quantity, and warehouse ID.
	 *
	 * @param product      The product associated with the inventory item.
	 * @param quantity     The quantity of the product in the inventory item.
	 * @param warehouseId  The identifier of the warehouse where the inventory item is stored.
	 */
	public ShopInventoryItem(Product product, Quantity quantity, String warehouseId) {
		super(product, quantity);
		this.warehouseId = warehouseId;
		nonSeller = false;
	}


	public ShopInventoryItem() {
		super(new Product("0", Money.of(10000, EURO)), Quantity.of(1));
		warehouseId = "0";
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param obj The reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ShopInventoryItem inventoryItem = (ShopInventoryItem) obj;
		return
			Objects.equals(getArticle(), inventoryItem.getArticle()) &&
				Objects.equals(getQuantity(), inventoryItem.getQuantity()) &&
				Objects.equals(getWarehouseId(), inventoryItem.getWarehouseId()) &&
				Objects.equals(getNonSeller(), inventoryItem.getNonSeller());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getArticle(),getQuantity(),warehouseId,nonSeller);
	}


	public String getWarehouseId() {

		return warehouseId;
	}

	public boolean getNonSeller() {
		return nonSeller;
	}

	public void setNonSeller(boolean value) {
		nonSeller = value;
	}


	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}


	/**
	 * Returns connected {@link Article}
	 *
	 * @return {@link Article}
	 */
	@OneToOne
	public Article getArticle() {
		if (super.getProduct() instanceof Article) {
			return (Article) getProduct();
		} else {
			return null;
		}
	}
}

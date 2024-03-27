package clockshop.inventory;

import clockshop.grandfatherclock.GrandfatherClock;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Component
class InventoryInitializer implements DataInitializer {

	private final ShopInventoryManagement shopInventoryManagement;

	InventoryInitializer(ShopInventoryManagement shopInventoryManagement) {
		this.shopInventoryManagement = shopInventoryManagement;

	}

	/**
	 * initializes Inventory
	 */
	@Override
	public void initialize() {
		AtomicInteger i = new AtomicInteger();
		shopInventoryManagement.findAllArticles().forEach(article -> {
			ShopInventoryItem itemV = new ShopInventoryItem(
				article,
				Quantity.of(ThreadLocalRandom.current().nextInt(1, 100)),
				"V" + ":" + i);
			shopInventoryManagement.save(itemV);
			ShopInventoryItem itemL = new ShopInventoryItem(
				article,
				Quantity.of(ThreadLocalRandom.current().nextInt(1, 100)),
				"L" + ":" + i);
			shopInventoryManagement.save(itemL);
			i.getAndIncrement();
			}
		);
	}
}
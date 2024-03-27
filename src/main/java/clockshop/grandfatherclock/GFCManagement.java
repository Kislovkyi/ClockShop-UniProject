package clockshop.grandfatherclock;

import clockshop.extras.PageManager;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.Currencies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Optional;

/**
 * Manages the operations related to Grandfather Clocks (GFCs) in the application.
 */
@Component
public class GFCManagement implements PageManager<GrandfatherClock> {
	private final GFCCatalog gfcCatalog;

	/**
	 * Constructs a new instance of GFCManagement with the specified GFCCatalog.
	 *
	 * @param gfcCatalog The catalog responsible for managing Grandfather Clocks (GFC).
	 */
	public GFCManagement(GFCCatalog gfcCatalog){
		this.gfcCatalog = gfcCatalog;
	}

	/**
	 * Calculates the total price for a list of Grandfather Clock items in the shopping cart.
	 *
	 * @param cartItems The list of Grandfather Clock items in the shopping cart.
	 * @return The total price as a MonetaryAmount.
	 */
	public static MonetaryAmount calculateTotalPrice(List<GFCCartItem> cartItems) {
		MonetaryAmount totalPrice = Money.of(0, Currencies.EURO);
		for (GFCCartItem cartItem : cartItems) {
			totalPrice = totalPrice.add(cartItem.getPrice().multiply(cartItem.getQuantity().getAmount()));
		}
		return totalPrice;
	}

	/**
	 * Adds a new Grandfather Clock to the catalog.
	 *
	 * @param name The name of the Grandfather Clock.
	 * @param price The price of the Grandfather Clock in euros.
	 * @param companyName The name of the company producing the Grandfather Clock.
	 * @param description A description of the Grandfather Clock.
	 * @param discount The discount applied to the Grandfather Clock.
	 */
	void addClock(String name, int price, String companyName, String description, double discount) {
		GrandfatherClock newClock = new GrandfatherClock(name,
			Money.of(price, "EUR"),
			companyName,
			description,
			discount);
		gfcCatalog.save(newClock);
	}

	/**
	 * Retrieves a stream of all Grandfather Clocks in the catalog.
	 *
	 * @return A Streamable containing all Grandfather Clocks in the catalog.
	 */
	public Streamable<GrandfatherClock> findAll(){

		return gfcCatalog.findAll();
	}

	/**
	 * Searches {@link GFCManagement} for GrandfatherClocks matching the searchTerm
	 * OldName: searchGFCByName
	 * @param searchTerm {@link String}
	 * @return {@link List<GrandfatherClock>}
	 */
	public List<GrandfatherClock> search(String searchTerm) {
		return gfcCatalog.findByNameContainingIgnoreCase(searchTerm).stream().toList();
	}

	/**
	 * @param page number of page
	 * @param size length of page
	 * @return template
	 */
	public Page<GrandfatherClock> getGFCPage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<GrandfatherClock> employeeList = gfcCatalog.findAll().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<GrandfatherClock> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	/**
	 * Retrieves a Grandfather Clock from the catalog based on its product identifier.
	 *
	 * @param id The product identifier of the Grandfather Clock.
	 * @return An Optional containing the Grandfather Clock if found, or an empty Optional if not found.
	 */
	public Optional<GrandfatherClock> findById(Product.ProductIdentifier id) {
		return gfcCatalog.findById(id);
	}

	/**
	 * Checks if a Grandfather Clock with the specified name exists in the catalog.
	 *
	 * @param name The name of the Grandfather Clock to check.
	 * @return true if a Grandfather Clock with the given name exists, false otherwise.
	 */
	public boolean GFCNameExists(String name){
		return gfcCatalog.existsByNameIgnoreCase(name);
	}

	/**
	 * Deletes a Grandfather Clock from the catalog based on its product identifier.
	 *
	 * @param id The product identifier of the Grandfather Clock to be deleted.
	 */
	public void deleteGFCById(Product.ProductIdentifier id) {

		gfcCatalog.deleteById(id);
	}

	/**
	 * Updates or saves a Grandfather Clock in the catalog.
	 *
	 * @param grandfatherClock The Grandfather Clock to be updated or saved.
	 */
	public void updateGFC(GrandfatherClock grandfatherClock) {
		gfcCatalog.save(grandfatherClock);
	}

}

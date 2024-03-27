package clockshop.accountancy;


import jakarta.persistence.Entity;
import org.salespointframework.accountancy.AccountancyEntry;

import javax.money.MonetaryAmount;


/**
 * AccountancyEntry that has no specific use but can easily be extended for custom use-cases
 */
@Entity
public class CustomAccountancyEntry extends AccountancyEntry {


	/**
	 * Constructs a new CustomAccountancyEntry with the specified monetary amount and description.
	 *
	 * @param amount The monetary amount associated with the accountancy entry.
	 * @param description The description providing additional information about the entry.
	 */
	public CustomAccountancyEntry(MonetaryAmount amount, String description) {
		super(amount, description);
	}

	@SuppressWarnings({"unused", "deprecation"})
	public CustomAccountancyEntry() {

	}
}



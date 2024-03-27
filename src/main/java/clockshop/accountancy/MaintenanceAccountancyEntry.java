package clockshop.accountancy;

import clockshop.maintenance.MaintenanceContract;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.order.Order;

import javax.money.MonetaryAmount;

@Entity
public class MaintenanceAccountancyEntry extends AccountancyEntry {

	private final String company;
	private final String contactPerson;
	private final String address;

	/**
	 * Constructs a new MaintenanceAccountancyEntry with the specified monetary amount, description, company,
	 * contact person, and address.
	 *
	 * @param amount The monetary amount associated with the maintenance accountancy entry.
	 * @param description The description providing additional information about the maintenance entry.
	 * @param company The company associated with the maintenance entry.
	 * @param contactPerson The contact person associated with the maintenance entry.
	 * @param address The address associated with the maintenance entry.
	 */
	public MaintenanceAccountancyEntry(MonetaryAmount amount,
									   String description,
									   String company,
									   String contactPerson,
									   String address) {
		super(amount, description);
        this.company = company;
        this.contactPerson = contactPerson;
        this.address = address;
    }

	@SuppressWarnings({"unused", "deprecation"})
	public MaintenanceAccountancyEntry() {
        this.company = null;
        this.contactPerson = null;
        this.address = null;
    }

	public String getCompany() {
		return company;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public String getAddress() {
		return address;
	}

	public Order.OrderIdentifier getOrderIdentifier(){
		return null;
	}
}

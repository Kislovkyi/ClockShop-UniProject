package clockshop.maintenance;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.javamoney.moneta.Money;
import org.jmolecules.ddd.types.Identifier;
import org.salespointframework.time.BusinessTime;

import java.io.Serializable;
import java.util.UUID;

@Entity
public class MaintenanceContract {
	@Id
	private final UUID id = new ContractIdentifier().identifier;
	private String company;
	private Integer towerQuantity;
	private Integer buildingQuantity;
	private transient BusinessTime businessTime;
	private Money price;
	private String address;
	private String contactPerson;


	public MaintenanceContract(String company,
							   Integer towerQuantity,
							   Integer buildingQuantity,
							   BusinessTime businessTime,
							   Money price,
							   String contactPerson,
							   String address) {
		this.company = company;
		this.towerQuantity = towerQuantity;
		this.buildingQuantity = buildingQuantity;
		this.businessTime = businessTime;
		this.price = price;
		this.contactPerson = contactPerson;
		this.address = address;
	}

	public MaintenanceContract() {
	}

	public String getCompany() {
		return company;
	}

	public Integer getTowerQuantity() {
		return towerQuantity;
	}

	public Integer getBuildingQuantity() {
		return buildingQuantity;
	}

	public Money getPrice() {
		return this.price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public UUID getId() {
		return this.id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public static final class ContractIdentifier implements Identifier, Serializable {

		private final UUID identifier;

		public ContractIdentifier() {
			this(UUID.randomUUID());
		}

		ContractIdentifier(UUID identifier) {
			this.identifier = identifier;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + (identifier == null ? 0 : identifier.hashCode());

			return result;
		}
	}

}

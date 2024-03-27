package clockshop.repair;

import jakarta.persistence.Entity;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;
import org.springframework.util.Assert;
import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Objects;
/**
 * Entity class representing a repair in the ClockShop. It extends the Product class.
 * Manages information such as email, customer name, dates, repair type, and status.
 */
@Entity
public class Repair extends Product implements Serializable  {

    private String customerAddress;
    private String telephoneNumber;
	private String description;
    private ProductIdentifier id;
	private String email;

	private String customerForename;
	private String customerName;
	private LocalDateTime date;
	private LocalDateTime endDate;
	private RepairType repairType;
	private int costEstimate;
	private transient BusinessTime businessTime;
	private Order.OrderIdentifier orderIdentifier;


	/**
	 * Default constructor for the Repair class.
	 *
	 * This no-argument constructor is required by JPA for instantiating objects 
	 * during retrieval from the database.
	 */
	@SuppressWarnings({"unused", "deprecation"})
	public Repair() {}


	/**
	 * Constructor for the Repair class.
	 *
	 * @param email The email of the customer.
	 * @param price The price of the repair.
	 * @param repairType The type of the repair.
	 * @param businessTime The business time.
	 * @param customerName The name of the customer.
	 * @param customerAddress The address of the customer.
	 * @param telephoneNumber The telephone number of the customer.
	 * @param description The description of the repair.
	 */
	public Repair(String email,
				  MonetaryAmount price,
				  RepairType repairType,
				  BusinessTime businessTime,
				  String customerForename,
				  String customerName,
				  String customerAddress,
				  String telephoneNumber,
				  String description) {
		
		super(email, price);
        Assert.notNull(email, "Email must not be null");
		this.email = email;
		Assert.notNull(customerName, "Customer Name must not be null");
		this.customerForename = customerForename;
		this.customerName = customerName;
		this.repairType = repairType;
		this.businessTime = businessTime;
		this.date = this.businessTime.getTime();
		this.endDate = null;
		this.costEstimate = repairType.getCost();
		this.orderIdentifier = null;
		this.customerAddress = customerAddress;
		this.telephoneNumber = telephoneNumber;
		this.description = description;

	}


	/**
	 * Returns the cost estimate for the repair.
	 *
	 * @return The cost estimate.
	 */
	public int getCostEstimate() {
		return costEstimate;
	}
	
	
	/**
	 * Sets the cost estimate for the repair.
	 *
	 * @param costEstimate The cost estimate.
	 */
	public void setCostEstimate(int costEstimate) {
		this.costEstimate = costEstimate;
	}

	/**
	 * Returns the email associated with the repair.
	 *
	 * @return The email.
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * set the {@link RepairType}
	 * @param repairType {@link RepairType}
	 */
	public void setRepairType(RepairType repairType) {
		this.repairType = repairType;
	}

	/**
	 * Sets the email for the repair.
	 *
	 * @param email The email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * Sets the customer name for the repair.
	 *
	 * @param customerName The customer name.
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * Returns the type of the repair.
	 *
	 * @return The repair type.
	 */
	public RepairType getRepairType() {
		return repairType;
	}


	/**
	 * Returns the start date of the repair.
	 *
	 * @return The start date.
	 */
	public LocalDateTime getDate() {
		return date;
	}

	/**
	 * Gets the formatted date and time as a string.
	 *
	 * @param date The date to be formatted.
	 * @return The formatted date and time string.
	 */
	public String getFormattedDateTime(LocalDateTime date){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		return date.format(formatter);
	}


	/**
	 * Returns the end date of the repair.
	 *
	 * @return The end date.
	 */
	public LocalDateTime getEndDate() {
		return endDate;
	}

	/**
	 * Gets the duration of the repair in minutes.
	 *
	 * @return The duration in minutes.
	 */
	public long getDurationInMinutes(){
		if (endDate != null && date != null) {
			if (Duration.between(date, endDate).toMinutes() < 1) {
				return Duration.between(date, endDate).toMinutes() + 1;
			}
			return Duration.between(date, endDate).toMinutes();
		}
		return 0;
	}


	/**
	 * Marks the repair as finished by setting the end date to the current business time.
	 *
	 * @param businessTime The current business time.
	 */
	public void finish(BusinessTime businessTime) {
		this.endDate = businessTime.getTime();
	}


	/**
	 * Checks if the repair is finished.
	 *
	 * @return True if the repair is finished, false otherwise.
	 */
	public boolean isFinished() {
		return this.endDate != null;
	}

	/**
	 * Checks if the repair type is RADIO.
	 *
	 * @return True if the repair type is radio, false otherwise.
	 */
	public boolean isRadio() {
        return repairType.equals(RepairType.RADIO);
	}


	/**
	 * Returns the customer's forename associated with the repair.
	 * @return The customer's forename.
	 */
	public String getCustomerForename() {
		return customerForename;
	}

	/**
	 * Returns the customer's name associated with the repair.
	 *
	 * @return The customer's name.
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Gets the status of the repair: In Progress or Finished.
	 *
	 * @return The status.
	 */
	public String getStatus(){
		if (isFinished()) {
			return "Finished";
		}
		return "In Progress";
	}

	/**
	 * Overrides the getId method to return the custom identifier.
	 *
	 * @return The custom identifier of the repair.
	 */
	@Override
	public ProductIdentifier getId() {
		if (id == null) {
			id = super.getId();
		}
		return id;
	}

	/**
	 * Overrides the equals method to compare repairs based on their attributes.
	 *
	 * @param o The object to compare with.
	 * @return True if the repairs are equal, false otherwise.
	 */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Repair repair = (Repair) o;
		return
			Objects.equals(email, repair.email) &&
				Objects.equals(date, repair.date) &&
				Objects.equals(endDate, repair.endDate) &&
				repairType == repair.repairType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, date, endDate, repairType);
	}

	/**
	 * Returns the order identifier associated with the repair.
	 *
	 * @return The order identifier.
	 */
	public Order.OrderIdentifier getOrderIdentifier() {
		return orderIdentifier;
	}

	/**
	 * Sets the order identifier associated with the repair.
	 *
	 * @param orderIdentifier The order identifier.
	 */
	public void setOrderIdentifier(Order.OrderIdentifier orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}


	/**
	 * Returns the customer's address associated with the repair.
	 *
	 * @return The customer's address.
	 */
	public String getCustomerAddress() {
		return customerAddress;
	}

	/**
	 * Sets the customer's address for the repair.
	 *
	 * @param customerAddress The customer's address.
	 */
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}


	/**
	 * Returns the customer's telephone number associated with the repair.
	 *
	 * @return The customer's telephone number.
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}


	/**
	 * Sets the customer's telephone number for the repair.
	 *
	 * @param telephoneNumber The customer's telephone number.
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}


	/**
	 * Returns the description associated with the repair.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Sets the description for the repair.
	 *
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}

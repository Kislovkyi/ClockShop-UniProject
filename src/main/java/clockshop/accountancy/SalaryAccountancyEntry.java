package clockshop.accountancy;

import clockshop.staff.Employee;
import jakarta.persistence.Entity;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.order.Order;

import javax.money.MonetaryAmount;

/**
 * AccountancyEntry for salary payment contains employeeIdentifier
 */
@Entity
public class SalaryAccountancyEntry extends AccountancyEntry {

	private final Employee.EmployeeIdentifier employeeIdentifier;


	/**
	 * Constructs a new SalaryAccountancyEntry with the specified monetary amount, description, and employee identifier.
	 *
	 * @param amount The monetary amount associated with the salary accountancy entry.
	 * @param description The description providing additional information about the salary entry.
	 * @param employeeIdentifier The unique identifier of the employee associated with the salary entry.
	 */
	SalaryAccountancyEntry(MonetaryAmount amount, String description, Employee.EmployeeIdentifier employeeIdentifier) {
		super(amount, description);
		this.employeeIdentifier = employeeIdentifier;
	}

	@SuppressWarnings({"unused", "deprecation"})
	public SalaryAccountancyEntry() {
		employeeIdentifier = null;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Employee.EmployeeIdentifier getEmployeeIdentifier() {
		return employeeIdentifier;
	}

	public Order.OrderIdentifier getOrderIdentifier(){
		return null;
	}
}

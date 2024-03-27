package clockshop.accountancy;

import clockshop.staff.Employee;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link SalaryAccountancyEntry}
 */
class SalaryAccountancyEntryTest {

	private SalaryAccountancyEntry entry;
	@BeforeEach
	void setUp() {
		entry = new SalaryAccountancyEntry(Money.of(1, EURO),"Description",new Employee.EmployeeIdentifier());
	}

	@Test
	void testSalaryAccountancyEntryConstructor(){
		assertNotNull(entry);
	}

	@Test
	void emptySalaryAccountancyEntryConstructorTest(){
		SalaryAccountancyEntry empty = new SalaryAccountancyEntry();
		assertNotNull(empty);
	}

	@Test
	void getEmployeeIdentifierTest(){
		Employee.EmployeeIdentifier id = mock(Employee.EmployeeIdentifier.class);
		SalaryAccountancyEntry salaryAccountancyEntry = new SalaryAccountancyEntry(Money.of(20, "EUR"), "test",id);
		assertEquals(id,salaryAccountancyEntry.getEmployeeIdentifier());
	}

}
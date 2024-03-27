package clockshop.employee;

import clockshop.staff.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.useraccount.UserAccount;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests for {@link Employee}
 */
class EmployeeTest {

	private Employee employee;
	private UserAccount userAccount;

	@BeforeEach
	public void setUp() {
		userAccount = Mockito.mock(UserAccount.class);
		Mockito.when(userAccount.getUsername()).thenReturn("testuser");

		employee = new Employee(userAccount, "John", "Doe", "johndoe", "john@example.com","0173 2519314", "123 Main St", 20.0f, 160, "Developer", "example.jpg");
	}

	@Test
	void testUserAccount() {
		Mockito.when(userAccount.getUsername()).thenReturn("testuser");
		assertEquals(userAccount, employee.getUserAccount());
	}

	@Test
	void testAddress() {
		assertEquals("123 Main St", employee.getAddress());
		employee.setAddress("456 Oak St");
		assertEquals("456 Oak St", employee.getAddress());
	}

	@Test
	void testForename() {
		assertEquals("John", employee.getForename());
		employee.setForename("Jane");
		assertEquals("Jane", employee.getForename());
	}

	@Test
	void testName() {
		assertEquals("Doe", employee.getName());
		employee.setName("Smith");
		assertEquals("Smith", employee.getName());
	}

	@Test
	void testUsername() {
		assertEquals("johndoe", employee.getUsername());
		employee.setUsername("janesmith");
		assertEquals("janesmith", employee.getUsername());
	}

	@Test
	void testEmail() {
		assertEquals("john@example.com", employee.getEmail());
		employee.setEmail("test@example.com");
		assertEquals("test@example.com", employee.getEmail());
	}

	@Test
	void testTelephoneNumber(){
		assertEquals("0173 2519314", employee.getTelephoneNumber());
		employee.setTelephoneNumber("0173 2519315");
		assertEquals("0173 2519315", employee.getTelephoneNumber());
	}

	@Test
	void testImageUrl() {
		assertEquals("example.jpg", employee.getImageUrl());
		employee.setImageUrl("newexample.jpg");
		assertEquals("newexample.jpg", employee.getImageUrl());
	}

	@Test
	void testHoursRate() {
		assertEquals(20.0f, employee.getHourRate());
		employee.setHourRate(25.0f);
		assertEquals(25.0f, employee.getHourRate());
	}

	@Test
	void testHours() {
		assertEquals(160, employee.getMonthlyHours());
		employee.setMonthlyHours(180);
		assertEquals(180, employee.getMonthlyHours());
	}

	@Test
	void testRole() {
		assertEquals("Developer", employee.getRole());
		employee.setRole("Manager");
		assertEquals("Manager", employee.getRole());
	}

	@Test
	void testSalary() {
		assertEquals(3200.0f, employee.getSalary());
		employee.setSalary(4000.0f);
		assertEquals(4000.0f, employee.getSalary());
	}

	@Test
	void testGetPasswordResetToken() {
		assertNotNull(employee.getPasswordResetToken());

		UUID previousToken = employee.getPasswordResetToken();
		employee.generateNewPasswordResetToken();
		assertNotNull(employee.getPasswordResetToken());
		assertNotEquals(previousToken, employee.getPasswordResetToken());
	}

	@Test
	void testGetId() {
		Employee.EmployeeIdentifier expectedId = employee.getId();

		assertEquals(expectedId, employee.getId());
		assertNotNull(expectedId, String.valueOf(employee.getId()));
	}

	@Test
	void testEmptyConstructor() {
		Employee emptyEmployee = new Employee();
		assertNotNull(emptyEmployee);
	}

	@Test
	void testHashCodeAndEquals() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		Employee.EmployeeIdentifier identifier1 = new Employee.EmployeeIdentifier(uuid1);
		Employee.EmployeeIdentifier identifier2 = new Employee.EmployeeIdentifier(uuid1);
		Employee.EmployeeIdentifier identifier3 = new Employee.EmployeeIdentifier(uuid2);
		Employee.EmployeeIdentifier identifier4 = new Employee.EmployeeIdentifier(null);

		assertEquals(31, identifier4.hashCode());
		assertEquals(identifier1, identifier2);
		assertNotEquals(identifier1, identifier3);
		assertEquals(identifier1.hashCode(), identifier2.hashCode());
		assertNotEquals(identifier1.hashCode(), identifier3.hashCode());
		assertFalse(identifier2.equals(employee));

	}
}

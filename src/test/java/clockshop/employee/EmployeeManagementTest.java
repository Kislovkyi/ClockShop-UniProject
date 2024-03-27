package clockshop.employee;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.staff.Employee;
import clockshop.staff.EmployeeManagement;
import clockshop.staff.EmployeeRepository;
import clockshop.staff.RegistrationForm;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link EmployeeManagement}
 */
class EmployeeManagementTest {


	private MockMultipartFile defaultImage;
	private RegistrationForm registrationForm;

	@BeforeEach
	public void setUp() {
		try {
			defaultImage = new MockMultipartFile("default.png", new FileInputStream("src/test/java/clockshop/employee/data/default.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		registrationForm = new RegistrationForm("test", "test", "test", "uhrenladenswt@gmail.com","0173 2519314", "password", "Hellmutweg 3, Dresden", 21F, 174, List.of(Role.of("BOSS")), defaultImage);
	}

	@Test
	 void testCreateEmployee() throws IOException {

		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		Employee employee = employeeManagement.createEmployee(registrationForm);


		String fileName = UUID.randomUUID() + "_" + registrationForm.getImage().getOriginalFilename() + ".png";
		String imageUrl = "resources/images" + "/" + registrationForm.getUsername() + "/" + fileName;
		Employee savedEmployee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(),registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", imageUrl);
		when(employeeRepository.findByUsername(anyString())).thenReturn(Optional.of(savedEmployee));

		assertThat(employee.getUserAccount()).isNotNull();
		verify(employeeRepository).save(any(Employee.class));
		assertTrue(employeeRepository.findByUsername(registrationForm.getUsername()).isPresent());
	}

	@Test
	void testDeleteEmployee() {
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);


		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		when(employeeRepository.findByUsername("nonExistentUsername")).thenReturn(Optional.empty());
		employeeManagement.deleteEmployeebyUsername("nonExistentUsername");
		verify(employeeRepository, never()).delete(any());

		Employee savedEmployee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(),registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl");
		when(employeeRepository.findByUsername(anyString())).thenReturn(Optional.of(savedEmployee));

		employeeManagement.deleteEmployeebyUsername("test");
		verify(employeeRepository).delete(savedEmployee);
	}

	@Test
	void testFindAll(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		List<Employee> employees = Arrays.asList(
			new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl"),
			new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl")
		);

		when(employeeRepository.findAll()).thenReturn(Streamable.of(employees));

		Streamable<Employee> foundEmployees = employeeManagement.findAll();

		assertEquals(employees.size(), foundEmployees.stream().count());
	}

	@Test
	void testFindEmployeeByUsername(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		Employee employee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(),registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl");
		String testUsername = "test";

		when(employeeRepository.findByUsername(testUsername)).thenReturn(Optional.of(employee));

		Optional<Employee> result = employeeManagement.findEmployeeByUsername(testUsername);

		verify(employeeRepository).findByUsername(testUsername);

		assertEquals(employee, result.orElse(null));
	}

	@Test
	void testFindEmployeeByEmail(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		Employee employee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl");
		String testEmail = "uhrenladenswt@gmail.com";

		when(employeeRepository.findByEmail(testEmail)).thenReturn(Optional.of(employee));

		Optional<Employee> result = employeeManagement.findEmployeeByEmail(testEmail);

		verify(employeeRepository).findByEmail(testEmail);

		assertEquals(employee, result.orElse(null));
	}

	@Test
	void testChangePassword(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		String newPassword = "newPassword";

		employeeManagement.changePassword(userAccount, newPassword);

		verify(userAccountManager, times(1)).changePassword(eq(userAccount), any());
	}

	@Test
	public void testFindEmployeeByPasswordResetToken() {
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		UUID testToken = UUID.randomUUID();

		Employee testEmployee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl");

		when(employeeRepository.findByPasswordResetToken(any(UUID.class))).thenReturn(Optional.of(testEmployee));

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		Optional<Employee> foundEmployee = employeeManagement.findEmployeeByPasswordResetToken(testToken);

		assertTrue(foundEmployee.isPresent());
		assertEquals(testEmployee, foundEmployee.get());
	}

	@Test
	void testSalaryPay(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);
		Employee employee = new Employee(userAccount, registrationForm.getForename(), registrationForm.getName(), registrationForm.getUsername(), registrationForm.getEmail(), registrationForm.getTelephoneNumber(), registrationForm.getAddress(), registrationForm.getHourRate(), registrationForm.getMonthlyHours(), "Boss", "imageUrl");
		when(employeeRepository.findByUsername(any(String.class))).thenReturn(Optional.of(employee));

		employeeManagement.paySalaryByUsername("test");
		verify(employeeRepository).findByUsername("test");

		String expectedDescription = "Salary of " + employee.getForename() + " " + employee.getName();
		verify(shopAccountancyManagement).paySalary(Money.of(BigDecimal.valueOf(-employee.getSalary()), EURO), expectedDescription,employee.getId());

	}
	@Test
	void getEmployeePageTest(){
		EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
		ShopAccountancyManagement shopAccountancyManagement = mock(ShopAccountancyManagement.class);
		when(employeeRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccount.getUsername()).thenReturn("test");
		when(userAccountManager.create(
			registrationForm.getUsername(),
			Password.UnencryptedPassword.of(registrationForm.getPassword()),
			registrationForm.getRoles()))
			.thenReturn(userAccount);

		EmployeeManagement employeeManagement = new EmployeeManagement(employeeRepository, userAccountManager, shopAccountancyManagement);

		when(employeeRepository.findAll()).thenReturn(Streamable.of(mock(Employee.class)));

		Page<Employee> employeePage = employeeManagement.getEmployeePage(0, 5);

		verify(employeeRepository, times(1)).findAll();
		assertTrue(employeePage.hasContent());
	}
}



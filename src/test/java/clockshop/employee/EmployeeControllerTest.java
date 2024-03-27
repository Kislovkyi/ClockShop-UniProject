package clockshop.employee;

import clockshop.error.MyErrorController;
import clockshop.extras.EmailService;
import clockshop.staff.*;
import clockshop.time.TimeManagement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link EmployeeController}
 */
class EmployeeControllerTest {

	@Mock
	private EmployeeManagement employeeManagement;

	@InjectMocks
	private EmployeeController employeeController;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserAccount userAccount;

	@Mock
	private EmailService emailService;

	@Mock
	private TimeManagement timeManagement;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		employeeController = new EmployeeController(employeeManagement,
			passwordEncoder,
			emailService,
			timeManagement);
	}

	@Test
	void testCustomLoginForm() {
		String result = employeeController.customLoginForm();
		assertEquals("login", result);
	}

	@Test
	void showResetPasswordFormTest() {
		String result = employeeController.showResetPasswordForm(null);
		assertEquals("Staff/resetPassword", result);
	}

	@Test
	void showRegisterFormTest() {
		String result = employeeController.register(null);
		assertEquals("Staff/register", result);
	}

	@Test
	void errorInRegisterFormTest() throws IOException {
		Errors error = mock(Errors.class);
		when(error.hasErrors()).thenReturn(true);
		String result = employeeController.registerNew(null, error);
		assertEquals("Staff/register", result);
	}

	@Test
	void registerValidUserTest() throws IOException {
		Employee employee = new Employee();
		Errors error = mock(Errors.class);
		RegistrationForm registrationForm = mock(RegistrationForm.class);
		when(registrationForm.getUsername()).thenReturn("Henry");
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.of(employee));
		when(employeeManagement.createEmployee(any())).thenReturn(employee);
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.empty());
		String result = employeeController.registerNew(registrationForm, error);
		assertEquals("redirect:/employees", result);
	}

	@Test
	void myAccountTest() {
		Principal principal = mock(Principal.class);
		TimeManagement timeManagement = mock(TimeManagement.class);
		Employee employee = new Employee();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession httpSession = mock(HttpSession.class);
		when(principal.getName()).thenReturn("Henry");
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.of(employee));
		when(timeManagement.timeString()).thenReturn("");
		when(request.getSession()).thenReturn(httpSession);
		when(timeManagement.dateString()).thenReturn("");
		doNothing().when(request).setAttribute(any(),any());
		Model m = new ExtendedModelMap();
		String result = employeeController.showMyAccount(m, principal, request);
		assertEquals("Staff/myaccount", result);
	}

	@Test
	void myAccountUserNotFoundTest() {
		Principal principal = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(principal.getName()).thenReturn("Henry");
		when(employeeManagement.findEmployeeByUsername(any())).thenReturn(Optional.empty());
		Model m = new ExtendedModelMap();
		String result = employeeController.showMyAccount(m, principal, request);
		assertEquals("error", result);
	}

	@Test
	void usernameAlreadyTakenTest() throws IOException {
		Errors error = mock(Errors.class);
		Employee employee = new Employee();
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));
		RegistrationForm registrationForm = mock(RegistrationForm.class);
		when(registrationForm.getUsername()).thenReturn("Henry");
		String result = employeeController.registerNew(registrationForm, error);
		assertEquals("Staff/register", result);
	}


	@Test
	void testProcessLoginWithValidCredentials() {
		Optional<Employee> employee = Optional.of(new Employee());
		UserAccount useraccount = mock(UserAccount.class);
		employee.get().setUserAccount(userAccount);
		Errors error = mock(Errors.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		Password.EncryptedPassword password = mock(Password.EncryptedPassword.class);
		when(password.toString()).thenReturn("");
		when(useraccount.getPassword()).thenReturn(password);
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(employee);
		when(passwordEncoder.matches(any(), any())).thenReturn(true);
		String result = employeeController.processLogin("", "", redirectAttributes, error);
		assertEquals("Warehouse/catalog", result);
	}

	@Test
	void testProcessLoginWithInvalidCredentials() {
		Optional<Employee> employee = Optional.of(new Employee());
		UserAccount useraccount = mock(UserAccount.class);
		employee.get().setUserAccount(userAccount);
		Errors error = mock(Errors.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		Password.EncryptedPassword password = mock(Password.EncryptedPassword.class);
		when(password.toString()).thenReturn("");
		when(useraccount.getPassword()).thenReturn(password);
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(employee);
		when(passwordEncoder.matches(any(), any())).thenReturn(false);
		when(redirectAttributes.addAttribute(any(), any())).thenReturn(null);
		String result = employeeController.processLogin("", "", redirectAttributes, error);
		assertEquals("login", result);

	}

	@Test
	void testProcessLoginWithUserNotFound() {
		Optional<Employee> employee = Optional.of(new Employee());
		UserAccount useraccount = mock(UserAccount.class);
		employee.get().setUserAccount(userAccount);
		Errors error = mock(Errors.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		Password.EncryptedPassword password = mock(Password.EncryptedPassword.class);
		when(password.toString()).thenReturn("");
		when(useraccount.getPassword()).thenReturn(password);
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.empty());
		when(passwordEncoder.matches(any(), any())).thenReturn(false);
		when(redirectAttributes.addAttribute(any(), any())).thenReturn(null);
		String result = employeeController.processLogin("", "", redirectAttributes, error);
		assertEquals("login", result);

	}

	@Test
	void testShowUpdatePasswordForm_ValidToken() {
		UUID resetPasswordToken = UUID.randomUUID();
		Employee employee = new Employee();
		when(employeeManagement.findEmployeeByPasswordResetToken(resetPasswordToken)).thenReturn(Optional.of(employee));
		Model m = new ExtendedModelMap();
		String result = employeeController.showUpdatePasswordForm(m, resetPasswordToken);

		assertEquals("Staff/updatePassword", result);

	}

	@Test
	void testShowUpdatePasswordForm_InvalidToken() {
		UUID random = UUID.randomUUID();
		Model m = new ExtendedModelMap();
		String result = employeeController.showUpdatePasswordForm(m, random);

		assertEquals("error", result);
	}

	@Test
	void testShowResetPasswordForm_ValidEmail() {
		UUID resetPasswordToken = UUID.randomUUID();
		String email = "test@example.com";
		Employee employee = new Employee();
		employee.setEmail(email);
		employee.setPasswordResetToken(resetPasswordToken);
		Mockito.doNothing().when(emailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(employeeManagement.findEmployeeByEmail(email)).thenReturn(Optional.of(employee));
		String result = employeeController.showResetPasswordForm(email);
		assertEquals("redirect:/login", result);
	}

	@Test
	void updatePasswordTest() {
		Employee employee = new Employee();
		UserAccount userAccount = mock(UserAccount.class);
		employee.setUserAccount(userAccount);
		when(employeeManagement.findEmployeeByEmail(any())).thenReturn(Optional.of(employee));
		String password = "password";
		Mockito.doNothing().when(employeeManagement).changePassword(Mockito.any(), Mockito.anyString());
		String result = employeeController.updatePassword("", password, password);
		assertEquals("login", result);
	}
	@Test
	void updatePasswordWhenPasswordsDoNotMatchTest() {
		Employee employee = new Employee();
		UserAccount userAccount = mock(UserAccount.class);
		employee.setUserAccount(userAccount);
		when(employeeManagement.findEmployeeByEmail(any())).thenReturn(Optional.of(employee));
		String password = "password";
		String password2 = "password2";
		Mockito.doNothing().when(employeeManagement).changePassword(Mockito.any(), Mockito.anyString());
		String result = employeeController.updatePassword("", password, password2);
		assertEquals("login", result);
	}

	@Test
	void testShowResetPasswordForm_InvalidEmail() {
		UUID resetPasswordToken = UUID.randomUUID();
		String email = "test@example.com";

		Employee employee = new Employee();

		employee.setEmail(email);
		employee.setPasswordResetToken(resetPasswordToken);
		Mockito.doNothing().when(emailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(employeeManagement.findEmployeeByEmail(email)).thenReturn(Optional.empty());
		String result = employeeController.showResetPasswordForm(email);

		assertEquals("Staff/resetPassword", result);
		assertTrue(employeeManagement.findEmployeeByEmail(email).isEmpty());

	}

	@Test
	void showEmployeesTest() {
		Model m = new ExtendedModelMap();
		Streamable<Employee> employeeList = Streamable.empty();
		when(employeeManagement.findAll()).thenReturn(employeeList);
		String result = employeeController.employees(m, null, null, null, null,null);
		assertEquals("Staff/employees", result);
	}

	@Test
	void showEmployeesWithSearchTermTest() {
		Model m = new ExtendedModelMap();
		Employee employee = mock(Employee.class);
		Streamable<Employee> employeeList = Streamable.empty();
		when(employeeManagement.findAll()).thenReturn(employeeList);
		when(employeeManagement.search(any())).thenReturn(Streamable.of(employee).toList());
		String result = employeeController.employees(m, null, null, null, null,"null");
		assertEquals("Staff/employees", result);
	}

	@Test
	void deleteEmployeesTest() {
		Model m = new ExtendedModelMap();
		Streamable<Employee> employeeList = Streamable.empty();
		when(employeeManagement.findAll()).thenReturn(employeeList);
		Mockito.doNothing().when(employeeManagement).deleteEmployeebyUsername(anyString());
		String result = employeeController.employees(m, "del", null, null, null,null);
		assertEquals("redirect:/employees", result);
	}

	@Test
	void editEmployeesFormTest() {
		Employee employee = new Employee();
		Model m = new ExtendedModelMap();
		Streamable<Employee> employeeList = Streamable.empty();
		when(employeeManagement.findAll()).thenReturn(employeeList);
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));
		Mockito.doNothing().when(employeeManagement).deleteEmployeebyUsername(anyString());
		String result = employeeController.employees(m, null, "Henry", null, null,null);
		assertEquals("Staff/employees", result);
	}

	@Test
	void editEmployee() {
		Employee employee = new Employee();
		employee.setMonthlyHours(20);
		employee.setHourRate(40F);
		Model m = new ExtendedModelMap();
		MultipartFile image = mock(MultipartFile.class);

		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));

		String result = employeeController.editEmployee(m, "test", "test", "test", "test", 20, "20","20", "mail@mail.com", image, "WATCH");

		assertEquals("Staff/employees", result);
	}

	@Test
	void editEmployeeWhenEmployeeNotFound() {
		Employee employee = new Employee();
		employee.setMonthlyHours(20);
		employee.setHourRate(40F);
		Model m = new ExtendedModelMap();
		MultipartFile image = mock(MultipartFile.class);

		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));

		String result = employeeController.editEmployee(m, "test", "test", "test", "test", 20, "20","20","mail@mail.com",image,"BOSS");

		assertEquals("Staff/employees", result);
	}

	@Test
	void editEmployeeWithEmptyValue() {
		Employee employee = new Employee();
		employee.setMonthlyHours(20);
		employee.setHourRate(40F);
		Model m = new ExtendedModelMap();
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));
		String result = employeeController.editEmployee(m, null, null, null, null, null, null,null, null,null,null);

		assertEquals("Staff/employees", result);
	}

	@Test
	void editEmployeeWithUserNotPresent() {
		Employee employee = new Employee();
		employee.setMonthlyHours(20);
		employee.setHourRate(40F);
		Model m = new ExtendedModelMap();
		when(employeeManagement.findEmployeeByUsername(anyString())).thenReturn(Optional.of(employee));
		String result = employeeController.editEmployee(m, null, null, null, null, null, null,null,null, null,null);

		assertEquals("Staff/employees", result);
	}

	@Test
	void logOutUserWhenAuthNullTest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		String result = employeeController.logoutPage(request, response);
		assertEquals("redirect:/login?logout", result);
	}

	@Test
	void logOutUserTest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		String result = employeeController.logoutPage(request, response);
		assertEquals("redirect:/login?logout", result);
	}

	@Test
	void errorControllerTest(){
		MyErrorController myErrorController = new MyErrorController();
		HttpServletResponse response = mock(HttpServletResponse.class);
		Model model = mock(Model.class);
		String result = myErrorController.handleError(response, model);
		assertEquals("error", result);
	}


}

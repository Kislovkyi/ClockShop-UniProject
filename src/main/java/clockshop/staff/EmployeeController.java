package clockshop.staff;


import clockshop.extras.EmailService;
import clockshop.extras.PageData;
import clockshop.extras.PageService;
import clockshop.time.TimeManagement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Controller
public class EmployeeController {
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);
	private final EmployeeManagement employeeManagement;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final TimeManagement timeManagement;
	private static final String REGISTER = "Staff/register";
	private static final String EMPLOYEE = "Staff/employees";
	private static final String EMPLOYEE_STR = "employees";
	private static final String EMPLOYEELIST = "employeeList";
	private static final String LOGIN = "login";
	private static final String ERROR = "error";
	private static final String MYACCOUNT = "Staff/myaccount";
	private static final String CATALOG = "Warehouse/catalog";
	private static final String UPDATE_PASSWORD = "Staff/updatePassword";
	private static final String RESET_PASSWORD = "Staff/resetPassword";

	/**
	 * EmployeeController constructor.
	 *
	 * @param employeeManagement The employee management service.
	 * @param passwordEncoder The password encoder for securing passwords.
	 * @param emailService The email service for sending emails.
	 * @param timeManagement The time management service.
	 */
	public EmployeeController(EmployeeManagement employeeManagement,
							  PasswordEncoder passwordEncoder,
							  EmailService emailService,
							  TimeManagement timeManagement) {
		this.employeeManagement = employeeManagement;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.timeManagement = timeManagement;
	}

	/**
	 * Displays the custom login form.
	 *
	 * @return The view name for the custom login form.
	 */
	@GetMapping("/")
	public String customLoginForm() {
		return LOGIN;
	}

	/**
	 * Processes POST from Login-Form
	 * validates Login Credentials
	 *
	 * @return the MyAccount template on successful validation.
	 */

	@PostMapping("/login")
	public String processLogin(@RequestParam("username") String username,
							   @RequestParam("password") String password,
							   RedirectAttributes redirectAttributes, Errors result) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(username);
		if (user.isPresent()
			&& passwordEncoder.matches(password, String.valueOf(user.get().getUserAccount().getPassword()))) {
			LOG.info(format("Logged into %s", username));
			return CATALOG;
		} else {
			redirectAttributes.addFlashAttribute(ERROR, "Invalid username or password");
			LOG.error("Invalid Username or Password...");
			return LOGIN;
		}
	}


	/**
	 * Displays the password reset form for sending a reset-mail
	 *
	 * @param email will never be {@literal null}.
	 * @return the login template.
	 */
	@GetMapping("/password-reset")
	public String showResetPasswordForm(@RequestParam(required = false) final String email) {
		if (email != null) {
			Optional<Employee> user = employeeManagement.findEmployeeByEmail(email);
			if (user.isPresent()) {
				String emailText;
				String URL = "http://127.0.0.1:8080/updatePassword?resetPasswordToken="
					+ user.get().getPasswordResetToken();
				emailText = "Dear " + user.get().getForename() + " " + user.get().getName() + ", \n" + "\n" +
					"We've heard that you lost your password. Sorry about that :c " + "\n" +
					"In order to reset your password - click the following link." + "\n" + "\n" + URL;
				emailService.sendEmail(email, "Request Password Reset", emailText);
				return "redirect:/login";
			}
		}
		return RESET_PASSWORD;
	}

	/**
	 * Displays update password form for resetting the password
	 * usually entered through email
	 *
	 * @return the updatePassword form
	 */

	@GetMapping("/updatePassword")
	public String showUpdatePasswordForm(Model model, @RequestParam final UUID resetPasswordToken) {
		if (employeeManagement.findEmployeeByPasswordResetToken(resetPasswordToken).isPresent()) {
			model.addAttribute("Employee",
				employeeManagement.findEmployeeByPasswordResetToken(resetPasswordToken));
			return UPDATE_PASSWORD;
		} else {
			return ERROR;
		}
	}

	/**
	 * Processes the POST from update-password form
	 *
	 * @param email    will never be {@literal null}
	 * @param password should equal @param password2
	 * @return the login form
	 */
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam final String email,
								 @RequestParam final String password,
								 @RequestParam final String password1) {

		if (employeeManagement.findEmployeeByEmail(email).isPresent() && Objects.equals(password, password1)) {
			employeeManagement.changePassword(employeeManagement.findEmployeeByEmail(email).get().getUserAccount(),
				password);
			employeeManagement.findEmployeeByEmail(email).get().generateNewPasswordResetToken();
		}
		return LOGIN;
	}

	/**
	 * Processes the POST from register form
	 *
	 * @param form contains all user-data and validates it
	 * @return employees on success
	 */
	@PostMapping("/register")
	@Secured({"BOSS"})
	public String registerNew(@Valid RegistrationForm form, Errors result) throws IOException {
		if (result.hasErrors()) {
			return REGISTER ;
		}
		if (employeeManagement.findEmployeeByUsername(form.getUsername()).isEmpty()) {
			employeeManagement.createEmployee(form);
			return "redirect:/employees";
		} else {
			result.rejectValue("username", "username.exists");
			return REGISTER;
		}

	}

	/**
	 * Displays the registration form
	 *
	 * @return registration form
	 */
	@GetMapping("/register")
	@Secured({"BOSS"})
	public String register(RegistrationForm form) {
		return REGISTER;
	}

	/**
	 * Displays the useraccount information of the logged-In User
	 *
	 * @return myAccount
	 */

	@GetMapping("myaccount")
	@Secured({"BOSS", "WATCH", "SALE"})
	public String showMyAccount(Model model, Principal principal, HttpServletRequest request) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(principal.getName());
		if (user.isPresent()) {
			model.addAttribute("employee", user.get());
			model.addAttribute("date", timeManagement.dateString());
			model.addAttribute("time", timeManagement.timeString());
			request.getSession().setAttribute("employee", user.get());
			return MYACCOUNT;
		} else {
			return ERROR;
		}

	}

	/**
	 * Displays all registered employees in
	 *
	 * @param del   indicates that a user is to be deleted
	 * @param edit  indicates that a user is to be edited
	 * @param model contains all registed employees
	 * @return employees
	 */

	@GetMapping(value = "/employees")
	@PreAuthorize("hasRole('BOSS')")
	public String employees(Model model,
							@RequestParam(required = false) final String del,
							@RequestParam(required = false) final String edit,
							@RequestParam(required = false) Integer size,
							@RequestParam(required = false) Integer page,
							@RequestParam(required = false) String searchTerm) {
		if ((del == null) == (edit == null)) {

			PageData<Employee> pageData = PageService.calculatePageData(size, page, searchTerm, employeeManagement);
			model.addAttribute("page", pageData.getPage());
			model.addAttribute("maxPages", pageData.getMaxPages());

			if (pageData.getPaginatedResults().isEmpty()) {
				model.addAttribute(EMPLOYEELIST, employeeManagement.getEmployeePage(pageData.getPage(), pageData.getSize()));
			} else {
				model.addAttribute(EMPLOYEELIST, new PageImpl<>(pageData.getPaginatedResults(),
					PageRequest.of(pageData.getPage(), pageData.getSize()),
					pageData.getTotalResults()));

				return EMPLOYEE;
			}
		}
			if (del != null) {
				employeeManagement.deleteEmployeebyUsername(del);
				model.addAttribute(EMPLOYEELIST, employeeManagement.findAll());
				return "redirect:/" + EMPLOYEE_STR;
			}
			return EMPLOYEE;

	}

	/**
	 * Displays the form for editing an employee.
	 *
	 * @param model The Spring MVC model for the view.
	 * @param username The username of the employee to be edited.
	 * @return The view name for the employee editing form.
	 */
	@GetMapping("/editEmployee")
	public String editEmployee(Model model, @RequestParam("username") String username) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(username);

		if (user.isPresent()) {
			model.addAttribute("employee", user.get());
		}


		return "Staff/editEmployee";
	}


	/**
	 * Processes POST from edit employee form
	 *
	 * @param model contains all registered employees + updated employee
	 *              parameters contains all employee data
	 *              check if param is empty otherwise change employee data
	 * @return employees
	 */
	@PostMapping("edit_employee")
	@PreAuthorize("hasRole('BOSS')")
	public String editEmployee(Model model,
							   @RequestParam(value = "forename", required = false) String forename,
							   @RequestParam(value = "name", required = false) String name,
							   @RequestParam(value = "username") String username,
							   @RequestParam(value = "address", required = false) String address,
							   @RequestParam(value = "monthlyHours", required = false) Integer monthlyHours,
							   @RequestParam(value = "telephoneNumber", required = false) String telephoneNumber,
							   @RequestParam(value = "hourRate", required = false) String hourRate,
							   @RequestParam(value = "email", required = false) String email,
							   @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
							   @RequestParam(value = "roles", required = false) String role) {
		if (!employeeManagement.findEmployeeByUsername(username).isPresent()) {

			return EMPLOYEE;
		}
			if (!forename.isEmpty()) {
				employeeManagement.findEmployeeByUsername(username).get().setForename(forename);
			}
			if (!name.isEmpty()) {
				employeeManagement.findEmployeeByUsername(username).get().setName(name);
			}
			if (address != null) {
				employeeManagement.findEmployeeByUsername(username).get().setAddress(address);
			}
			if (hourRate != null) {
				employeeManagement.findEmployeeByUsername(username).get().setHourRate(Float.parseFloat(hourRate));
				employeeManagement.findEmployeeByUsername(username).get().setSalary(Float.parseFloat(hourRate)
					* employeeManagement.findEmployeeByUsername(username).get().getMonthlyHours());
			}
			if (monthlyHours != null) {
				employeeManagement.findEmployeeByUsername(username).get().setMonthlyHours(monthlyHours);
				employeeManagement.findEmployeeByUsername(username).get().setSalary(monthlyHours
					* employeeManagement.findEmployeeByUsername(username).get().getHourRate());
			}
			if (!email.isEmpty()) {
				employeeManagement.findEmployeeByUsername(username).get().setEmail(email);
			}
			if (profilePicture != null) {
				String imageURL = employeeManagement.uploadProfilePicture(profilePicture, username);
				employeeManagement.findEmployeeByUsername(username).get().setImageUrl(imageURL);
			}
			if (role != null){
				employeeManagement.findEmployeeByUsername(username).get().setRole(role);
			}
			model.addAttribute(EMPLOYEELIST, employeeManagement.findAll());

		return EMPLOYEE;
	}

	/**
	 * Logout User
	 */
	@GetMapping("/logout")
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

}


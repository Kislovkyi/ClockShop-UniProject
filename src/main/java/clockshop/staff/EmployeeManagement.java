package clockshop.staff;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.extras.PageManager;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class EmployeeManagement implements PageManager<Employee> {

	private final EmployeeRepository employeeRepository;
	private final UserAccountManagement userAccounts;
	private final ShopAccountancyManagement shopAccountancyManagement;
	/**
	 * Directories for saving profile pictures
	 */
	private static final String UPLOAD_DIRECTORY = "/resources/static/resources/images";
	private static final String HTML_DIRECTORY = "resources/images";


	/**
	 * Manages employees, including user accounts, profile pictures, and salary payments.
	 *
	 * @param employeeRepository The repository for storing and retrieving employee data.
	 * @param userAccounts The user account management component.
	 * @param shopAccountancyManagement The management component for shop accountancy.
	 */
	public EmployeeManagement(EmployeeRepository employeeRepository,
							  @Qualifier("persistentUserAccountManagement") UserAccountManagement userAccounts,
							  ShopAccountancyManagement shopAccountancyManagement) {
		this.employeeRepository = employeeRepository;
		this.userAccounts = userAccounts;
		this.shopAccountancyManagement = shopAccountancyManagement;
	}

	/**
	 * creates a new Employee
	 *
	 * @param form contains all relevant data and validates it
	 *             creates userAccount
	 *             saves itself to EmployeeRepository
	 *             creates folder for profile picture
	 * @return saved employee
	 */
	public Employee createEmployee(RegistrationForm form) throws IOException {
		Assert.notNull(form, "Registration form must not be null!");
		var userAccount = userAccounts.create(form.getUsername(),
			Password.UnencryptedPassword.of(form.getPassword()),
			form.getRoles());

		userAccount.setFirstname(form.getForename());
		userAccount.setLastname(form.getName());
		userAccount.setEmail(form.getEmail());

		String imageUrl = uploadProfilePicture(form.getImage(), form.getUsername());

		String roles = form.getRoles().toString().replaceAll("[\\[\\]]", "");

		return employeeRepository.save(new Employee(userAccount, form.getForename(), form.getName(), form.getUsername(),
			form.getEmail(),form.getTelephoneNumber(), form.getAddress(), form.getHourRate(), form.getMonthlyHours(), roles,
			imageUrl));
	}

	/**
	 * Safely deletes a user
	 * deletes user from repository
	 * deletes profile picture
	 */
	public void deleteEmployeebyUsername(String username) {
		if (employeeRepository.findByUsername(username).isPresent()) {
			deleteEmployeeImageByUsername(username);
			employeeRepository.delete(employeeRepository.findByUsername(username).get());
		}

	}

	/**
	 * deletes profile picture
	 */
	public void deleteEmployeeImageByUsername(String username) {
		String employeeFolderPath = "%s/%s/".formatted(UPLOAD_DIRECTORY, username);
		File folderToDelete = new File(employeeFolderPath);

		File[] files = folderToDelete.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					file.delete();
				} else {
					file.delete();
				}
			}
		}
		folderToDelete.delete();
	}

	/**
	 * @return all Employees
	 */
	public Streamable<Employee> findAll() {
		return employeeRepository.findAll();
	}

	/**
	 * Uploads a profile picture for an employee.
	 *
	 * @param profilePicture The profile picture file.
	 * @param username The username of the employee.
	 * @return The URL of the uploaded profile picture.
	 */
	public String uploadProfilePicture(MultipartFile profilePicture, String username) {
		if (profilePicture != null && !profilePicture.isEmpty()) {
			deleteEmployeeImageByUsername(username); // prevent data spam
			try {
				String employeeFolderPath = "%s/%s/".formatted(UPLOAD_DIRECTORY, username);
				File folder = new File(employeeFolderPath);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				String fileName = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
				Path filePath = Paths.get(employeeFolderPath, fileName);
				Files.write(filePath, profilePicture.getBytes());
				return HTML_DIRECTORY + "/" + username + "/" + fileName;
			} catch (IOException ignored) {
				return HTML_DIRECTORY + "/default.png";
			}
		} else {
			return HTML_DIRECTORY + "/default.png";
		}

	}


	/**
	 * Retrieves an employee by username.
	 *
	 * @param username The username of the employee.
	 * @return An optional containing the employee if found.
	 */
	public Optional<Employee> findEmployeeByUsername(String username) {
		return employeeRepository.findByUsername(username);
	}

	/**
	 * Retrieves an employee by email.
	 *
	 * @param email The email address of the employee.
	 * @return An optional containing the employee if found.
	 */
	public Optional<Employee> findEmployeeByEmail(String email) {
		return employeeRepository.findByEmail(email);
	}

	/**
	 * Retrieves an employee by password reset token.
	 *
	 * @param passwordResetToken The password reset token.
	 * @return An optional containing the employee if found.
	 */
	public Optional<Employee> findEmployeeByPasswordResetToken(UUID passwordResetToken) {
		return employeeRepository.findByPasswordResetToken(passwordResetToken);
	}

	/**
	 * Searches {@link EmployeeRepository} for Employee's UserNames matching the searchTerm
	 * OldName: searchEmployeesByUsername
	 * @param searchTerm {@link String}
	 * @return {@link List<Employee>}
	 */
	public List<Employee> search(String searchTerm) {
		return employeeRepository.findByUsernameContainingIgnoreCase(searchTerm);
	}

	/**
	 * Change user password
	 */
	public void changePassword(UserAccount userAccount, String password) {
		userAccounts.changePassword(userAccount, UnencryptedPassword.of(password));
	}

	/**
	 * Pays the salary for an employee and records it in shop accountancy.
	 *
	 * @param username The username of the employee.
	 */
	public void paySalaryByUsername(String username) {
		Optional<Employee> user = employeeRepository.findByUsername(username);
		if (user.isPresent()) {
			String description = "Salary of " + user.get().getForename() + " " + user.get().getName();
			shopAccountancyManagement.paySalary(Money.of(-user.get().getSalary(), "EUR"), description, user.get().getId());
		}
	}

	/**
	 * Retrieves a page of employees.
	 *
	 * @param page The page number.
	 * @param size The page size.
	 * @return A page of employees.
	 */
	public Page<Employee> getEmployeePage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<Employee> employeeList = employeeRepository.findAll().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<Employee> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	/**
	 * Retrieves an employee by employee identifier.
	 *
	 * @param employeeIdentifier The employee identifier.
	 * @return The employee.
	 */
	public Employee findById(Employee.EmployeeIdentifier employeeIdentifier){
		return employeeRepository.findById(employeeIdentifier).get();
	}

}
package clockshop.staff;

import clockshop.staff.Employee.EmployeeIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends CrudRepository<Employee, EmployeeIdentifier> {

	/**
	 * Retrieves all employees.
	 *
	 * @return A streamable collection of all employees.
	 */
	@Override
	Streamable<Employee> findAll();

	/**
	 * Retrieves an employee by username.
	 *
	 * @param username The username of the employee.
	 * @return An optional containing the employee if found.
	 */
	Optional<Employee> findByUsername(String username);

	/**
	 * Retrieves an employee by email.
	 *
	 * @param email The email address of the employee.
	 * @return An optional containing the employee if found.
	 */
	Optional<Employee> findByEmail(String email);

	/**
	 * Retrieves an employee by password reset token.
	 *
	 * @param token The password reset token.
	 * @return An optional containing the employee if found.
	 */
	Optional<Employee> findByPasswordResetToken(UUID token);

	/**
	 * Searches for employees by username containing the specified search term.
	 *
	 * @param searchTerm The search term for usernames.
	 * @return A list of employees matching the search term.
	 */
	List<Employee> findByUsernameContainingIgnoreCase(String searchTerm);
}
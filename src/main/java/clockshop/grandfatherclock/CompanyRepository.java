package clockshop.grandfatherclock;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * Repository class for managing companies.
 */
@Repository
public class CompanyRepository {

	private final List<Company> companyList;

	/**
	 * Constructs a new CompanyRepository with an empty list of companies.
	 */
	public CompanyRepository() {
		this.companyList = new ArrayList<>();
	}

	/**
	 * Adds a new company to the repository with the specified name and address.
	 *
	 * @param companyName The name of the company to be added.
	 * @param address The address of the company to be added.
	 */
	public void addCompany(String companyName, String address) {
		Company company = new Company(companyName, address);
		companyList.add(company);
	}

	/**
	 * Retrieves a list of all companies in the repository.
	 *
	 * @return A list containing all companies in the repository.
	 */
	public List<Company> getAllCompanies() {
		return companyList;
	}

	/**
	 * Checks if a company with the specified name exists in the repository.
	 *
	 * @param companyName The name of the company to check for existence.
	 * @return true if a company with the given name exists, false otherwise.
	 */
	public boolean existsByName(String companyName) {
		return companyList.stream()
			.anyMatch(company -> company.getCompanyName().equals(companyName));
	}
}

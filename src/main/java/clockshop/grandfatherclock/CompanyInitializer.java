package clockshop.grandfatherclock;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

@Component
public class CompanyInitializer implements DataInitializer {

	private final CompanyRepository companyRepository;

	/**
	 * @param companyRepository contains all companies
	 */
	public CompanyInitializer(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@Override
	public void initialize() {

		companyRepository.addCompany("DariusGaming",
			"Beispiel Straße");
	}
}

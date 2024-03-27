package clockshop.grandfatherclock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controller class for managing companies and their related views.
 */
@Controller
public class CompanyController {

	private final CompanyRepository companyRepository;
	private static final String COMPANIES = "GFC/companies";
	private static final String ADDCOMPANIES = "GFC/addcompanies";

	/**
	 * Constructor for CompanyController.
	 *
	 * @param companyRepository The repository for managing company data.
	 */
	@Autowired
	public CompanyController(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	/**
	 * Handles GET requests to "/companies" to display a list of companies.
	 *
	 * @param model The model to which attributes are added for rendering the view.
	 * @return The view name for displaying the list of companies.
	 */
	@GetMapping("/companies")
	public String showCompany(Model model) {
		model.addAttribute("companies", companyRepository.getAllCompanies());
		return COMPANIES;
	}

	/**
	 * Handles GET requests to "/addcompanies" to display the form for adding companies.
	 *
	 * @return The view name for displaying the form to add companies.
	 */
	@GetMapping("/addcompanies")
	public String add(){
		return ADDCOMPANIES;
	}
}

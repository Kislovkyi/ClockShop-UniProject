package clockshop.catalog;

import clockshop.inventory.ShopInventoryManagement;
import clockshop.extras.PageData;
import clockshop.extras.PageService;
import clockshop.staff.Employee;
import clockshop.staff.EmployeeManagement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CatalogController {

	private final ShopInventoryManagement shopInventoryManagement;
	private final ArticleCatalog articleCatalog;
	private static final String CATALOGSTR = "catalog";
	private static final String CATALOG = "Warehouse/catalog";
	private static final String INVENTORY_STR = "inventory";
	private EmployeeManagement employeeManagement;

	/**
	 * Constructs a new CatalogController with the provided dependencies.
	 *
	 * @param shopInventoryManagement The ShopInventoryManagement instance responsible for managing shop inventory.
	 * @param articleCatalog The ArticleCatalog instance containing information about available articles.
	 * @param employeeManagement The EmployeeManagement instance responsible for managing employees.
	 */
	CatalogController(ShopInventoryManagement shopInventoryManagement,
					  ArticleCatalog articleCatalog,
					  EmployeeManagement employeeManagement) {
		this.shopInventoryManagement = shopInventoryManagement;
		this.articleCatalog = articleCatalog;
        this.employeeManagement = employeeManagement;
    }

	/**
	 * Handles the GET request for displaying the article catalog.
	 *
	 * @param model        The model to which attributes are added for rendering the view.
	 * @param size         The number of articles to display per page.
	 * @param page         The current page number.
	 * @param searchTerm   The search term for filtering articles (optional).
	 * @param principal    Represents the current logged-in user.
	 * @param request      Represents the HTTP request.
	 * @return The view name for displaying the article catalog.
	 */
	@GetMapping("/catalog")
	@Secured({"BOSS", "WATCH", "SALE"})
	String articleCatalog(Model model, @RequestParam(required = false) Integer size,
						  @RequestParam(required = false) Integer page,
						  @RequestParam(required = false) String searchTerm, Principal principal, HttpServletRequest request) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(principal.getName());
		if (user.isPresent()) {
			request.getSession().setAttribute("employee", user.get());
		}


		PageData<Article> pageData = PageService.calculatePageData(size,page,searchTerm,shopInventoryManagement);


		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());
		model.addAttribute(INVENTORY_STR, shopInventoryManagement);

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute(CATALOGSTR,
				shopInventoryManagement.getCatalogAndInventoryPage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute(CATALOGSTR, new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}
		return CATALOG;
	}

}

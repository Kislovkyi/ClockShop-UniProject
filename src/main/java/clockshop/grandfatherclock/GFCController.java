package clockshop.grandfatherclock;


import clockshop.order.ShopOrderManagement;
import clockshop.extras.PageData;
import clockshop.extras.PageService;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.salespointframework.core.Currencies.EURO;

/**
 *
 */
@Controller
public class GFCController {

	private final ShopOrderManagement shopOrderManagement;
	private final GFCManagement gfcManagement;
	private final CompanyRepository companyRepository;

	private final List<GFCCartItem> cartItems = new ArrayList<>();

	private static final String GFCLIST = "GFC/gfclists";
	private static final String GFCLIST_STR = "gfclists";
	private static final String COMPANIES_STR = "companies";
	private static final String GFCORDERS = "GFC/gfcorders";
	private static final String GFCORDERS_STR = "gfcorders";
	private static final String GFC = "GFC/gfc";
	private static final String ADDGFC = "GFC/addgfc";

	private static final String EDITGFC = "GFC/editgfc";

	private final List<GFCCartItem> orderItems = new ArrayList<>();


	/**
	 * Constructs a new instance of the GFCController.
	 *
	 * @param shopOrderManagement The service for managing shop orders.
	 * @param gfcManagement The service for managing Grandfather Clocks (GFC).
	 * @param companyRepository The repository for managing companies.
	 */
	GFCController(ShopOrderManagement shopOrderManagement, GFCManagement gfcManagement,
				  CompanyRepository companyRepository) {
		this.shopOrderManagement = shopOrderManagement;
		this.gfcManagement = gfcManagement;
		this.companyRepository = companyRepository;
	}

	/**
	 * @param model never literal null
	 * @param size length of page
	 * @param page number of page
	 * @param searchTerm search term in list
	 * @return
	 */
	@GetMapping("/gfclists")
	public String gfcPre(Model model,
						 @RequestParam(required = false) Integer size,
						 @RequestParam(required = false) Integer page,
						 @RequestParam(required = false) String searchTerm) {

		PageData<GrandfatherClock> pageData = PageService.calculatePageData(size,page,searchTerm,gfcManagement);


		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute(GFCLIST_STR, gfcManagement.getGFCPage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute(GFCLIST_STR, new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}


		return GFCLIST;
	}

	/**
	 * Displays a list of Grandfather Clocks, optionally filtered by name.
	 *
	 * @param model      The Spring MVC model.
	 * @param size       {@link Integer}
	 * @param page       {@link Integer}
	 * @param searchTerm {@link String}
	 * @return The view name for the Grandfather Clock list page.
	 */
	@GetMapping("/gfcorders")
	public String orderPre(Model model,
						   @RequestParam(required = false) Integer size,
						   @RequestParam(required = false) Integer page,
						   @RequestParam(required = false) String searchTerm) {


		if (page == null) {
			page = 0;
		}
		if (size == null) {
			size = 5;
		}

		if (searchTerm != null && !searchTerm.isEmpty()) {
			List<GFCCartItem> searchResults = shopOrderManagement.searchGFCCartItemByName(searchTerm);
			int totalResults = searchResults.size();
			int max_pages = (int) Math.ceil((double) totalResults / size);

			if (page < 0) {
				page = 0;
			} else if (page >= max_pages && max_pages > 0) {
				page = max_pages - 1;
			}
			int start = page * size;
			int end = Math.min((start + size), totalResults);
			List<GFCCartItem> paginatedResults = searchResults.subList(start, end);

			model.addAttribute("page", page);
			model.addAttribute("maxPages", max_pages);
			model.addAttribute("orderItems", new PageImpl<>(paginatedResults, PageRequest.of(page, size), totalResults));
		} else {
			double dSize = size;
			double employeeAmount = gfcManagement.findAll().toList().size();
			Integer max_pages = (int) Math.ceil(employeeAmount / dSize);
			model.addAttribute("page", page);
			model.addAttribute("maxPages", max_pages);
			model.addAttribute("orderItems", shopOrderManagement.getGFCOrdersPage(page, size));
		}

		return GFCORDERS;
	}

	/**
	 * Displays the form for adding a new Grandfather Clock.
	 *
	 * @param model The Spring MVC model.
	 * @return The view name for the Grandfather Clock addition form.
	 */
	@GetMapping("addGFC")
	public String addGFC(Model model) {
		List<Company> companies = companyRepository.getAllCompanies();
		model.addAttribute(COMPANIES_STR, companies);
		return ADDGFC;
	}

	/**
	 * Updates the status of a Grandfather Clock order item.
	 *
	 * @param itemId  The identifier of the clock item.
	 * @param orderId The identifier of the order.
	 * @return Redirects to the Grandfather Clock orders page.
	 */
	@PostMapping("/gfcorders-updatestatus")
	public String updateStatus(@RequestParam("itemId") Product.ProductIdentifier itemId,
							   @RequestParam("orderId") Order.OrderIdentifier orderId) {
		for (GFCCartItem item : shopOrderManagement.getOrderItems()) {
			if (item.getId().equals(itemId) && item.getOrderId().equals(orderId)) {
				switch (item.getStatusType()) {
					case ORDERED:
						item.setStatusType(StatusType.READY);
						break;
					case READY:
						item.setStatusType(StatusType.FINISHED);
						break;
					default:
						break;
				}
			}
		}
		return "redirect:/gfcorders";
	}

	/**
	 * Deletes a Grandfather Clock item.
	 *
	 * @param id The identifier of the clock item.
	 * @return Redirects to the Grandfather Clock list page.
	 */
	@PostMapping("/deleteGFC")
	public String deleteCartItem(@RequestParam("id") Product.ProductIdentifier id) {

		gfcManagement.deleteGFCById(id);
		return "redirect:/" + GFCLIST_STR;
	}

	/**
	 * Handles the addition of a new Grandfather Clock.
	 *
	 * @param name        The name of the clock.
	 * @param price       The price of the clock.
	 * @param companyName The name of the clock's company.
	 * @return Redirects to the Grandfather Clock list page.
	 */
	@PostMapping("addgfc-add")
	public String addClock(@RequestParam("name") String name,
						   @RequestParam("price") int price,
						   @RequestParam("companyName") String companyName,
						   @RequestParam("des") String description,
						   @RequestParam("discount") double discount) {

		if (gfcManagement.GFCNameExists(name)) {
			return "redirect:/gfclists";
		}
		gfcManagement.addClock(name, price, companyName, description, discount);

		return "redirect:/" + GFCLIST_STR;
	}


	/**
	 * Handles the addition of a new company.
	 *
	 * @param companyName The name of the company.
	 * @param address     The address of the company.
	 * @return Redirects to the Grandfather Clock addition form.
	 */
	@PostMapping("addcompanies-add")
	public String addCompany(@RequestParam("companyName") String companyName,
							 @RequestParam("address") String address) {

		if (companyRepository.existsByName(companyName)) {

			return "redirect:/addcompanies";
		}
		companyRepository.addCompany(companyName, address);

		return "redirect:/addGFC";
	}

	/**
	 * Displays the edit page for a Grandfather Clock (GFC) with the specified identifier.
	 *
	 * @param model The Spring MVC model to add attributes.
	 * @param id The identifier of the Grandfather Clock to be edited.
	 * @return The view name for the edit GFC page.
	 */
	@GetMapping("/editgfc")
	public String showEditGFCPage(Model model,
									 @RequestParam("id") Product.ProductIdentifier id) {
		GrandfatherClock grandfatherClock = gfcManagement.findById(id).get();
		List<Company> companies = companyRepository.getAllCompanies();
		model.addAttribute(COMPANIES_STR, companies);
		model.addAttribute("gfc", grandfatherClock);
		return "GFC/editgfc";
	}

	/**
	 * @param id of Grandfatherclock item
	 * @param name of Grandfatherclock
	 * @param price of Grandfatherclock
	 * @param companyName of manufacturer
	 * @param description of Article
	 * @param discount of selling price
	 * @return
	 */
	@PostMapping("/edit")
	public String editGFC(@RequestParam("id") Product.ProductIdentifier id,
						  @RequestParam("name") String name,
						  @RequestParam("price") int price,
						  @RequestParam("companyName") String companyName,
						  @RequestParam("des") String description,
						  @RequestParam("discount") double discount){
		GrandfatherClock grandfatherClock = gfcManagement.findById(id).get();
		grandfatherClock.setName(name);
		grandfatherClock.setPrice(Money.of(price,EURO));
		grandfatherClock.setCompanyName(companyName);
		grandfatherClock.setDescription(description);
		grandfatherClock.setDiscount(discount);
		gfcManagement.updateGFC(grandfatherClock);
		return "redirect:/" + GFCLIST_STR;
		}

}



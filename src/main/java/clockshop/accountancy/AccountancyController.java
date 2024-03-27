package clockshop.accountancy;


import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;

import clockshop.extras.PageData;
import clockshop.extras.PageService;
import clockshop.staff.EmployeeManagement;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.accountancy.OrderPaymentEntry;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;


@Controller
class AccountancyController {

	private final ShopInventoryManagement inventory;

	private final ShopAccountancyManagement accountancy;

	private final ShopOrderManagement orderManagement;

	private final EmployeeManagement employeeManagement;

	private final BusinessTime businessTime;

	private final StatisticsManagement statisticsManagement;

	private static final String ACCOUNTING = "Accounting/accounting";
	private static final String ACCOUNTING_ENTRY = "Accounting/accountingEntry";
	private static final String ACCOUNTING_OVERVIEW = "Accounting/accountingOverview";
	private static final String ACCOUNTING_ALL_ORDER = "Accounting/accountingAllOrder";

	/**
	 * Constructs a new AccountancyController with the provided dependencies.
	 *
	 * @param inventory The ShopInventoryManagement instance responsible for managing shop inventory.
	 * @param accountancy The ShopAccountancyManagement instance handling accountancy operations.
	 * @param orderManagement The ShopOrderManagement instance responsible for managing shop orders.
	 * @param employeeManagement The EmployeeManagement instance responsible for managing employees.
	 * @param businessTime The BusinessTime instance providing information about the current business time.
	 * @param statisticsManagement The StatisticsManagement instance for handling statistical data.
	 */
	AccountancyController(ShopInventoryManagement inventory,
                          ShopAccountancyManagement accountancy,
                          ShopOrderManagement orderManagement, EmployeeManagement employeeManagement,
                          BusinessTime businessTime,
                          StatisticsManagement statisticsManagement) {
		this.inventory = inventory;
		this.accountancy = accountancy;
		this.orderManagement = orderManagement;
        this.employeeManagement = employeeManagement;
        this.businessTime = businessTime;
        this.statisticsManagement = statisticsManagement;

	}


	/**
	 * Displays every AccountancyEntry in Accountancy
	 *
	 * @param model will never be {@literal null}.
	 * @return the template name.
	 */
	@GetMapping("/accounting")
	@Secured({"BOSS"})
	String accounting(Model model,
					  @RequestParam(value = "page", required = false) Integer page,
					  @RequestParam(value = "length", required = false) Integer length) {
		var entries = accountancy.findAllByOrderByDateDesc();

		ArrayList<LocalDateTime> monthList = new ArrayList<>();
		if (!entries.isEmpty()) {
			var startDate = entries.get(entries.size() - 1).getDate().get();
            LocalDateTime iterator = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), 1
				, 0, 0, 0, 0);
			while (iterator.isBefore(businessTime.getTime())) {
				monthList.add(iterator);
				iterator = iterator.plusMonths(1);
			}
			Collections.reverse(monthList);
		}
		if(page == null){
			page = 0;
		}
		if(length == null){
			length = 5;
		}
		model.addAttribute("protocol", accountancy.getAccountancyPage(page,length));

		double entryAmount = entries.size();
		double dLength = length;

		model.addAttribute("chartData", statisticsManagement.getGraphData());
		model.addAttribute("totalProfit", accountancy.calculateTotals()[0]);
		model.addAttribute("totalExpense",accountancy.calculateTotals()[1]);
		model.addAttribute("pieChartData", statisticsManagement.getPieChartData());
		model.addAttribute("accountancy", accountancy);
		model.addAttribute("monthList", monthList);
		model.addAttribute("repairsCount", statisticsManagement.getFinishedRepairsCount());
		model.addAttribute("completedOrdersCount", statisticsManagement.getCompletedOrdersCount());
		model.addAttribute("mostSells", statisticsManagement.getMostSells());


		model.addAttribute("RecentOrders", statisticsManagement.getRecentOrders());
		model.addAttribute("orderManagement", orderManagement);


		model.addAttribute("page", page);
		model.addAttribute("maxPages", Math.ceil(entryAmount/dLength));
		return ACCOUNTING;
	}

	/**
	 * Displays Details for specific AccountancyEntry
	 *
	 * @param model will never be {@literal null}.
	 * @param id    a AccountancyEntryIdentifier
	 * @return the template name.
	 */
	@GetMapping("/accounting-id")
	public String details(Model model,
						  @RequestParam("id") AccountancyEntry.AccountancyEntryIdentifier id) {


		// Complete Entry
		model.addAttribute("accEntry", accountancy.getEntry(id));
		// OrderManagement
		model.addAttribute("orderManagement", orderManagement);
		// inventoryManagement
		model.addAttribute("inventoryManagement", inventory);
		model.addAttribute("employeeManagement",employeeManagement);

		// if the Entry is created by OrderManagement
		if (accountancy.getEntry(id) instanceof OrderPaymentEntry entry) {
			// can be defaultOrder, GFC, Repair


			// is defaultOrder
			if (!orderManagement.getOrder(entry.getOrderIdentifier()).getChargeLines().get().toList()
				.stream().filter(chargeLine -> chargeLine.getDescription().equals("ORDER")).toList().isEmpty()){
				model.addAttribute("entryType","ORDER");
			}else if (!orderManagement.getOrder(entry.getOrderIdentifier()).getChargeLines().get().toList()
				.stream().filter(chargeLine -> chargeLine.getDescription().equals("REPAIR")).toList().isEmpty()){
				model.addAttribute("entryType","REPAIR");
			}else if (!orderManagement.getOrder(entry.getOrderIdentifier()).getChargeLines().get().toList()
				.stream().filter(chargeLine -> chargeLine.getDescription().equals("GFC")).toList().isEmpty()){
				model.addAttribute("entryType","GFC");
			}else if (!orderManagement.getOrder(entry.getOrderIdentifier()).getChargeLines().get().toList()
				.stream().filter(chargeLine -> chargeLine.getDescription().equals("BUY")).toList().isEmpty()){
				model.addAttribute("entryType","BUY");

			}else {
				model.addAttribute("entryType","NON");
			}

			model.addAttribute("canceled", entry.getDescription().contains("canceled"));

			model.addAttribute("oEntry", entry);
			model.addAttribute("order", orderManagement.getOrder(
				entry.getOrderIdentifier()));
			model.addAttribute("orderLines", orderManagement.getOrder(
				entry.getOrderIdentifier()).getOrderLines().toList());
			model.addAttribute("extraCharges", orderManagement.getOrder(
				entry.getOrderIdentifier()).getChargeLines().toList());


		// if SortOutAccountancyEntry
		} else if (accountancy.getEntry(id) instanceof SortoutAccountancyEntry entry) {
			model.addAttribute("outEntry", entry);
			model.addAttribute("entryType","ENTRY");
			model.addAttribute("product",inventory.findProduct(entry.getProductId()));

		// if SalaryAccountancyEntry
		} else if (accountancy.getEntry(id) instanceof SalaryAccountancyEntry entry) {
			model.addAttribute("salEntry", entry);
			model.addAttribute("entryType","ENTRY");

		// if MaintenanceAccountancyEntry
		} else if (accountancy.getEntry(id) instanceof MaintenanceAccountancyEntry entry){
			model.addAttribute("maintenanceEntry", entry);
			model.addAttribute("entryType","ENTRY");
		}

		return ACCOUNTING_ENTRY;
	}


	/**
	 * Cancels an order and returns to the accounting site
	 *
	 * @param model         will never be {@literal null}.
	 * @param orderIdString String of the OrderID
	 * @param reason        String that describes why the Order was canceled
	 * @return the template name.
	 */
	@PostMapping("/accounting-cancel")
	public String cancelOrder(Model model,
							  @RequestParam("orderIdString") String orderIdString,
							  @RequestParam("reason") String reason) {
		Order.OrderIdentifier orderId = Order.OrderIdentifier.of(orderIdString);
		orderManagement.cancelOrder(orderManagement.getOrder(orderId), reason);

		return "redirect:/accounting";
	}

	/**
	 * Returns an overview of a given Month
	 * @param date date of month to get overview of
	 * @param model will never be {@literal null}.
	 * @return the template name.
	 */
	@GetMapping("accounting-overview")
	public String getOverview(@RequestParam final String date,
							  Model model) {
		String[] splittDate = date.split("\\.", 2);
		Integer year = Integer.valueOf(splittDate[0]);
		Integer month = Integer.valueOf(splittDate[1]);
		List<AccountancyEntry> list = accountancy.findOrderByMonthAndYear(month, year);
		model.addAttribute("entries", list);
		model.addAttribute("sum", accountancy.salesVolumeOfMonth(month, year));
		return ACCOUNTING_OVERVIEW;
	}

	/**
	 * Returns an overview of a all Orders that are made
	 * @param model The model to be populated with data for rendering the view; must not be {@literal null}.
	 * @param size The size of the page to be retrieved;
	 * @param page The page number to be retrieved;
	 * @param searchTerm The search term to filter orders;
	 * @return the template name.
	 */
	@GetMapping("accountingAllOrder")
	public String getAllOrder(Model model,
							  @RequestParam(required = false) Integer size,
							  @RequestParam(required = false) Integer page,
							  @RequestParam(required = false) String searchTerm) {

		PageData<AccountancyEntry> pageData = PageService.calculatePageData(size,page,searchTerm,accountancy);

		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());
		model.addAttribute("orderManagement", orderManagement);

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute("AllOrder", accountancy.getAccountancyPage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute("AllOrder", new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}

		return ACCOUNTING_ALL_ORDER;
	}

}
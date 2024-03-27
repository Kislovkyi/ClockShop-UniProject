package clockshop.maintenance;

import clockshop.extras.PageData;
import clockshop.extras.PageService;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;


@Controller
public class MaintenanceController {

	private final MaintenanceRepository maintenanceRepository;
	private final MaintenanceManagement maintenanceManagement;
	private static final String MAINTENANCE = "Maintenance/maintenance";
	private static final String ADDMAINTENANCE = "Maintenance/addMaintenance";

	private static final String ATTRMAINTENANCE = "maintenance";
	private final BusinessTime businessTime;

	/**
	 * Constructs a new instance of MaintenanceController with the provided maintenance repository,
	 * maintenance management, and business time.
	 *
	 * @param maintenanceRepository The repository for managing maintenance-related data.
	 * @param maintenanceManagement The management system for handling maintenance operations.
	 * @param businessTime The business time utility used for time-related operations.
	 */
	@Autowired
	MaintenanceController(MaintenanceRepository maintenanceRepository,
						  MaintenanceManagement maintenanceManagement,
						  BusinessTime businessTime) {
		this.maintenanceRepository = maintenanceRepository;
		this.maintenanceManagement = maintenanceManagement;
		this.businessTime = businessTime;
	}

	/**
	 * shows the already existing Maintenance Contracts and the form of the input
	 *
	 * @param company contract partner
	 * @param towerQuantity	Quantity of the Tower Clocks
	 * @param buildingQuantity Quantity of Building Clocks
	 * @param model contains all maintenance Contracts
	 * @return
	 */
	@GetMapping("/maintenance")
	public String maintenance(@RequestParam(value = "company", required = false) String company,
							  @RequestParam(value = "towerQuantity", required = false) Integer towerQuantity,
							  @RequestParam(value = "buildingQuantity", required = false) Integer buildingQuantity,
							  @RequestParam(required = false) Integer size,
							  @RequestParam(required = false) Integer page,
							  @RequestParam(required = false) String searchTerm,
							  Model model) {
		PageData<MaintenanceContract> pageData = PageService.calculatePageData(size,page,searchTerm,maintenanceManagement);


		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute(ATTRMAINTENANCE,
				maintenanceManagement.getMaintenancePage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute(ATTRMAINTENANCE, new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}
		return MAINTENANCE;

	}

	/**
	 * @return template
	 */
	@GetMapping("/addMaintenance")
	public String add (){ return ADDMAINTENANCE;}

	/**
	 * handles the input of a new maintenance Contract
	 *
	 * @param model contains the existing maintenance Contracts
	 * @param price price of the Tower Clocks
	 * @param price2 price of the Building Clocks
	 * @param address of the company
	 * @param contactPerson for the pdf bill
	 */
	@PostMapping("/addMaintenance")
	public String submit(Model model,
						 @RequestParam("company") String company,
						 @RequestParam("towerQuantity") Integer towerQuantity,
						 @RequestParam("buildingQuantity") Integer buildingQuantity,
						 @RequestParam(value = "towerSubmit", required = false) String towerSubmit,
						 @RequestParam(value = "buildingSubmit", required = false) String buildingSubmit,
						 @RequestParam("price") Integer price,
						 @RequestParam("price2") Integer price2,
						 @RequestParam("address") String address,
						 @RequestParam("contactPerson") String contactPerson) throws DocumentException, IOException, WriterException {

		MaintenanceContract maintenanceContract = new MaintenanceContract(company,
			towerQuantity,
			buildingQuantity,
			businessTime,
			Money.of(price*towerQuantity + price2*buildingQuantity, "EUR"),
			contactPerson,
			address);
		maintenanceManagement.addContract(maintenanceContract);
		model.addAttribute(ATTRMAINTENANCE, maintenanceRepository.findAll());
		return "redirect:/maintenance";
	}

	/**
	 * delete an existing Contract
	 *
	 * @param model contains the existing Maintenance Contracts
	 * @param id of the contract to be deleted
	 * @return
	 */

	@GetMapping("/update-maintenance")
	public String updateMaintenance(Model model, @RequestParam("id") UUID id) {
		maintenanceManagement.deleteMaintenanceById(id);
		model.addAttribute(ATTRMAINTENANCE, maintenanceRepository.findAll());

		return "redirect:/maintenance";
	}


}

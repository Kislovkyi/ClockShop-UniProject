package clockshop.repair;

import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;
import clockshop.extras.EmailService;
import clockshop.extras.PageData;
import clockshop.extras.PageService;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for communication with frontend.
 * Manages adding of repairs, finishing repairs,
 * adding material to repairs and changing the quantity of added materials.
 */

@Controller
public class RepairController {
	private final RepairManagement repairManagement;
	private final EmailService emailService;
	private final ShopOrderManagement shopOrderManagement;
	private final ShopInventoryManagement inventory;
	private final BusinessTime businessTime;

	private final Map<ProductIdentifier, Integer> materialMap;
	private ProductIdentifier tempProductId;

	private static final String REPAIR = "Repair/repair";
	private static final String REPAIR_STR = "repair";
	private static final String REDIRECT_REPAIR = "redirect:/repair";
	private static final String MATERIALS_ATTRIBUTE = "materials";
	private static final String MATERIALMAP_ATTRIBUTE = "materialMap";


	/**
	 * Constructor for the RepairController class.
	 *
	 * @param emailService The service for sending emails.
	 * @param businessTime The business time.
	 * @param repairManagement The management for repairs.
	 * @param inventory The inventory of the shop.
	 * @param shopOrderManagement The management for shop orders.
	 */
	public RepairController(EmailService emailService,
							BusinessTime businessTime,
							RepairManagement repairManagement,
							ShopInventoryManagement inventory,
							ShopOrderManagement shopOrderManagement) {

		this.emailService = emailService;
		this.businessTime = businessTime;
		this.repairManagement = repairManagement;
		this.inventory = inventory;
		this.shopOrderManagement = shopOrderManagement;
		materialMap = new HashMap<>();
	}

	/**
	 * Handles the HTTP GET request to view the repair page.
	 *
	 * @param model The Spring MVC model.
	 * @param size The size of the page.
	 * @param page The page number.
	 * @return The view name for the repair pages.
	 */
	@GetMapping("/repair")
	public String repairPre(Model model, 
							@RequestParam(required = false) Integer size,
							@RequestParam(required = false) Integer page,
							@RequestParam(required = false) String searchTerm) {


		PageData<Repair> pageData = PageService.calculatePageData(size,page,searchTerm,repairManagement);

		model.addAttribute("RepairType", RepairType.values());
		model.addAttribute("page", pageData.getPage());
		model.addAttribute("maxPages", pageData.getMaxPages());

		if (pageData.getPaginatedResults().isEmpty()){
			model.addAttribute("repairs", repairManagement.getRepairPage(pageData.getPage(), pageData.getSize()));
		} else {
			model.addAttribute("repairs", new PageImpl<>(pageData.getPaginatedResults(),
				PageRequest.of(pageData.getPage(), pageData.getSize()),
				pageData.getTotalResults()));
		}
		return REPAIR;
	}



	/**
	 * Site for the Repair Form
	 * @return The view name for the add Repair form.
	 */
	@GetMapping("/addRepair")
	public String addRepair(){
		return "Repair/addRepair";
	}

	/**
	 * Handles the HTTP GET request to show the add materials form.
	 *
	 * @param model The Spring MVC model.
	 * @return The view name for the add materials form.
	 */
	@GetMapping("/repair-addMaterials")
	public String showAddMaterialsForm(Model model, @RequestParam(required = false) Integer size,
									   @RequestParam(required = false) Integer page,
									   @RequestParam(required = false) String searchTerm) {

		if (page == null) {
			page = 0;
		}
		if (size == null) {
			size = 5;
		}
		double dSize = size;
		double employeeAmount = inventory.findAllMaterials().toList().size();
		Integer maxPages = (int) Math.ceil(employeeAmount / dSize);
		model.addAttribute("page", page);
		model.addAttribute("maxPages", maxPages);
		model.addAttribute(MATERIALS_ATTRIBUTE, repairManagement.getMaterialsPage(page, size));
		model.addAttribute(MATERIALMAP_ATTRIBUTE, materialMap);
		model.addAttribute("repairObj", repairManagement.findRepairById(tempProductId));
		return "/Repair/addMaterials";
	}

	/**
	 * Handles the HTTP POST request to add a new repair.
	 *
	 * @param model        The Spring MVC model.
	 * @param email        The email associated with the repair.
	 * @param repairType   The type of repair (QUICK or NORMAL).
	 * @param customerName The name of the customer.
	 * @return The view name for redirecting to the repair page.
	 * @throws DocumentException If an error occurs during PDF document creation.
	 * @throws IOException       If an error occurs during file I/O operations.
	 * @throws WriterException   If an error occurs during QR code creation.
	 */
	@PostMapping("/repair/add")
	public String repairPost(
		Model model,
		@RequestParam("mail-address") String email,
		@RequestParam("repairType") RepairType repairType,
		@RequestParam("customerForename") String customerForename,
		@RequestParam("customerName") String customerName,
		@RequestParam("customerAddress") String customerAddress,
		@RequestParam("telephoneNumber") String telephoneNumber,
		@RequestParam("des") String description) throws DocumentException, IOException, WriterException {


		Repair repair = new Repair(email,
			Money.of(0, "EUR"),
			repairType,
			businessTime,
			customerForename,
			customerName,
			customerAddress,
			telephoneNumber,
			description);
		repairManagement.createStartPDF(repair);
		repairManagement.addRepair(repair);

		model.addAttribute("repairs", repairManagement.sortRepairsByDateAndFinished());
		model.addAttribute("inventory", inventory.findAllMaterials());
		String text;
		text =
				"Sehr geehrte(r) " + repair.getCustomerForename() + " " + repair.getCustomerName() + ",\n" +
				"\n" +
				"wir hoffen, dass es Ihnen gut geht. Wir möchten Ihnen mitteilen, " +
					"dass wir Ihre Anfrage zur Reparatur Ihrer Uhr erhalten haben und freuen uns, " +
					"Ihnen mitteilen zu können, dass wir uns darum kümmern werden.\n" +
				"\n" +
				"Auftragsdetails:\n" +
				"Kunde: " + repair.getCustomerForename() + " " + repair.getCustomerName() + "\n" +
				"Beschreibung des Problems: [Kurze Beschreibung des Problems]\n" +
				"Datum des Abgabe: " + repair.getFormattedDateTime(repair.getDate()) + "\n" +
				"\n" +
				"Unsere Uhrmacher und Techniker werden " +
					"Ihre Uhr sorgfältig überprüfen und die notwendigen Reparaturen durchführen," +
					" um sicherzustellen, dass sie bald wieder einwandfrei funktioniert.\n" +
				"\n" +
				"Wir streben danach, die Reparatur so schnell wie möglich abzuschließen. " +
					"Wir werden Sie über den Fortschritt auf dem Laufenden halten" +
					" und Sie rechtzeitig informieren, sobald Ihre Uhr fertig ist.\n" +
				"\n" +
				"Bitte zögern Sie nicht, uns bei Fragen oder zusätzlichen Anliegen zu kontaktieren." +
					" Wir schätzen Ihr Vertrauen in unsere Dienstleistungen und sind bestrebt, " +
					"Ihnen die bestmögliche Erfahrung zu bieten.\n" +
				"\n" +
				"Vielen Dank für Ihre Zusammenarbeit und " +
					"Geduld während des Reparaturprozesses.\n" +
				"\n" +
				"Mit freundlichen Grüßen,\n" +
				"\n" +
				"An Uhren Gmbh.\n" +
				"Clockshopstr. 86 \n" +
				"01187 Dresden";

		emailService.sendEmail(email, "Bestätigung der Reparatur für Ihre Uhr", text);
		return "redirect:/" + REPAIR_STR;
	}

	/**
	 * Handles the HTTP POST request to add materials to a repair.
	 *
	 * @param model    The Spring MVC model.
	 * @param repairId The product identifier of the repair.
	 * @return The view name for the add materials form.
	 */
	@PostMapping("/repair-addMaterials")
	public String addMaterials(
		Model model,
		@RequestParam("repairId") ProductIdentifier repairId) {
		materialMap.clear();
		model.addAttribute(MATERIALMAP_ATTRIBUTE, materialMap);
		model.addAttribute("repairObj", repairManagement.findRepairById(repairId));
		model.addAttribute(MATERIALS_ATTRIBUTE, inventory.findAllMaterials());
		tempProductId = repairId;

		return "redirect:/repair-addMaterials";
	}

	/**
	 * Handles the HTTP POST request to add material quantity to a repair.
	 *
	 * @param model     The Spring MVC model.
	 * @param articleId The product identifier of the material.
	 * @param quantity  The quantity of the material to be added.
	 * @return The view name for redirecting to the add materials form.
	 */
	@PostMapping("/repair/addMaterial")
	public String addMaterialQuantity(
		Model model,
		@RequestParam("articleId") ProductIdentifier articleId,
		@RequestParam("quantity") int quantity) {

		materialMap.put(articleId, quantity);

		model.addAttribute("inventory", inventory.findAllMaterials());
		model.addAttribute(MATERIALMAP_ATTRIBUTE, materialMap);

		return "redirect:/repair-addMaterials";
	}

	/**
	 * Handles the HTTP POST request to complete a repair.
	 *
	 * @param repairId The product identifier of the repair to be completed.
	 * @return The view name for redirecting to the repair page.
	 */
	@PostMapping("/repair/complete")
	public String completeRepair(@RequestParam("repairId") ProductIdentifier repairId,
								 Principal principal) throws DocumentException, IOException, WriterException {
		Repair repair = repairManagement.findRepairById(repairId);
		if (!repair.isFinished()) {
			repair.finish(businessTime);
			repairManagement.updateRepair(repair);
			String text;
			text =
				"Sehr geehrte(r) " + repair.getCustomerName() + ",\n" +
					"\n" +
					"wir hoffen, es geht Ihnen gut. Wir freuen uns, Ihnen mitteilen zu können," +
					" dass die Reparatur an Ihrer Uhr erfolgreich abgeschlossen wurde.\n" +
					"\n" +
					"Auftragsdetails:\n" +
					"Kunde: " + repair.getCustomerName() + "\n" +
					"Datum des Abgabe: " + repair.getFormattedDateTime(repair.getDate()) + "\n" +
					"Datum der Reparaturabschluss: " + repair.getFormattedDateTime(repair.getEndDate()) + "\n" +
					"\n" +
					"Unsere Uhrmacher und Techniker haben Ihre Uhr sorgfältig repariert und sie " +
					"wurde auf Herz und Nieren geprüft, um sicherzustellen, dass sie wieder einwandfrei funktioniert.\n" +
					"\n" +
					"Ihre Uhr ist jetzt abholbereit und kann zu den Öffnungszeiten unseres Geschäfts abgeholt werden." +
					" Falls andere Abholmodalitäten gewünscht sind, " +
					"lassen Sie es uns bitte wissen, damit wir entsprechende Vorkehrungen treffen können.\n" +
					"\n" +
					"Wir möchten uns für Ihr Vertrauen in unsere Dienstleistungen bedanken und hoffen, " +
					"dass Ihre Zufriedenheit mit der Reparatur unseren hohen Standards entspricht.\n" +
					"\n" +
					"Wenn Sie weitere Fragen haben oder Unterstützung benötigen, zögern Sie bitte nicht, " +
					"uns zu kontaktieren. Wir sind gerne bereit, Ihnen zu helfen.\n" +
					"\n" +
					"Wir freuen uns darauf, Sie in unserem Geschäft begrüßen zu dürfen, " +
					"um Ihre reparierte Uhr persönlich zu übergeben.\n" +
					"\n" +
					"Mit freundlichen Grüßen,\n" +
					"\n" +
					"An Uhren Gmbh.\n" +
					"Clockshopstr. 86 \n" +
					"01187 Dresden";

			repair.setOrderIdentifier(shopOrderManagement.completeRepairOrder(repair, materialMap, principal));
			repair.setPrice(shopOrderManagement.getOrder(repair.getOrderIdentifier()).getTotal());
			repairManagement.updateRepair(repair);
			repairManagement.createEndPDF(repair);
			emailService.sendEmail(repair.getEmail(), "Ihre Uhr ist abholbereit!", text);
			materialMap.clear();
		}

		return REDIRECT_REPAIR;
	}

	/**
	 * Handles the HTTP GET request to show the edit repair form.
	 *
	 * @param repairId The product identifier of the repair.
	 * @param model    The Spring MVC model.
	 * @return The view name for the edit repair form.
	 */
	@GetMapping("/editRepair")
	public String showEditRepairPage(Model model,
									@RequestParam("repairId") ProductIdentifier repairId) {
		Repair repair = repairManagement.findRepairById(repairId);
		model.addAttribute(REPAIR_STR, repair);
		return "Repair/editRepair";
	}


	/**
	 * Handles the HTTP GET request to show the repair cost configuration page.
	 *
	 * @param model The Spring MVC model.
	 * @return The view name for the repair cost configuration page.
	 */
	@PostMapping("/repairCostConfigure")
	public String configureRepairCost(Model model) {
		model.addAttribute("RepairType", RepairType.values());
		return "Repair/repairCostConfigure";
	}


	/**
	 * Handles the HTTP POST request to edit a repair.
	 *
	 * @param model         The Spring MVC model.
	 * @param repairId      The product identifier of the repair.
	 * @param customerName  The name of the customer.
	 * @param description   The description of the repair.
	 * @param email         The email of the customer.
	 * @return The view name for redirecting to the repair page.
	 */
	@PostMapping("edit_repair")
	public String editRepair(Model model,
							@RequestParam(value = "repairId") ProductIdentifier repairId,
							@RequestParam(value = "customerName", required = false) String customerName,
							@RequestParam(value = "description", required = false) String description,
							@RequestParam(value = "email", required = false) String email,
							@RequestParam(value = "repairType", required = false) RepairType repairType,
							@RequestParam(value = "costEstimate", required = false) Integer costEstimate) {
		Repair repair = repairManagement.findRepairById(repairId);
		if (repair != null) {
			if (customerName != null && !customerName.isEmpty()) {
				repair.setCustomerName(customerName);
			}
			if (description != null && !description.isEmpty()) {
				repair.setDescription(description);
			}
			if (email != null && !email.isEmpty()) {
				repair.setEmail(email);
			}
			if (repairType != null) {
				repair.setRepairType(repairType);
			}	
			if (costEstimate != null && costEstimate >= 0 ) {
				repair.setCostEstimate(costEstimate);
			}
			repairManagement.updateRepair(repair);
		}
		return REDIRECT_REPAIR;
	}


	/**
	 * Handles the HTTP POST request to save the configuration of repair costs.
	 *
	 * @param quickRepair The cost of a quick repair.
	 * @param normalRepair The cost of a normal repair.
	 * @param maintenanceRepair The cost of a maintenance repair.
	 * @param radioRepair The cost of a radio repair.
	 * @return The view name for redirecting to the repair page.
	 */
	@PreAuthorize("hasRole('BOSS')")
	@PostMapping("/finishConfiguration")
	public String saveConfiguration(@RequestParam(value = "quickRepair", required = false) Integer quickRepair,
									@RequestParam(value = "normalRepair", required = false) Integer normalRepair,
									@RequestParam(value = "maintenanceRepair", required = false) Integer maintenanceRepair,
									@RequestParam(value = "radioRepair", required = false) Integer radioRepair) {
	
		if (quickRepair != null) {
			RepairType.QUICK.setCost(quickRepair);
		}
		if (normalRepair != null) {
			RepairType.NORMAL.setCost(normalRepair);
		}
		if (maintenanceRepair != null) {
			RepairType.MAINTENANCE.setCost(maintenanceRepair);
		}
		if (radioRepair != null) {
			RepairType.RADIO.setCost(radioRepair);
		}

		return REDIRECT_REPAIR;
	}


	/**
	 * Handles the HTTP POST request to delete a repair.
	 *
	 * @param repairId The product identifier of the repair.
	 * @return The view name for redirecting to the repair page.
	 */
	@GetMapping("/deleteRepair")
	public String deleteRepair(@RequestParam("repairId") ProductIdentifier repairId) {
		repairManagement.deleteRepairById(repairId);
		return REDIRECT_REPAIR;
	}

	


}

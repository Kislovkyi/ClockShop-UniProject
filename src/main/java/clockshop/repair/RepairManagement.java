package clockshop.repair;

import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.extras.PageManager;
import com.google.zxing.WriterException;
import clockshop.extras.PDFManagement;
import com.itextpdf.text.*;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class for managing repairs, providing CRUD operations and additional functionalities.
 * This class uses a RepairRepository for data access and a BusinessTime instance for timestamping.
 */
@Service
@Qualifier
public class RepairManagement implements PageManager<Repair> {

	private final RepairRepository repairRepository;
	private final BusinessTime businessTime;
	private final ShopInventoryManagement inventory;


	private final PDFManagement pdfManagement;

	/**
	 * Constructs a new instance of RepairManagement.
	 *
	 * @param repairRepository   The repository for managing repair entities.
	 * @param businessTime       The business time service.
	 * @param inventory          The inventory management system.
	 * @param pdfManagement      The PDF management system.
	 */
	RepairManagement(RepairRepository repairRepository,
					 BusinessTime businessTime,
					 ShopInventoryManagement inventory, PDFManagement pdfManagement) {
		this.inventory = inventory;
		this.pdfManagement = pdfManagement;
		this.repairRepository = repairRepository;
		this.businessTime = businessTime;
	}
	/**
	 * Adds a new repair to the system.
	 *
	 * @param repair The repair object to be added. Must not be null.
	 * @return The added repair entity.
	 */
	public Repair addRepair(Repair repair) {
		Assert.notNull(repair, "Repair must not be null!");
		var email = repair.getEmail();
		var customerForename = repair.getCustomerForename();
		var customerName = repair.getCustomerName();
		var repairType = repair.getRepairType();
		var price = repair.getPrice();
		var address = repair.getCustomerAddress();
		var telephoneNumber = repair.getTelephoneNumber();
		var description = repair.getDescription();
		return repairRepository.save(new Repair(email,
			price,
			repairType,
			businessTime,
			customerForename,
			customerName,
			address,
			telephoneNumber,
			description));
	}

	/**
	 * Deletes an existing repair from the system.
	 *
	 * @param repair The repair object to be deleted. Must not be null.
	 */
	public void deleteRepair(Repair repair) {
		repairRepository.delete(repair);
	}


	/**
	 * Searches {@link RepairRepository} for Repair's CustomerNames matching the searchTerm
	 * OldName: searchRepairsByCustomerName
	 * @param searchTerm {@link String}
	 * @return {@link List<Repair>}
	 */

	public List<Repair> search(String searchTerm) {
		return repairRepository.findByCustomerNameContainingIgnoreCase(searchTerm);
	}


	public Page<Repair> getRepairPage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<Repair> employeeList = StreamSupport
			.stream(RepairManagement.this
				.sortRepairsByDateAndFinished()
				.spliterator(), false)
			.collect(Collectors.toList());

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<Repair> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	public Page<ShopInventoryItem> getMaterialsPage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<ShopInventoryItem> employeeList = inventory.findAllMaterials().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<ShopInventoryItem> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	/**
	 * Finds a repair entity by its product identifier.
	 *
	 * @param id The product identifier of the repair entity to be found.
	 * @return The found repair entity or null if not found.
	 */
	public Repair findRepairById(ProductIdentifier id) {
		for (Repair repair : repairRepository.findAll()) {
			if (repair != null) {
				ProductIdentifier repairId = repair.getId();
				if (repairId != null && repairId.equals(id)) {
					return repair;
				}
			}
		}
		return null;
	}

	/**
	 * Updates an existing repair in the system.
	 *
	 * @param repair The repair object to be updated. Must not be null.
	 */
	public void updateRepair(Repair repair) {
		repairRepository.save(repair);
	}


	/**
	 * Retrieves all repairs currently in the system.
	 *
	 * @return A streamable collection of all repair entities.
	 */
	public Streamable<Repair> findAll() {
		return repairRepository.findAll();
	}

	/**
	 * Retrieves all repairs sorted by date and finished status.
	 *
	 * @return A streamable collection of repairs sorted by quick, normal, and finished categories.
	 */
	public Streamable<Repair> sortRepairsByDateAndFinished() {
		List<Repair> quickRepairs = new ArrayList<>();
		List<Repair> normalRepairs = new ArrayList<>();
		List<Repair> finishedRepairs = new ArrayList<>();
		List<Repair> sortedRepairs = new ArrayList<>();
		for (Repair repair : repairRepository.findAll()) {
			if (repair.getRepairType() == RepairType.QUICK) {
				quickRepairs.add(repair);
			} else {
				normalRepairs.add(repair);
			}
		}
		List<Repair> toRemoveQuick = new ArrayList<>();
		for (Repair repair : quickRepairs) {
			if (repair.isFinished()) {
				finishedRepairs.add(repair);
				toRemoveQuick.add(repair);
			}
		}
		quickRepairs.removeAll(toRemoveQuick);

		List<Repair> toRemoveNormal = new ArrayList<>();
		for (Repair repair : normalRepairs) {
			if (repair.isFinished()) {
				finishedRepairs.add(repair);
				toRemoveNormal.add(repair);
			}
		}
		normalRepairs.removeAll(toRemoveNormal);

		sortedRepairs.addAll(quickRepairs);
		sortedRepairs.addAll(normalRepairs);
		sortedRepairs.addAll(finishedRepairs);

		return Streamable.of(sortedRepairs);
	}

	
	/**
	 * Creates a PDF document for the start of a repair.
	 *
	 * @param repair The Repair object for which the PDF is created. Must not be null.
	 * @throws DocumentException If there is an error creating the document.
	 * @throws IOException If there is an I/O error.
	 * @throws WriterException If there is an error writing to the document.
	 */
	public void createStartPDF(Repair repair) throws DocumentException, IOException, WriterException {
		pdfManagement.pdfStartRepair(repair);
	}

	/**
	 * Creates a PDF document for the end of a repair.
	 *
	 * @param repair The Repair object for which the PDF is created. Must not be null.
	 * @throws DocumentException If there is an error creating the document.
	 * @throws IOException If there is an I/O error.
	 * @throws WriterException If there is an error writing to the document.
	 */
	public void createEndPDF(Repair repair) throws DocumentException, IOException, WriterException {
		pdfManagement.pdfEndRepair(repair);
		}
	
	/**
	 * Deletes a repair by its product identifier.
	 *
	 * @param id The product identifier of the repair to be deleted. Must not be null.
	 */
	public void deleteRepairById(ProductIdentifier id) {
		repairRepository.deleteById(id);
	}	





}

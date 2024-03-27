package clockshop.maintenance;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.extras.PDFManagement;
import clockshop.extras.PageManager;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceManagement implements PageManager<MaintenanceContract> {
	private final MaintenanceRepository maintenanceRepository;
	private final transient BusinessTime businessTime;
	private final ShopAccountancyManagement shopAccountancyManagement;
	private final PDFManagement pdfManagement;

	/**
	 * Constructs a new instance of MaintenanceManagement with the provided maintenance repository, business time,
	 * shop accountancy management, and PDF management.
	 *
	 * @param maintenanceRepository The repository for managing maintenance-related data.
	 * @param businessTime The business time utility used for time-related operations.
	 * @param shopAccountancyManagement The management system for handling shop accountancy operations.
	 * @param pdfManagement The management system for handling PDF-related operations.
	 */
	public MaintenanceManagement(MaintenanceRepository maintenanceRepository,
                                 BusinessTime businessTime,
                                 ShopAccountancyManagement shopAccountancyManagement, PDFManagement pdfManagement) {
		this.maintenanceRepository = maintenanceRepository;
		this.businessTime = businessTime;
		this.shopAccountancyManagement = shopAccountancyManagement;
        this.pdfManagement = pdfManagement;
    }

	/**
	 * adds a new Contract to the Repository and creates a pdf bill
	 *
	 * @param maintenanceContract that is added
	 */
	public void addContract(MaintenanceContract maintenanceContract)
		throws DocumentException, IOException, WriterException {
		maintenanceRepository.save(maintenanceContract);
		pdfManagement.pdfMaintenanceContract(maintenanceContract);
	}

	/**
	 * deletes existing Contract
	 *
	 * @param id of the to be deleted contract
	 */
	public void deleteMaintenanceById(UUID id) {
		maintenanceRepository.findAll().forEach(item -> {
			if (item.getId().equals(id)) {
				maintenanceRepository.delete(item);
			}
		});
	}

	/**
	 * creates Accountancy entry to add the monthly money from the contract
	 */
	public void payContract() {

		for (MaintenanceContract entry : maintenanceRepository.findAll()) {
			shopAccountancyManagement.payContract(entry.getPrice(),
				"%s Maintenance %s".formatted(entry.getCompany(),
					entry.getPrice()),
				entry.getCompany(),
				entry.getContactPerson(),
				entry.getAddress());
		}
	}

	/**
	 * Searches {@link MaintenanceRepository} for {@link MaintenanceContract}'s Contact-Persons matching the searchTerm
	 * OldName: searchContractsByContactPerson
	 * @param searchTerm any {@link String}
	 * @return {@link List<MaintenanceContract>} of {@link MaintenanceContract}
	 */
	public List<MaintenanceContract> search(String searchTerm) {
		return maintenanceRepository.findByContactPersonContainingIgnoreCase(searchTerm);
	}

	/**
	 * @return all Maintenance Contracts
	 */
	@Override
	public Streamable<MaintenanceContract> findAll() {
		return  maintenanceRepository.findAll();
	}

	/**
	 * @param page number of page
	 * @param size length of page
	 * @return template
	 */
	public Page<MaintenanceContract> getMaintenancePage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<MaintenanceContract> maintenanceContractListList = maintenanceRepository.findAll().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),maintenanceContractListList.size());

		List<MaintenanceContract> pageContent = maintenanceContractListList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, maintenanceContractListList.size());
	}
}

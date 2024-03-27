package clockshop.maintenance;

import clockshop.accountancy.ShopAccountancyManagement;
import clockshop.extras.PDFManagement;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link MaintenanceManagement}
 */
class MaintenanceManagementTest {
	@Mock
	private MaintenanceRepository maintenanceRepository;

	@Mock
	private BusinessTime businessTime;

	@Mock
	private ShopAccountancyManagement shopAccountancyManagement;

	@Mock
	private PDFManagement pdfManagement;

	private MaintenanceManagement maintenanceManagement;
	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		maintenanceManagement = new MaintenanceManagement(maintenanceRepository, businessTime, shopAccountancyManagement,pdfManagement);
	}

	@Test
	void addContractTest() throws DocumentException, IOException, WriterException {
		MaintenanceContract maintenanceContract = new MaintenanceContract("An Uhren", 10, 10, businessTime, Money.of(10, "EUR"),"Bernd","BerndAllee");
		maintenanceManagement.addContract(maintenanceContract);

		verify(maintenanceRepository,times(1)).save(any());
	}

	@Test
	void deleteMaintenanceByIdTest(){
		MaintenanceContract maintenanceContract = new MaintenanceContract("An Uhren", 10, 10, businessTime, Money.of(10, "EUR"),"Bernd", "Berndallee");

		when(maintenanceRepository.findAll()).thenReturn(Streamable.of(maintenanceContract));

		maintenanceManagement.deleteMaintenanceById(maintenanceContract.getId());

		verify(maintenanceRepository,times(1)).delete(any());
	}

	@Test
	void deleteMaintenanceByIdWhenIDsDontMatchTest(){
		MaintenanceContract maintenanceContract = new MaintenanceContract("An Uhren", 10, 10, businessTime, Money.of(10, "EUR"),"Bernd", "Berndalee");

		when(maintenanceRepository.findAll()).thenReturn(Streamable.of(maintenanceContract));

		maintenanceManagement.deleteMaintenanceById(UUID.randomUUID());

		verify(maintenanceRepository,times(0)).delete(any());
	}

	@Test
	void payContractTest(){
		MaintenanceContract maintenanceContract = new MaintenanceContract("An Uhren", 10, 10, businessTime, Money.of(10, "EUR"),"Bernd","BerndAlee");

		when(maintenanceRepository.findAll()).thenReturn(Streamable.of(maintenanceContract));

		maintenanceManagement.payContract();

		verify(shopAccountancyManagement,times(1)).payContract(any(),any(),any(),any(),any());
	}
}

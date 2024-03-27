package clockshop.maintenance;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Streamable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Tests for {@link MaintenanceController}
 */
class MaintenanceControllerTest {

	@Mock
	private MaintenanceRepository maintenanceRepository;

	@Mock
	private MaintenanceManagement maintenanceManagement;

	@Mock
	private Model model;

	@InjectMocks
	private MaintenanceController maintenanceController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSubmitMaintenanceContract() throws DocumentException, IOException, WriterException {
		// Mocking data
		String company = "Test Company";
		int towerQuantity = 2;
		int buildingQuantity = 3;
		int price = 100;
		int price2 = 50;
		String address = "Bernweg";
		String contactPerson = "Bernd";
		// Stubbing
		when(model.addAttribute(any(), any())).thenReturn(model);

		// Perform action
		String result = maintenanceController.submit(model, company, towerQuantity, buildingQuantity, null, null, price, price2,contactPerson,address);

		// Verify behavior
		verify(maintenanceManagement, times(1)).addContract(any(MaintenanceContract.class));
		verify(model, times(1)).addAttribute(any(), any());
		// Add additional assertions as needed based on the behavior you expect
	}

	@Test
	void testMaintenanceGetEndpoint() {
		// Stubbing
		when(maintenanceRepository.findAll()).thenReturn(Streamable.empty());
		when(maintenanceManagement.findAll()).thenReturn(Streamable.empty());

		// Perform action
		String result = maintenanceController.maintenance(null, null, null,1,1,"" ,model);

		// Verify behavior
		verify(model, times(3)).addAttribute(any(), any());
		// Add additional assertions as needed based on the behavior you expect
	}

	@Test
	void testUpdateMaintenance() {
		UUID id = UUID.randomUUID();

		when(maintenanceRepository.findAll()).thenReturn(Streamable.empty());

		String result = maintenanceController.updateMaintenance(model, id);

		verify(maintenanceManagement, times(1)).deleteMaintenanceById(id);
		verify(model, times(1)).addAttribute(any(), any());
	}

	@Test
	void maintenanceTest(){

		MaintenanceContract maintenanceContract = mock(MaintenanceContract.class);

		when(maintenanceRepository.findByContactPersonContainingIgnoreCase(any())).thenReturn(Streamable.of(maintenanceContract).toList());

		String result = maintenanceController.maintenance("Keule World", 5, 5, 5,0, "Louis", model);


		verify(model,times(3)).addAttribute(any(),any());
	}

	@Test
	void maintenancePageNullTest(){

		MaintenanceContract maintenanceContract = mock(MaintenanceContract.class);

		when(maintenanceRepository.findByContactPersonContainingIgnoreCase(any())).thenReturn(Streamable.of(maintenanceContract).toList());
		when(maintenanceRepository.findAll()).thenReturn(Streamable.of(maintenanceContract));
		when(maintenanceManagement.findAll()).thenReturn(Streamable.empty());

		String result = maintenanceController.maintenance("Keule World", 5, 5, null,null, null, model);
		String result2 = maintenanceController.maintenance("Keule World", 5, 5, null,-1, null, model);

		verify(model,times(6)).addAttribute(any(),any());

	}

	@Test
	void addTest(){
		assertEquals("Maintenance/addMaintenance",maintenanceController.add());
	}

}

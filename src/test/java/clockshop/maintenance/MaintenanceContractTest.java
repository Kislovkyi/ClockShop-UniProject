package clockshop.maintenance;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.salespointframework.time.BusinessTime;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link MaintenanceContract}
 */
class MaintenanceContractTest {

	MaintenanceRepository maintenanceRepository;
	@Mock
	private MaintenanceContract maintenanceContract;
	BusinessTime businessTime;
	@BeforeEach
	public void setUp(){
		String company = "FelixTopEinsInMain";
		Integer towerQuantity = 1;
		Integer buildingQuantity = 1;
		Money price = Money.of(5, "EUR");
		String contactPerson = "Bernd";
		String address = "Dummkopf Allee 24";
		maintenanceContract = new MaintenanceContract(company, towerQuantity, buildingQuantity, businessTime, price, contactPerson, address);

	}
	@Test
	void testConstructor(){
		assertEquals("FelixTopEinsInMain", maintenanceContract.getCompany());
		assertEquals(1, maintenanceContract.getTowerQuantity());
		assertEquals(1, maintenanceContract.getBuildingQuantity());
		assertEquals(Money.of(5, "EUR"), maintenanceContract.getPrice());
	}
	@Test
	void testEmptyConstructor(){
		MaintenanceContract emptyMaintenanceContract = new MaintenanceContract();
		assertNotNull(emptyMaintenanceContract);
	}
	@Test
	void testSetPrice(){
		maintenanceContract.setPrice(Money.of(6, "EUR"));
		assertEquals(Money.of(6, "EUR"), maintenanceContract.getPrice());
	}
	@Test
	void testHashCodeAndEquals() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		MaintenanceContract.ContractIdentifier identifier1 = new MaintenanceContract.ContractIdentifier(uuid1);
		MaintenanceContract.ContractIdentifier identifier2 = new MaintenanceContract.ContractIdentifier(uuid1);
		MaintenanceContract.ContractIdentifier identifier3 = new MaintenanceContract.ContractIdentifier(uuid2);
		MaintenanceContract.ContractIdentifier identifier4 = new MaintenanceContract.ContractIdentifier(null);

		assertEquals(31, identifier4.hashCode());
		assertNotEquals(identifier1, identifier3);
		assertEquals(identifier1.hashCode(), identifier2.hashCode());
		assertNotEquals(identifier1.hashCode(), identifier3.hashCode());
	}

	@Test
	void getIdTest(){
		MaintenanceContract maintenanceContract = new MaintenanceContract();
		assertNotNull(maintenanceContract.getId());
	}

}

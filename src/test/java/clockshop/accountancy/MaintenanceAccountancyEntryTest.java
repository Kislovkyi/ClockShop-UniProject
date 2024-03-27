package clockshop.accountancy;

import clockshop.maintenance.MaintenanceContract;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.accountancy.AccountancyEntry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link MaintenanceAccountancyEntry}
 */
class MaintenanceAccountancyEntryTest {

	private MaintenanceAccountancyEntry accountancyEntry;
	private MaintenanceContract contract;

	@BeforeEach
	void setUp(){
	}


	@Test
	void emptyMaintenanceAccountancyEntryConstructorTest(){
		MaintenanceAccountancyEntry empty = new MaintenanceAccountancyEntry();
		assertNotNull(empty);
	}

}
package clockshop.maintenance;

import org.javamoney.moneta.Money;
import org.jboss.logging.Logger;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Component;

import static org.salespointframework.core.Currencies.EURO;

/**
 * initializes Maintenance contracts
 */
@Component
public class MaintenanceDataInitializer implements DataInitializer {

	private static final Logger LOG = Logger.getLogger(MaintenanceDataInitializer.class);
	private final MaintenanceRepository maintenanceRepository;
	private BusinessTime businessTime;

	MaintenanceDataInitializer(MaintenanceRepository maintenanceRepository){
		this.maintenanceRepository = maintenanceRepository;
	}

	@Override
	public void initialize(){
		LOG.info("creating example MaintenanceObjects");

			maintenanceRepository.save(new MaintenanceContract("An's Glockshop",
				1,
				2,
				businessTime,
				Money.of(100, EURO),
				"An",
				"Glock Street 12"));

			maintenanceRepository.save(new MaintenanceContract("DariusIntGaming",
				0,
				2,
				businessTime,
				Money.of(150, EURO),
				"Darius",
				"irgendwo Südvorstadt"));
	}
}

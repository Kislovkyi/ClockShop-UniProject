package clockshop.repair;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * Data initializer for creating initial repair data in the system.
 * This class implements the DataInitializer interface for automatic data initialization during the application startup.
 */
@Component
public class RepairDataInitializer implements DataInitializer {

	private final RepairManagement repairManagement;
	private final BusinessTime businessTime;


	/**
	 * Constructor for the RepairDataInitializer class.
	 *
	 * @param repairManagement The RepairManagment service to manage repairs.
	 * @param businessTime The BusinessTime instance to manage business time.
	 */
	RepairDataInitializer(RepairManagement repairManagement, BusinessTime businessTime) {
		Assert.notNull(repairManagement, "RepairManagment must not be null!");
		this.repairManagement = repairManagement;
		this.businessTime = businessTime;
	}

	/**
	 * Initializes repair data in the system if no repairs are currently present.
	 * Creates sample repair instances and adds them to the system using the RepairManagment service.
	 */
	@Override
	public void initialize() {
		if (repairManagement.findAll().toList().isEmpty()) {

			MonetaryAmount priceQuick = Money.of(RepairType.QUICK.getCost(), "EUR");
			MonetaryAmount priceNormal = Money.of(RepairType.NORMAL.getCost(), "EUR");
			Repair repair = new Repair("uhrenladenswt@gmail.com",
				priceQuick,
				RepairType.QUICK,
				businessTime,
				"Willi",
				"First Repair",
				"Uhrenladenstr. 86",
				"0153 25421342",
				"Uhr stinkt nach Maggi");
			repairManagement.addRepair(repair);
			List.of(
				new Repair("1@gmail.com",
					priceNormal,
					RepairType.NORMAL,
					businessTime,
					"Willi",
					"First Customer",
					"Bayreutherstr. 14A",
					"0163 2519314",
					"Felix sucht die Main"),
				new Repair("2@gmail.com",
					priceQuick,
					RepairType.QUICK,
					businessTime,
					"Waldo",
					"Second Customer",
					"Bundestag",
					"0351 252 234",
					"Tobias hat den Mathebären verprügelt"),
				new Repair(
					"3@gmail.com", 
					priceNormal, 
					RepairType.RADIO, 
					businessTime,
					"AN",
					"Third Customer", 
					"Address 3", 
					"0163 2519315", 
					"Description 3"),
				new Repair(
					"4@gmail.com", 
					priceQuick, 
					RepairType.MAINTENANCE, 
					businessTime,
					"Darius",
					"Fourth Customer", 
					"Address 4", 
					"0163 2519316", 
					"Description 4"),
				new Repair(
					"5@gmail.com", 
					priceNormal, 
					RepairType.MAINTENANCE, 
					businessTime,
					"Steve",
					"Fifth Customer", 
					"Address 5", 
					"0163 2519317", 
					"Description 5"
				)

			).forEach(repairManagement::addRepair);
		}
	}
}

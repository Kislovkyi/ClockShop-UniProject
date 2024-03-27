package clockshop.time;

import clockshop.accountancy.StatisticsManagement;
import clockshop.maintenance.MaintenanceManagement;
import clockshop.order.ShopOrderManagement;
import org.salespointframework.time.BusinessTime;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TimedEventListener {

	private final ShopOrderManagement shopOrderManagement;
	private final MaintenanceManagement maintenanceManagement;

	private final StatisticsManagement statisticsManagement;


	/**
	 * @param shopOrderManagement service class
	 * @param maintenanceManagement service class
	 * @param statisticsManagement service class
	 * listens to TimeEvents
	 */
	public TimedEventListener(ShopOrderManagement shopOrderManagement,
                              MaintenanceManagement maintenanceManagement, StatisticsManagement statisticsManagement) {
		this.shopOrderManagement = shopOrderManagement;
		this.maintenanceManagement = maintenanceManagement;
        this.statisticsManagement = statisticsManagement;
    }

	@EventListener
	void dayPassed(BusinessTime.DayHasPassed event) {
		//no events yet
	}

	@EventListener
	void monthPassed(BusinessTime.MonthHasPassed event) {
		maintenanceManagement.payContract();
		shopOrderManagement.payAllSalary();
		statisticsManagement.findNonSellers();

	}
}

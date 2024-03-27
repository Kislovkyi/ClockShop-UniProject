package clockshop.time;

import clockshop.accountancy.StatisticsManagement;
import clockshop.maintenance.MaintenanceManagement;
import clockshop.order.ShopOrderManagement;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.time.BusinessTime;

import static org.mockito.Mockito.*;


/**
 * Tests for {@link TimedEventListener}
 */
class TimedEventListenerTest {

	@Test
	void monthPassedShouldPayContractAndSalaries() {
		ShopOrderManagement shopOrderManagementMock = mock(ShopOrderManagement.class);
		MaintenanceManagement maintenanceManagementMock = mock(MaintenanceManagement.class);
		StatisticsManagement statisticsManagementMock = mock(StatisticsManagement.class);

		TimedEventListener timedEventListener = new TimedEventListener(shopOrderManagementMock, maintenanceManagementMock, statisticsManagementMock);

		BusinessTime.MonthHasPassed event = Mockito.mock(BusinessTime.MonthHasPassed.class);

		// Act
		timedEventListener.monthPassed(event);

		// Assert
		verify(maintenanceManagementMock, times(1)).payContract();
		verify(shopOrderManagementMock, times(1)).payAllSalary();
		verifyNoMoreInteractions(shopOrderManagementMock, maintenanceManagementMock);
	}

	@Test
	void dayPassedShouldHaveNoInteractions(){
		ShopOrderManagement shopOrderManagementMock = mock(ShopOrderManagement.class);
		MaintenanceManagement maintenanceManagementMock = mock(MaintenanceManagement.class);
		StatisticsManagement statisticsManagementMock = mock(StatisticsManagement.class);

		TimedEventListener timedEventListener = new TimedEventListener(shopOrderManagementMock, maintenanceManagementMock, statisticsManagementMock);

		BusinessTime.DayHasPassed event = Mockito.mock(BusinessTime.DayHasPassed.class);

		timedEventListener.dayPassed(event);
		verifyNoMoreInteractions(shopOrderManagementMock, maintenanceManagementMock);
	}
}

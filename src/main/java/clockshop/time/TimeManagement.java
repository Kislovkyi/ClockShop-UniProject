package clockshop.time;

import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Service;

@Service
public class TimeManagement {
	private final BusinessTime businessTime;

	public TimeManagement(BusinessTime businessTime) {
		this.businessTime = businessTime;
	}

	/**
	 * @return converted String to Time
	 */
	public String dateString() {
		return businessTime.getTime().getDayOfMonth() + "." +
			businessTime.getTime().getMonthValue() + "." + businessTime.getTime().getYear();
	}

	/**
	 * Retrieves the current time as a formatted string.
	 *
	 * @return A string representing the current time in the format "HH:mm".
	 */
	public String timeString() {
		return businessTime.getTime().getHour() + ":" + businessTime.getTime().getMinute();
	}
}

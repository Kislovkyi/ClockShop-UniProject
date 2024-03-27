package clockshop.time;

import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;

@Controller
public class TimeController {
	private final BusinessTime businessTime;

	public TimeController(BusinessTime businessTime) {
		this.businessTime = businessTime;
	}

	/**
	 * Skips time 30 days forward
	 *
	 * @param time will never be {@literal null}.
	 * @return the template name.
	 */

	@GetMapping("/time-management")
	public String timeSkip(@RequestParam int time) {
		//Without those loops TimeEventListener won't trigger
		switch (time) {
			case 3600:
				businessTime.forward(Duration.ofSeconds(3600));
				break;
			case 86400:
				for (int i = 0; i < 24; i++) {
					businessTime.forward(Duration.ofSeconds(3600));
				}
				break;
			case 2592000:
				for (int i = 0; i < 24 * 30; i++) {
					businessTime.forward(Duration.ofSeconds(3600));
				}
				break;
			case 31536000:
				for (int i = 0; i < 24 * 365; i++) {
					businessTime.forward(Duration.ofSeconds(3600));
				}
				break;
			default:
				businessTime.forward(Duration.ofSeconds(time));
				break;
		}
		return "redirect:/accounting";
	}

}

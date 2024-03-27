package clockshop.grandfatherclock;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import static org.salespointframework.core.Currencies.EURO;

@Component
public class GFCInitializer implements DataInitializer {

	private final GFCManagement gfcManagement;


	public GFCInitializer(GFCManagement gfcManagement) {
		this.gfcManagement = gfcManagement;
	}

	@Override
	public void initialize() {
		/*
		gfcManagement.addClock("LiegeUhr",
			100,
			"LOL",
			"StandUhr im Liegen",
			0.5);


		 */

	}
}

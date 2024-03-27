package clockshop;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	ApplicationModules modules = ApplicationModules.of(Application.class);


	@Test
	void verifyApplicationStartTest() {
		Application.main(new String[]{});
	}
}
package clockshop.grandfatherclock;

import clockshop.repair.RepairController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link StatusType}
 */
class StatusTypeTest {

	@Test
	void testStatusTypeValues() {
		assertEquals(4, StatusType.values().length);
		assertEquals(StatusType.SELECTED, StatusType.valueOf("SELECTED"));
		assertEquals(StatusType.ORDERED, StatusType.valueOf("ORDERED"));
		assertEquals(StatusType.READY, StatusType.valueOf("READY"));
		assertEquals(StatusType.FINISHED, StatusType.valueOf("FINISHED"));
	}

}

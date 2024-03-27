package clockshop.repair;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepairTypeTest {

	@Test
	void testEnumValues() {

		RepairType quick = RepairType.QUICK;
		RepairType normal = RepairType.NORMAL;

		assertEquals("QUICK", quick.name());
		assertEquals("NORMAL", normal.name());

		assertEquals(RepairType.QUICK, RepairType.valueOf("QUICK"));
		assertEquals(RepairType.NORMAL, RepairType.valueOf("NORMAL"));

		assertEquals(4, RepairType.values().length);
		assertArrayEquals(new RepairType[]{RepairType.QUICK, RepairType.NORMAL,RepairType.MAINTENANCE,RepairType.RADIO}, RepairType.values());
	}
}

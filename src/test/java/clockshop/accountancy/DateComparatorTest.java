package clockshop.accountancy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.salespointframework.accountancy.AccountancyEntry;

import java.time.LocalDateTime;
import java.util.Optional;
/**
 * Tests for {@link clockshop.accountancy.ShopAccountancyManagement.DateComparator}
 */
class DateComparatorTest {

	@Test
	void compareTest_FirstDateBeforeSecondDate() {
		// Given
		LocalDateTime earlierDateTime = LocalDateTime.of(2023, 1, 1, 10, 0);
		LocalDateTime laterDateTime = LocalDateTime.of(2023, 1, 2, 10, 0);

		AccountancyEntry entryA = mock(AccountancyEntry.class);
		when(entryA.getDate()).thenReturn(Optional.of(earlierDateTime));

		AccountancyEntry entryB = mock(AccountancyEntry.class);
		when(entryB.getDate()).thenReturn(Optional.of(laterDateTime));

		ShopAccountancyManagement.DateComparator dateComparator = new ShopAccountancyManagement.DateComparator();

		// When
		int result = dateComparator.compare(entryA, entryB);

		// Then
		assertEquals(1, result, "Expected entryA to come before entryB");
	}

	@Test
	void compareTest_SecondDateBeforeFirstDate() {
		// Given
		LocalDateTime earlierDateTime = LocalDateTime.of(2023, 1, 1, 10, 0);
		LocalDateTime laterDateTime = LocalDateTime.of(2023, 1, 2, 10, 0);

		AccountancyEntry entryA = mock(AccountancyEntry.class);
		when(entryA.getDate()).thenReturn(Optional.of(laterDateTime));

		AccountancyEntry entryB = mock(AccountancyEntry.class);
		when(entryB.getDate()).thenReturn(Optional.of(earlierDateTime));

		ShopAccountancyManagement.DateComparator dateComparator = new ShopAccountancyManagement.DateComparator();

		// When
		int result = dateComparator.compare(entryA, entryB);

		// Then
		assertEquals(-1, result, "Expected entryB to come before entryA");
	}

	@Test
	void compareTest_SameDates() {
		// Given
		LocalDateTime sameDateTime = LocalDateTime.of(2023, 1, 1, 10, 0);

		AccountancyEntry entryA = mock(AccountancyEntry.class);
		when(entryA.getDate()).thenReturn(Optional.of(sameDateTime));

		AccountancyEntry entryB = mock(AccountancyEntry.class);
		when(entryB.getDate()).thenReturn(Optional.of(sameDateTime));

		ShopAccountancyManagement.DateComparator dateComparator = new ShopAccountancyManagement.DateComparator();

		// When
		int result = dateComparator.compare(entryA, entryB);

		// Then
		assertEquals(0, result, "Expected both entries to have the same date");
	}
}

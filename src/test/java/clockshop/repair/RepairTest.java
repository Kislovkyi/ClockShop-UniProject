package clockshop.repair;

import clockshop.time.TimeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;

import javax.money.Monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.javamoney.moneta.Money;

/**
 * Tests for {@link Repair}
 */
class RepairTest {
	private Repair repair;

	@BeforeEach
	void setUp(){
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		when(businessTimeMock.getTime()).thenReturn(LocalDateTime.now());

		repair = new Repair("test@example.com",
			Money.of(50, Monetary.getCurrency("EUR")),
			RepairType.NORMAL,
			businessTimeMock,
			"John",
			"Doe",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");
	}
	@Test
	void testConstructorInitialization() {


		assertNotNull(repair);
		assertEquals("test@example.com", repair.getEmail());
		assertEquals("Doe", repair.getCustomerName());
		assertEquals(RepairType.NORMAL, repair.getRepairType());
		assertNotNull(repair.getDate());
	}

	@Test
	void testFinishMethod() {

		BusinessTime businessTimeMock = mock(BusinessTime.class);
		LocalDateTime currentDate = LocalDateTime.now();
		when(businessTimeMock.getTime()).thenReturn(currentDate);


		repair.finish(businessTimeMock);

		assertTrue(repair.isFinished());
	}

	@Test
	void testIsFinishedMethod() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		when(businessTimeMock.getTime()).thenReturn(LocalDateTime.now());
		assertFalse(repair.isFinished());
		repair.finish(businessTimeMock);
		assertTrue(repair.isFinished());
	}

	@Test
	void GetDurationInMinutesWhenEndDateNullTest() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		when(businessTimeMock.getTime()).thenReturn(null);
		repair.finish(businessTimeMock);
		assertFalse(repair.isFinished());
		assertEquals(0, repair.getDurationInMinutes());
	}

	@Test
	void isRadioTest(){
		assertFalse(repair.isRadio());
		repair.setRepairType(RepairType.RADIO);
		assertTrue(repair.isRadio());
	}


	@Test
	void testGetters() {

		BusinessTime businessTimeMock = mock(BusinessTime.class);
		LocalDateTime currentDate = LocalDateTime.now();
		when(businessTimeMock.getTime()).thenReturn(currentDate);

		Repair repairTime = new Repair("another@example.com",
			Money.of(30, Monetary.getCurrency("USD")),
			RepairType.NORMAL,
			businessTimeMock,
			"LMAO",
			"Doe",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");


		assertEquals("test@example.com", repair.getEmail());
		assertEquals(RepairType.NORMAL, repair.getRepairType());
		assertEquals(currentDate, repairTime.getDate());
		assertEquals("John", repair.getCustomerForename());
		assertEquals("Doe", repair.getCustomerName());
		assertEquals("In Progress", repair.getStatus());
	}

	@Test
	void getterTests(){

		// STATUS
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		when(businessTimeMock.getTime()).thenReturn(LocalDateTime.now());
		assertEquals("In Progress", repair.getStatus());
		repair.finish(businessTimeMock);
		assertEquals("Finished", repair.getStatus());

		// Duration
		LocalDateTime currentDate = LocalDateTime.now();
		when(businessTimeMock.getTime()).thenReturn(currentDate, currentDate.plusMinutes(1));
		repair.finish(businessTimeMock);
		assertTrue(repair.isFinished());
		assertEquals(1, repair.getDurationInMinutes());

	}

	@Test
	void getterSetterTests(){
		// email
		assertEquals("test@example.com",repair.getEmail());
		repair.setEmail("PEST@example.com");
		assertEquals("PEST@example.com",repair.getEmail());
		// price
		assertEquals(Money.of(50, Monetary.getCurrency("EUR")),repair.getPrice());
		repair.setPrice(Money.of(100, Monetary.getCurrency("EUR")));
		assertEquals(Money.of(100, Monetary.getCurrency("EUR")),repair.getPrice());
		// Type
		assertEquals(RepairType.NORMAL,repair.getRepairType());
		repair.setRepairType(RepairType.QUICK);
		assertEquals(RepairType.QUICK,repair.getRepairType());
		//customerForename
		assertEquals("John",repair.getCustomerForename());
		// setter not used
		//customerName
		assertEquals("Doe",repair.getCustomerName());
		repair.setCustomerName("Schmoe");
		assertEquals("Schmoe",repair.getCustomerName());
		//customerAddress
		assertEquals("JustusBummStr. 84",repair.getCustomerAddress());
		repair.setCustomerAddress("NOWHERE");
		assertEquals("NOWHERE",repair.getCustomerAddress());
		//telephoneNumber
		assertEquals("01643215213",repair.getTelephoneNumber());
		repair.setTelephoneNumber("081570000");
		assertEquals("081570000",repair.getTelephoneNumber());
		//description
		assertEquals("stinkt nach Maggi",repair.getDescription());
		repair.setDescription("riecht gut");
		assertEquals("riecht gut",repair.getDescription());

	}



	@Test
	void testEqualsAndHashCode() {

		BusinessTime businessTimeMock = mock(BusinessTime.class);
		Repair repair1 = new Repair("test@example.com",
			Money.of(50, Monetary.getCurrency("USD")),
			RepairType.NORMAL,
			businessTimeMock,
			"LOL",
			"Doe",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");

		Repair repair2 = new Repair("test@example.com",
			Money.of(50,
				Monetary.getCurrency("USD")),
			RepairType.NORMAL,
			businessTimeMock,
			"LOL",
			"Doe",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");

		Repair repair3 = new Repair("another@example.com",
			Money.of(30, Monetary.getCurrency("USD")),
			RepairType.NORMAL,
			businessTimeMock,
			"LMAO",
			"Doe",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");

		assertEquals(repair1, repair2);
		assertNotEquals(repair1, repair3);
		assertEquals(repair1.hashCode(), repair2.hashCode());
		assertNotEquals(repair1.hashCode(), repair3.hashCode());
	}



	@Test
	void testGetFormattedDateTime() {

		LocalDateTime date = LocalDateTime.of(2023, 1, 1, 12, 30);
		BusinessTime businessTimeMock = mock(BusinessTime.class);

		String formattedDateTime = repair.getFormattedDateTime(date);

		assertEquals("01.01.2023 12:30", formattedDateTime);
	}

	@Test
	void testGetEnddateAndDurationInMinutes() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		when(businessTimeMock.getTime()).thenReturn(LocalDateTime.now());

		assertNull(repair.getEndDate());
		assertEquals(0, repair.getDurationInMinutes());

		repair.finish(businessTimeMock);

		assertNotNull(repair.getEndDate());
		assertTrue(repair.getDurationInMinutes() > 0);
	}

	@Test
	void testGetId() {
		assertNotNull(repair.getId());
	}


	@Test
	void GetSetCostEstimateTest() {

		assertEquals(RepairType.NORMAL.getCost(), repair.getCostEstimate());
		repair.setCostEstimate(500);
		assertEquals(500,repair.getCostEstimate());

	}

	@Test
	void equalsTest() {
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		Object object = new Object();
		boolean resultTrue = repair.equals(repair);
		assertTrue(resultTrue);
		boolean resultFalse = repair.equals(null);
		assertFalse(resultFalse);
		boolean resultFalse2 = repair.equals(object);
		assertFalse(resultFalse2);

	}

	@Test
	void getSetOrderIdentifierTest(){
		BusinessTime businessTimeMock = mock(BusinessTime.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);

		repair.setOrderIdentifier(orderIdentifier);

		Order.OrderIdentifier result = repair.getOrderIdentifier();

		assertEquals(orderIdentifier, result);

	}





}

package clockshop.repair;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.time.BusinessTime;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.money.MonetaryAmount;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepairSomeTests {

	@MockBean
	private BusinessTime businessTime;

	@Test
    void testConstructor() {
        MonetaryAmount price = Money.of(150, "USD");
        RepairType repairType = RepairType.QUICK;
        String email = "test@example.com";
		String customerForename = "Willi";
        String customerName = "Test Customer";
        String customerAddress = "Pfotenhauerstr. 75";
        String telephoneNumber = "0163 2519314";
        String des = "Uhr ist verbrannt.";

        Repair repair = new Repair(email,
            price,
            repairType,
            businessTime,
			customerForename,
            customerName,
            customerAddress,
            telephoneNumber,
            des);

        assertEquals(email, repair.getEmail());
        assertEquals(customerName, repair.getCustomerName());
        assertEquals(customerAddress, repair.getCustomerAddress());
        assertEquals(telephoneNumber, repair.getTelephoneNumber());
        assertEquals(des, repair.getDescription());
        assertEquals(repairType, repair.getRepairType());
        assertEquals(repairType.getCost(), repair.getCostEstimate());
        assertEquals(businessTime.getTime(), repair.getDate());
        assertNull(repair.getEndDate());
    }

    @Test
    void testConstructorWithNullEmail() {
        MonetaryAmount price = Money.of(100, "USD");
        RepairType repairType = RepairType.QUICK;
		String customerForename = "Willi";
        String customerName = "Test Customer";
        String customerAddress = "Pfotenhauerstr. 75";
        String telephoneNumber = "0163 2519314";
        String des = "Uhr ist verbrannt.";
        assertThrows(IllegalArgumentException.class, () -> {
            new Repair(null,
                price,
                repairType,
                businessTime,
				customerForename,
                customerName,
                customerAddress,
                telephoneNumber,
                des);
        });
    }

    @Test
    void testConstructorWithNullCustomerName() {
        MonetaryAmount price = Money.of(100, "USD");
        RepairType repairType = RepairType.NORMAL;
        String email = "test@example.com";
        String customerAddress = "Pfotenhauerstr. 75";
        String telephoneNumber = "0163 2519314";
        String des = "Uhr ist verbrannt.";
        assertThrows(IllegalArgumentException.class, () -> {
            new Repair(email, price, repairType, businessTime,null,null,
                customerAddress,telephoneNumber,des);
        });
    }
}
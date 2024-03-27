package clockshop.repair;

import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.extras.PDFManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

/**
 * Tests for {@link RepairManagement}
 */
@ExtendWith(MockitoExtension.class)
class RepairManagementTest {

	@Mock
	private ShopInventoryManagement shopInventoryManagement;

    @Mock
    private RepairRepository repairRepository;

    @Mock
    private BusinessTime businessTime;

    private RepairManagement repairManagement;

	@Mock
	private PDFManagement pdfManagement;

    private Repair repair;

    @BeforeEach
    void setUp() {
		MockitoAnnotations.openMocks(this);
        MonetaryAmount price = Money.of(100.0, "EUR");
        repair = new Repair("test@example.com",
			price,
			RepairType.QUICK,
			businessTime,
			"lol",
			"Test Customer",
			"Pfotenhauerstr. 74",
			"0163 2519314",
			"Uhr brennt");
		repairManagement = new RepairManagement(repairRepository, businessTime, shopInventoryManagement, pdfManagement);

    }

    @Test
    void addRepair() {
        when(repairRepository.save(any(Repair.class))).thenReturn(repair);

        repairManagement.addRepair(repair);

        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void deleteRepair() {
        doNothing().when(repairRepository).delete(repair);

        repairManagement.deleteRepair(repair);

        verify(repairRepository, times(1)).delete(repair);
    }

    @Test
    void updateRepair() {
        when(repairRepository.save(repair)).thenReturn(repair);

        repairManagement.updateRepair(repair);

        verify(repairRepository, times(1)).save(repair);
    }

    @Test
    void findAll() {
        when(repairRepository.findAll()).thenReturn(Streamable.of(repair));

        repairManagement.findAll();

        verify(repairRepository, times(1)).findAll();
    }

    @Test
    void findRepairById_ExistingId() {
        when(repairRepository.findAll()).thenReturn(Streamable.of(repair));

        Repair foundRepair = repairManagement.findRepairById(repair.getId());

        verify(repairRepository, times(1)).findAll();
        assertNotNull(foundRepair, "Repair should not be null");
        assertEquals(repair.getId(), foundRepair.getId(), "Ids should match");
    }

    @Test
    void findRepairById_NonExistingId() {
        when(repairRepository.findAll()).thenReturn(Streamable.of(repair));
        ProductIdentifier nonExistingId = ProductIdentifier.of("non-existing-id");
        Repair foundRepair = repairManagement.findRepairById(nonExistingId);

        verify(repairRepository, times(1)).findAll();
        assertNull(foundRepair, "Repair should be null for non-existing id");
    }

    @Test
    void findRepairById_NullId() {
        when(repairRepository.findAll()).thenReturn(Streamable.of(repair));

        Repair foundRepair = repairManagement.findRepairById(null);

        verify(repairRepository, times(1)).findAll();
        assertNull(foundRepair, "Repair should be null for null id");
    }

    @Test
    void sortRepairsByDateAndFinished() {
        
        Repair quickNotFinished = new Repair("quickNotFinished@example.com",
			Money.of(100.0, "EUR"),
			RepairType.QUICK,
			businessTime,
			"Test",
			"Customer",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");
        Repair normalNotFinished = new Repair("normalNotFinished@example.com",
			Money.of(100.0, "EUR"),
			RepairType.NORMAL,
			businessTime,
			"Test",
			"Customer",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");
        Repair finishedEarly = new Repair("finishedEarly@example.com",
			Money.of(100.0, "EUR"),
			RepairType.NORMAL,
			businessTime,
			"Test",
			"Customer",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");
        Repair finishedLate = new Repair("finishedLate@example.com",
			Money.of(100.0, "EUR"),
			RepairType.NORMAL,
			businessTime,
			"Test",
			"Customer",
			"JustusBummStr. 84",
			"01643215213",
			"stinkt nach Maggi");
        
        finishedEarly.finish(businessTime);
        businessTime.forward(Duration.ofMinutes(1));
        finishedLate.finish(businessTime);
        
        when(repairRepository.findAll()).thenReturn(Streamable.of(quickNotFinished, normalNotFinished, finishedEarly, finishedLate));

		Streamable<Repair> sortedRepairs = repairManagement.sortRepairsByDateAndFinished();
        
        List<Repair> sortedRepairsList = sortedRepairs.toList();
        assertEquals(4, sortedRepairsList.size());
        assertEquals(quickNotFinished, sortedRepairsList.get(0));
        assertEquals(normalNotFinished, sortedRepairsList.get(1));
        assertEquals(finishedEarly, sortedRepairsList.get(2));
        assertEquals(finishedLate, sortedRepairsList.get(3));

        verify(repairRepository, times(1)).findAll();
    }

	@Test
	void createStartPDFTest() throws DocumentException, IOException, WriterException {
		Mockito.doNothing().when(pdfManagement).pdfStartRepair(repair);

		repairManagement.createStartPDF(repair);

		verify(pdfManagement,times(1)).pdfStartRepair(repair);
	}

	@Test
	void endStartPDFTest() throws DocumentException, IOException, WriterException {
		Mockito.doNothing().when(pdfManagement).pdfEndRepair(repair);

		repairManagement.createEndPDF(repair);

		verify(pdfManagement,times(1)).pdfEndRepair(repair);
	}

	@Test
	void searchTest(){
		List<Repair> list = List.of(mock(Repair.class));

		when(repairRepository.findByCustomerNameContainingIgnoreCase(any())).thenReturn(list);

		List<Repair> result = repairManagement.search("test");
		assertEquals(list, result);
	}

	@Test
	void getRepairPageTest(){
		Repair repair = mock(Repair.class);

		when(repair.isFinished()).thenReturn(true);

		Streamable<Repair> repairs = Streamable.of(repair);

		when(repairRepository.findAll()).thenReturn(repairs);



		Page<Repair> result = repairManagement.getRepairPage(0, 5);

		verify(repairRepository, times(1)).findAll();
	}

	@Test
	void getMaterialsPageTest(){
		when(shopInventoryManagement.findAllMaterials()).thenReturn(Streamable.of(mock(ShopInventoryItem.class)));

		Page<ShopInventoryItem> result = repairManagement.getMaterialsPage(0,5);

		verify(shopInventoryManagement,times(1)).findAllMaterials();
	}
}
package clockshop.repair;

import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;

import clockshop.extras.EmailService;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link RepairController}
 */
class RepairControllerTest {

	@Mock
	private RepairManagement repairManagement;

	@Mock
	private EmailService emailService;

	@Mock
	private BusinessTime businessTime;

	@Mock
	private Model model;

	@Mock
	private ShopInventoryManagement inventory;


	private RepairController repairController;

	@Mock
	private ShopOrderManagement shopOrderManagement;


	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		Streamable<ShopInventoryItem> shopInventoryItems = mock(Streamable.class);
		repairController = new RepairController(emailService, businessTime, repairManagement, inventory, shopOrderManagement);
		when(inventory.findAllInventoryItems()).thenReturn(shopInventoryItems);
		when(repairManagement.findAll()).thenReturn(mock(Streamable.class));

	}


	@Test
	void testRepairPreWithDefaultParameters() {
		ArrayList<Repair> emptyList =  new ArrayList<>();
		List<Repair> repairs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			repairs.add(mock(Repair.class));
		}
		Streamable<Repair> repairsStreamable = Streamable.of(repairs);
		when(repairManagement.sortRepairsByDateAndFinished()).thenReturn(repairsStreamable);


		String viewName = repairController.repairPre(model, null, null,"");

		//verify(model).addAttribute("repairs", eq(anyList()));
		//verify(model).addAttribute("page", 0);
		//verify(model).addAttribute("maxPages", 0);
		assertEquals("Repair/repair", viewName);
	}


	@Test
	void testRepairPreWithCustomParameters() {
		List<Repair> repairs = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			repairs.add(mock(Repair.class));
		}
		Streamable<Repair> repairsStreamable = Streamable.of(repairs);
		when(repairManagement.sortRepairsByDateAndFinished()).thenReturn(repairsStreamable);

		String viewName = repairController.repairPre(model, 5, 2, "");

		//verify(model).addAttribute(eq("repairs"), anyList());
		assertEquals("Repair/repair", viewName);
	}


	@Test
	void testRepairPreWithPageOutOfBounds() {
		List<Repair> repairs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			repairs.add(mock(Repair.class));
		}
		Streamable<Repair> repairsStreamable = Streamable.of(repairs);
		when(repairManagement.sortRepairsByDateAndFinished()).thenReturn(repairsStreamable);

		String viewName = repairController.repairPre(model, 5, -1, "");
		assertEquals("Repair/repair", viewName);
	}


	@Test
	void testCompleteRepair() throws DocumentException, IOException, WriterException {
		Repair repair = mock(Repair.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);
		Order order = mock(Order.class);

		when(repair.isFinished()).thenReturn(false);
		when(shopOrderManagement.getOrder(any())).thenReturn(order);
		when(order.getTotal()).thenReturn(Money.of(10, "EUR"));
		when(repair.getDurationInMinutes()).thenReturn(Long.valueOf(25));
		Principal principal = mock(Principal.class);
		when(repairManagement.findRepairById(any(ProductIdentifier.class))).thenReturn(repair);
		when(shopOrderManagement.completeRepairOrder(any(), any(), any())).thenReturn(orderIdentifier);
		
		String viewName = repairController.completeRepair(mock(ProductIdentifier.class), principal);
		verify(repairManagement, times(1)).findRepairById(any(ProductIdentifier.class));
		verify(repairManagement, times(2)).updateRepair(any(Repair.class));
		assertEquals("redirect:/repair", viewName);
	}

	@Test
	void testCompleteWhenDurationZeroRepair() throws DocumentException, IOException, WriterException {
		Repair repair = mock(Repair.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);
		Order order = mock(Order.class);

		when(shopOrderManagement.getOrder(any())).thenReturn(order);
		when(order.getTotal()).thenReturn(Money.of(10, "EUR"));
		when(repair.getDurationInMinutes()).thenReturn(Long.valueOf(0));
		Principal principal = mock(Principal.class);
		when(repairManagement.findRepairById(any(ProductIdentifier.class))).thenReturn(repair);
		when(shopOrderManagement.completeRepairOrder(any(), any(), any())).thenReturn(orderIdentifier);
		
		String viewName = repairController.completeRepair(mock(ProductIdentifier.class), principal);
		verify(repairManagement, times(1)).findRepairById(any(ProductIdentifier.class));
		verify(repairManagement, times(2)).updateRepair(any(Repair.class));
		assertEquals("redirect:/repair", viewName);
	}

	@Test
	void testCompleteWhenRepairIsAlreadyFinished() throws DocumentException, IOException, WriterException {
		Repair repair = mock(Repair.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);
		when(repair.isFinished()).thenReturn(true);
		when(repair.getDurationInMinutes()).thenReturn(Long.valueOf(0));
		Principal principal = mock(Principal.class);
		when(repairManagement.findRepairById(any(ProductIdentifier.class))).thenReturn(repair);
		when(shopOrderManagement.completeRepairOrder(any(), any(), any())).thenReturn(orderIdentifier);
		
		String viewName = repairController.completeRepair(mock(ProductIdentifier.class), principal);
		verify(repairManagement, times(0)).updateRepair(any(Repair.class));
		assertEquals("redirect:/repair", viewName);
	}

	@Test
	void testShowAddMaterialsForm() {
		when(inventory.findAllMaterials()).thenReturn(Streamable.empty());

		String viewName = repairController.showAddMaterialsForm(model, 5,1, " ");
		verify(model, times(5)).addAttribute(anyString(), any());
		assertEquals("/Repair/addMaterials", viewName);
	}

	@Test
	void repairPostTest() throws DocumentException, IOException, WriterException {

		when(businessTime.getTime()).thenReturn(LocalDateTime.now());

		String result = repairController.repairPost(model,
			"Leon.Seidl@gmail.com",
			RepairType.NORMAL,
			"Leon",
			"Seidl",
			"Pfotenhauerstr. 84",
			"0163 2519314",
			"Bulldogge hat Uhr gefressen, Bulldogge mit abgegeben");

		//verify(repairManagment, times(1)).createNewPdf(any(), any());
		verify(repairManagement, times(1)).addRepair(any());
		verify(model, times(2)).addAttribute(any(), any());
		verify(emailService, times(1)).sendEmail(any(), any(), any());

		assertEquals("redirect:/repair", result);
	}

	@Test
	void addMaterialsTest() {
		ProductIdentifier id = mock(ProductIdentifier.class);

		String result = repairController.addMaterials(model, id);

		verify(model, times(3)).addAttribute(any(), any());

		assertEquals("redirect:/repair-addMaterials", result);


	}
	@Test
	void addMaterialQuantityTest(){
		ProductIdentifier id = mock(ProductIdentifier.class);

		String result = repairController.addMaterialQuantity(model, id, 10);

		verify(model, times(2)).addAttribute(any(),any());

		assertEquals("redirect:/repair-addMaterials", result);
	}
	@Test
	void addRepairTest(){
		assertEquals("Repair/addRepair", repairController.addRepair());
	}

	@Test
	 void showEditRepairPageTest(){
		ProductIdentifier repairId = mock(ProductIdentifier.class);

		assertEquals("Repair/editRepair", repairController.showEditRepairPage(model,repairId));


	}
	@Test
	void configureRepairCost(){
		assertEquals("Repair/repairCostConfigure", repairController.configureRepairCost(model));
	}

	@Test
	void editRepairTest(){
		ProductIdentifier repairId = mock(ProductIdentifier.class);
		RepairType repairType = mock(RepairType.class);
		Repair repair = mock(Repair.class);

		when(repairManagement.findRepairById(repairId)).thenReturn(repair);

		assertEquals("redirect:/repair", repairController.editRepair(model,
			repairId,
			null,
			null,
			null,
			null,
			10));



		assertEquals("redirect:/repair", repairController.editRepair(model,
			repairId,
			"BOB",
			"Scam",
			"keine@gmail.com",
			repairType,
			10));

	}

	@Test
	void saveConfigurationTest(){
		assertEquals("redirect:/repair", repairController.saveConfiguration(1,1,1,1));
		assertEquals("redirect:/repair", repairController.saveConfiguration(null,null,null,null));

	}

	@Test
	void deleteRepairTest(){
		ProductIdentifier repairId = mock(ProductIdentifier.class);
		assertEquals("redirect:/repair", repairController.deleteRepair(repairId));

	}



}


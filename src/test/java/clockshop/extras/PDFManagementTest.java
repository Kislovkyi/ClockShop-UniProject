package clockshop.extras;

import clockshop.grandfatherclock.GFCCartItem;
import clockshop.maintenance.MaintenanceContract;
import clockshop.order.ShopOrderManagement;
import clockshop.repair.Repair;
import clockshop.repair.RepairType;
import com.google.zxing.WriterException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for  {@link PDFManagement}
 */
class PDFManagementTest {

	@Mock
	private ShopOrderManagement shopOrderManagement;

	@Mock
	private QrCodeService qrCodeService;

	private PDFManagement pdfManagement;



	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		System.setProperty("os.name", "true");
		this.pdfManagement = new PDFManagement(shopOrderManagement);
	}

	@Test
	void createDocumentTest() throws DocumentException {
		Document document = PDFManagement.createDocument();
		assertTrue(document.isOpen());
	}

	@Test
	void addQRTest() throws DocumentException, IOException, WriterException {

		Document document = mock(Document.class);
		PDFManagement.addQR(document);

		verify(document, times(2)).add(any());
		verify(document, times(1)).close();
	}

	@Test
	void pdfOrderFinishedTest() throws DocumentException, IOException, WriterException {
		Image image = QrCodeService.generateQrCode("Test");

		UserAccount.UserAccountIdentifier userID = mock(UserAccount.UserAccountIdentifier.class);
		Order testOrder = new Order(userID, Cash.CASH);
		Order order = mock(Order.class);
		Order.OrderIdentifier id = mock(Order.OrderIdentifier.class);
		when(order.getDateCreated()).thenReturn(LocalDateTime.now());
		when(order.getId()).thenReturn(id);
		when(order.getOrderLines()).thenReturn(testOrder.getOrderLines());

		PDFManagement.pdfOrderFinished(order,"Bernd", "Peter", "Rudi", "0153");

		verify(order, times(1)).getDateCreated();
		verify(order, times(1)).getId();
	}

	@Test
	void pdfEndRepairTest() throws DocumentException, IOException, WriterException {
		Image image = QrCodeService.generateQrCode("Test");
		UserAccount.UserAccountIdentifier userID = mock(UserAccount.UserAccountIdentifier.class);
		Order testOrder = new Order(userID, Cash.CASH);
		Order.OrderIdentifier id = mock(Order.OrderIdentifier.class);
		Order order = mock(Order.class);
		Repair repair = mock(Repair.class);

		when(order.getId()).thenReturn(id);
		when(order.getOrderLines()).thenReturn(testOrder.getOrderLines());
		when(shopOrderManagement.getOrder(any())).thenReturn(order);
		when(repair.getRepairType()).thenReturn(RepairType.NORMAL);
		when(repair.getCostEstimate()).thenReturn(100);
		when(repair.getCustomerName()).thenReturn("Henry");
		when(repair.getEmail()).thenReturn("Henry");
		when(repair.getFormattedDateTime(any())).thenReturn("");
		when(repair.getPrice()).thenReturn(Money.of(10,"EUR"));

		pdfManagement.pdfEndRepair(repair);

		verify(repair,times(1)).getCustomerName();
		verify(repair,times(1)).getRepairType();
		verify(order,times(2)).getId();
		verify(shopOrderManagement,times(1)).getOrder(any());
	}

	@Test
	void pdfStartRepairTest() throws DocumentException, IOException, WriterException {

		Image image = QrCodeService.generateQrCode("Test");
		Repair repair = mock(Repair.class);
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);


		when(repair.getCostEstimate()).thenReturn(100);
		when(repair.getCustomerName()).thenReturn("Henry");
		when(repair.getEmail()).thenReturn("Henry");
		when(repair.getFormattedDateTime(any())).thenReturn("");
		when(repair.getPrice()).thenReturn(Money.of(10,"EUR"));
		when(repair.getId()).thenReturn(id);

		pdfManagement.pdfStartRepair(repair);

		verify(repair,times(2)).getCustomerName();
		verify(repair,times(1)).getEmail();
		verify(repair,times(2)).getCostEstimate();
		verify(repair,times(1)).getRepairType();
		verify(repair,times(1)).getId();


	}

	@Test
	void pdfMaintenanceContractTest() throws DocumentException, IOException, WriterException {

		Image image = QrCodeService.generateQrCode("Test");
		MaintenanceContract contract = mock(MaintenanceContract.class);
		UUID id = UUID.randomUUID();

		when(contract.getId()).thenReturn(id);
		when(contract.getCompany()).thenReturn("Danilo Company");
		when(contract.getAddress()).thenReturn("Krummen Weg 3");
		when(contract.getContactPerson()).thenReturn("Danilo");
		when(contract.getTowerQuantity()).thenReturn(2);
		when(contract.getBuildingQuantity()).thenReturn(2);
		when(contract.getPrice()).thenReturn(Money.of(20, "EUR"));

		pdfManagement.pdfMaintenanceContract(contract);

		verify(contract, times(1)).getId();
		verify(contract, times(1)).getCompany();
		verify(contract, times(1)).getAddress();
		verify(contract, times(1)).getContactPerson();
		verify(contract, times(1)).getTowerQuantity();
		verify(contract, times(1)).getBuildingQuantity();
		verify(contract, times(1)).getPrice();
	}

	@Test
	void pdfGFCOrderTest() throws DocumentException, IOException, WriterException {

		GFCCartItem gfcCartItem = mock(GFCCartItem.class);

		when(gfcCartItem.getName()).thenReturn("Henry");
		when(gfcCartItem.getCompanyName()).thenReturn("Henry");
		when(gfcCartItem.getQuantity()).thenReturn(Quantity.of(10));
		when(gfcCartItem.getPrice()).thenReturn(Money.of(10, "EUR"));

		PDFManagement.pdfGFCOrderConfirmation(gfcCartItem, "Danilo", "Krummer Weg 3", "1234");

		verify(gfcCartItem, times(2)).getName();
		verify(gfcCartItem, times(2)).getCompanyName();
		verify(gfcCartItem, times(2)).getQuantity();
		verify(gfcCartItem, times(2)).getPrice();



	}


}

package clockshop.grandfatherclock;

import clockshop.order.ShopOrderManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Tests for {@link GFCController}
 */
@ExtendWith(MockitoExtension.class)
class GFCControllerTest {

    @Mock
    private ShopOrderManagement shopOrderManagement;

    @Mock
    private CompanyRepository companyRepository;

	@Mock
	private GFCManagement gfcManagement;

    private GFCController gfcController;


    @BeforeEach
    void setUp() {
		MockitoAnnotations.openMocks(this);
        gfcController = new GFCController(shopOrderManagement,gfcManagement,companyRepository);
    }


    @Test
    void gfcPreTest(){

		Model m = new ExtendedModelMap();
		String result = gfcController.gfcPre(m, 5,-1,"Henry");
		assertEquals("GFC/gfclists", result);
    }

	@Test
	void gfcPreTestWhenNameNull(){

		when(gfcManagement.findAll()).thenReturn(Streamable.empty());
		Model m = new ExtendedModelMap();
		String result = gfcController.gfcPre(m, null,null,null);
		assertEquals("GFC/gfclists", result);
	}


    @Test
    void orderPreTest(){
		//when(companyRepository.getAllCompanies()).thenReturn(new ArrayList<>());
		Model m = new ExtendedModelMap();
		String result = gfcController.orderPre(m,5,-1, "Henry");
		assertEquals("GFC/gfcorders", result);
    }


	@Test
	void orderPreTestWhenNameNull(){
		//when(companyRepository.getAllCompanies()).thenReturn(new ArrayList<>());
		when(gfcManagement.findAll()).thenReturn(Streamable.empty());
		Model m = new ExtendedModelMap();
		String result = gfcController.orderPre(m,null,null, null);
		assertEquals("GFC/gfcorders", result);
	}

	@Test
	void addClockTest() {
		String name = "Henry";
		int price = 270;
		String companyName = "HenryCompany";
		String description = "Not sure if this is even a clock?";
		double discount =  0.1;

		String resultString = gfcController.addClock(name, price, companyName,description,discount);
		assertEquals("redirect:/gfclists" ,resultString);
	}

	@Test
	void addCompanyTest(){
		String result = gfcController.addCompany("HenryCompany", "43 Street");

		assertEquals("redirect:/addGFC", result);
	}

	@Test
	void updateStatus(){
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);
		Order.OrderIdentifier orderIdentifier = mock(Order.OrderIdentifier.class);

		List<GFCCartItem> items = new ArrayList<>();
		GFCCartItem item = mock(GFCCartItem.class);
		items.add(item);
		when(item.getOrderId()).thenReturn(orderIdentifier);
		when(item.getId()).thenReturn(productIdentifier);

		when(item.getStatusType()).thenReturn(StatusType.ORDERED);

		when(shopOrderManagement.getOrderItems()).thenReturn(items);

		assertEquals("redirect:/gfcorders",gfcController.updateStatus(productIdentifier,orderIdentifier));
		when(item.getStatusType()).thenReturn(StatusType.READY);
		assertEquals("redirect:/gfcorders",gfcController.updateStatus(productIdentifier,orderIdentifier));
		when(item.getStatusType()).thenReturn(StatusType.FINISHED);
		assertEquals("redirect:/gfcorders",gfcController.updateStatus(productIdentifier,orderIdentifier));

	}

	@Test
	void addGFCTest(){
		Model model = mock(Model.class);
		assertEquals("GFC/addgfc",gfcController.addGFC(model));
	}


	@Test
	void deleteCartItem(){
		Product.ProductIdentifier productIdentifier = mock(Product.ProductIdentifier.class);
		assertEquals("redirect:/gfclists",gfcController.deleteCartItem(productIdentifier));
	}

	@Test
	void editGFCTest(){
		Model model = mock(Model.class);
		Product.ProductIdentifier id = mock(Product.ProductIdentifier.class);
		GrandfatherClock target = new GrandfatherClock("t", Money.of(20,"EUR"),"t", "t", 20);

		when(gfcManagement.findById(any())).thenReturn(Optional.of(target));
		when(companyRepository.getAllCompanies()).thenReturn(List.of(mock(Company.class)));

		String result = gfcController.showEditGFCPage(model, id);
		assertEquals("GFC/editgfc", result);

		String result2 = gfcController.editGFC(id, "H", 20, "H", "H", 0.2);

		assertEquals("H",target.getName());
		assertEquals(20, target.getPrice().getNumber().intValue());
		assertEquals("H", target.getCompanyName());
		assertEquals("H", target.getDescription());
		assertEquals(0.2, target.getDiscount());
	}

}
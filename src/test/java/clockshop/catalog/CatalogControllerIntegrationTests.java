package clockshop.catalog;


import clockshop.inventory.ShopInventoryManagement;
import clockshop.staff.EmployeeManagement;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Streamable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CatalogController}
 */
class CatalogControllerIntegrationTests {

	CatalogController catalogController;


	@Mock
	ShopInventoryManagement shopInventoryManagement;

	@Mock
	ArticleCatalog articleCatalog;

	@Mock
	EmployeeManagement employeeManagement;


	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		catalogController = new CatalogController(shopInventoryManagement, articleCatalog, employeeManagement);
	}
	@Test
	void articleCatalogTest() {

		Model model = new ExtendedModelMap();
		Principal principal = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(articleCatalog.findAll()).thenReturn(Streamable.empty());
		when(shopInventoryManagement.findAll()).thenReturn(Streamable.empty());

		String result = catalogController.articleCatalog(model,null,null,null,principal,request);
		assertEquals("Warehouse/catalog", result);
	}

	@Test
	void articleCatalogWhenNameNullTest() {
		Model model = new ExtendedModelMap();
		Principal principal = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(articleCatalog.findAll()).thenReturn(Streamable.empty());
		when(shopInventoryManagement.findAll()).thenReturn(Streamable.empty());
		String result = catalogController.articleCatalog(model,5,0,null,principal,request);


		assertEquals("Warehouse/catalog", result);

	}

	@Test
	void articleCatalogSearchTest(){
		Model model = new ExtendedModelMap();
		Principal principal = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		List<Article> list = mock(List.class);


		when(articleCatalog.findAll()).thenReturn(Streamable.of(mock(Article.class)));
		when(articleCatalog.findByNameContainingIgnoreCase(any())).thenReturn(list);
		when(list.size()).thenReturn(10);
		when(list.removeIf(any())).thenReturn(true);
		when(shopInventoryManagement.findAll()).thenReturn(Streamable.of(mock(Article.class)));
		String result = catalogController. articleCatalog(model,5,0,"null",principal,request);
		when(shopInventoryManagement.findAllArticles()).thenReturn(Streamable.empty());

		assertEquals("Warehouse/catalog", result);
	}
}

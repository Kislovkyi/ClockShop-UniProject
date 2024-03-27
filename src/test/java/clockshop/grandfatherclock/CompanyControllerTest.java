package clockshop.grandfatherclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Tests for {@link CompanyController}
 */
class CompanyControllerTest {

	@Mock
	private CompanyRepository companyRepository;

	private CompanyController companyController;

	@BeforeEach
	void setup(){
		MockitoAnnotations.openMocks(this);
		companyController = new CompanyController(companyRepository);
	}
	@Test
	void showCompanyTest(){
		Model m = new ExtendedModelMap();
		String result = companyController.showCompany(m);
		assertEquals("GFC/companies", result);
	}

	@Test
	void add(){
		assertEquals("GFC/addcompanies",companyController.add());
	}

}

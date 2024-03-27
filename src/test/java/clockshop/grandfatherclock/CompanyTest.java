package clockshop.grandfatherclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Tests for {@link Company}
 */
class CompanyTest {
	private CompanyRepository companyRepository;
	private Company company;
	@BeforeEach
	void setup(){
		companyRepository = new CompanyRepository();
		String companyName = "Example Company";
		String address = "123 Main St";
		company = new Company(companyName, address);

	}

	@Test
	void addCompanyTest(){
		companyRepository.addCompany("test", "1234");
        assertFalse(companyRepository.getAllCompanies().isEmpty());
		assertEquals("test", companyRepository.getAllCompanies().get(0).getCompanyName());
		assertEquals("1234",companyRepository.getAllCompanies().get(0).getAddress());
	}

	@Test
	void testCompanyConstructor() {
		String companyName = "Example Company";
		String address = "123 Main St";

		assertNotNull(company);
		assertEquals(companyName, company.getCompanyName());
		assertEquals(address, company.getAddress());
	}

	@Test
	void testGetters(){
		assertEquals("Example Company", company.getCompanyName());
		assertEquals("123 Main St", company.getAddress());
	}

	@Test
	void testSetters(){
		assertEquals("Example Company", company.getCompanyName());
		assertEquals("123 Main St", company.getAddress());

		company.setCompanyName("Name");
		company.setAddress("nowhere");


		assertEquals("Name", company.getCompanyName());
		assertEquals("nowhere", company.getAddress());


	}
	@Test
	void existsByNameTest(){
		companyRepository.addCompany("LOL","Test-Street");
		assertTrue(companyRepository.existsByName("LOL"));
	}



}

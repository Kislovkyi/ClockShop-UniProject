package clockshop.employee;

import clockshop.staff.RegistrationForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Role;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link RegistrationForm}
 */
class RegistrationFormTest {


	private final String forename = "Henry";
	private final String name = "Fochmann";
	private final String username = "Fochstar";
	private final String email = "Henry.Fochmann@gmail.com"; //Axel Voss: "All gmail-users are bots."
	private final String password = "password";
	private final String address = "Luisenstr. 86, 06869 Coswig";
	private final String telephoneNumber = "0173 2519314";
	private final Float hourRate = 22.4F;
	private final Integer monthlyHours = 700;
	private final List<Role> roles = List.of(Role.of("stinklurch"));
	private final RegistrationForm registrationForm = new RegistrationForm(forename, name, username,email,telephoneNumber,
		password,address,hourRate,monthlyHours,roles,null);
	@BeforeEach
	void setup(){

	}
	@Test
	void rolesNull(){
		RegistrationForm registrationForm2 = new RegistrationForm(forename, name, username,email,telephoneNumber,
			password,address,hourRate,monthlyHours,null,null);
		assertEquals(List.of(Role.of("SALE")),registrationForm2.getRoles());
	}

	@Test
	void getForenameTest(){
		assertEquals(forename, registrationForm.getForename());
	}

	@Test
	void getMameTest(){
		assertEquals(name, registrationForm.getName());
	}

	@Test
	void getEmailTest(){
		assertEquals(email, registrationForm.getEmail());
	}

	@Test
	void getUsernameTest(){
		assertEquals(username, registrationForm.getUsername());
	}

	@Test
	void getPassword(){
		assertEquals(password, registrationForm.getPassword());
	}

	@Test
	void getAddress(){
		assertEquals(address, registrationForm.getAddress());
	}

	@Test
	void getMonthlyHours(){
		assertEquals(monthlyHours, registrationForm.getMonthlyHours());
	}

	@Test
	void getHourRate(){
		assertEquals(hourRate, registrationForm.getHourRate());

		RegistrationForm nullForm = new RegistrationForm(forename, name, username,email,telephoneNumber,
			password,address,null,monthlyHours,roles,null);

		assertNotNull(nullForm.getHourRate());

	}




}

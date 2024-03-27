package clockshop.staff;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@Order(10)
public class EmployeeDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeDataInitializer.class);

	private final UserAccountManagement userAccountManagement;
	private final EmployeeManagement employeeManagement;

	EmployeeDataInitializer(UserAccountManagement userAccountManagement, EmployeeManagement employeeManagement) {
		this.userAccountManagement = userAccountManagement;
		this.employeeManagement = employeeManagement;
	}

	@Override
	public void initialize() {

		if (userAccountManagement.findByUsername("boss").isPresent()) {
			return;
		}
		LOG.info("Creating default users and employee.");

		var password = "123";

		try {
			for (RegistrationForm registrationForm : List.of(
				new RegistrationForm("Ober",
					"Boss",
					"boss",
					"uhrenladenswt@gmail.com",
					"01573 252153",
					password,
					"Hellmutweg 3, Dresden",
					21F,
					174,
					List.of(Role.of("BOSS")),
					null),
				new RegistrationForm("Hans-Dieter",
					"Cunningham",
					"hans",
					"kemmeldarius@gmail.com",
					"01573 252153",
					password,
					"Finsterweg 4, Köln",
					21F,
					174,
					List.of(Role.of("WATCH"), Role.of("SALE")),
					null),
				new RegistrationForm("Dexter",
					"Deshawn",
					"dextermorgan",
					"Mail@mailer.de",
					"01573 252153",
					password,
					"Afterlife, NightCity",
					22F,
					174,
					List.of(Role.of("SALE")),
					null),
				new RegistrationForm("John",
					"Hickey",
					"earlhickey",
					"John.Hickey@love.com",
					"01573 252153",
					password,
					"Motel, Camden County",
					12F,
					40,
					List.of(Role.of("WATCH")),
					null),
				new RegistrationForm("Unknown",
					"McLovin",
					"mclovinfogell",
					"McLovin@gmail.com",
					"01573 252153",
					password,
					"Vespucci 4, Los Angeles",
					14F,
					200,
					List.of(Role.of("WATCH")),
					null),
				new RegistrationForm("Leon",
					"Seidl",
					"wizzardmann",
					"Leon.Seidl@mailbox.tu-dresden.de",
					"01573 252153",
					password,
					"Pfotenhauerstraße, Dresden",
					15F,
					32,
					List.of(Role.of("SALE"), Role.of("WATCH")),
					null),
				new RegistrationForm("Steve1",
					"BWL",
					"BWL1",
					"KimraGaming@gmail.com",
					"01573 252153",
					password,
					"Altstadt, Dresden",
					400F,
					5,
					List.of(Role.of("SALE")),
					null)

			)) {
				employeeManagement.createEmployee(registrationForm);
			}
		} catch (Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}

}
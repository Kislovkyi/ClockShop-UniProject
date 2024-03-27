package clockshop.staff;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.salespointframework.useraccount.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class RegistrationForm {
	private final @NotEmpty String forename;
	private final @NotEmpty String name;
	private final @NotEmpty String username;
	private final @NotEmpty String email;
	private final @NotEmpty String telephoneNumber;
	private final @NotEmpty String password;
	private final @NotEmpty String address;
	private final @NotNull Integer monthlyHours;
	private final @NotNull Float hourRate;
	private List<Role> roles;
	private final MultipartFile image;

	public RegistrationForm(String forename,
							String name,
							String username,
							String email,
							String telephoneNumber,
							String password,
							String address,
							Float hourRate,
							Integer monthlyHours,
							List<Role> roles,
							MultipartFile image) {

		this.forename = forename;
		this.name = name;
		this.username = username;
		this.email = email;
		this.telephoneNumber = telephoneNumber;
		this.password = password;
		this.address = address;
		this.monthlyHours = monthlyHours;
		this.hourRate = hourRate;
		this.roles = roles;
		if(roles == null){
			this.roles = List.of(Role.of("SALE"));
		}
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public String getForename() {
		return forename;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return address;
	}

	public float getHourRate() {
		return this.hourRate != null ? this.hourRate : 0.0f;
	}

	public Integer getMonthlyHours() {
		return monthlyHours;
	}

	public List<Role> getRoles() {
		return roles;
	}


	public String getEmail() {
		return email;
	}

	public MultipartFile getImage() {
		return image;
	}

	public String getTelephoneNumber() {
		return this.telephoneNumber;
	}
}


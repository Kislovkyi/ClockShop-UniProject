package clockshop.staff;

import clockshop.staff.Employee.EmployeeIdentifier;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import org.jmolecules.ddd.types.Identifier;
import org.salespointframework.core.AbstractAggregateRoot;
import org.salespointframework.useraccount.UserAccount;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


@Entity
public class Employee extends AbstractAggregateRoot<EmployeeIdentifier> {

	private final @EmbeddedId EmployeeIdentifier id = new EmployeeIdentifier();
	private String forename;
	private String name;
	private String username;
	private String email;
	private String address;
	private String role;
	private String imageUrl;
	private String telephoneNumber;
	private Integer monthlyHours;
	private float hourRate;
	private float salary;
	private UUID passwordResetToken;

	@OneToOne
	private UserAccount userAccount;

	public Employee(UserAccount userAccount,
					String forename,
					String name,
					String username,
					String email,
					String telephoneNumber,
					String address,
					float hourRate,
					Integer monthlyHours,
					String role,
					String imageUrl) {
		this.userAccount = userAccount;
		this.email = email;
		this.username = username;
		this.forename = forename;
		this.name = name;
		this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.imageUrl = imageUrl;
		this.salary = hourRate * monthlyHours;
		this.hourRate = hourRate;
		this.monthlyHours = monthlyHours;
		this.role = role;
		this.passwordResetToken = UUID.randomUUID();
	}

	public Employee() {
		super();
	}

	@Override
	public EmployeeIdentifier getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMonthlyHours() {
		return monthlyHours;
	}

	public void setMonthlyHours(Integer monthlyHours) {
		this.monthlyHours = monthlyHours;
	}

	public float getHourRate() {
		return hourRate;
	}

	public void setHourRate(Float hourRate) {
		this.hourRate = hourRate;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public UUID getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(UUID passwordReset) {
		this.passwordResetToken = passwordReset;
	}

	public void generateNewPasswordResetToken() {
		this.passwordResetToken = UUID.randomUUID();
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	@Embeddable
	public static final class EmployeeIdentifier implements Identifier, Serializable {
		@Serial
		private static final long serialVersionUID = 7740660930809051850L;
		private final @SuppressWarnings("unused") UUID identifier;

		public EmployeeIdentifier() {
			this(UUID.randomUUID());
		}

		public EmployeeIdentifier(UUID identifier) {
			this.identifier = identifier;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (identifier == null ? 0 : identifier.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}

			if (!(obj instanceof EmployeeIdentifier that)) {
				return false;
			}

			return this.identifier.equals(that.identifier);
		}
	}
}
package clockshop.order;

import jakarta.persistence.Entity;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;

@Entity
public class ShopOrder extends Order {

	private String forename;
	private String name;
	private String address;
	private String telephone;

	private String email;


	ShopOrder(UserAccount.UserAccountIdentifier userId,
			  PaymentMethod paymentMethod,
			  String forename,
			  String name,
			  String address,
			  String telephone,
			  String email){
		super(userId,paymentMethod);

		this.forename = forename;
		this.name = name;
		this.address = address;
		this.telephone = telephone;
		this.email = email;
	}

	public ShopOrder() {

	}

	public String getForename() {
		return forename;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getEmail() {
		return email;
	}
}

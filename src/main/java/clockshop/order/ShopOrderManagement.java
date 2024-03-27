package clockshop.order;

import clockshop.catalog.Article;
import clockshop.grandfatherclock.GFCCartItem;
import clockshop.grandfatherclock.GFCManagement;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.extras.PDFManagement;
import clockshop.repair.Repair;
import clockshop.staff.Employee;
import clockshop.staff.EmployeeManagement;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.*;
import org.salespointframework.order.Order.OrderIdentifier;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.salespointframework.core.Currencies.EURO;

@Service
public class ShopOrderManagement {


	private final EmployeeManagement employeeManagement;

	private final ShopInventoryManagement shopInventoryManagement;

	private final OrderManagement<Order> orderManagement;

	private final GFCManagement gfcManagement;

	private List<GFCCartItem> orderItems = new ArrayList<>();


	/**
	 * Constructs a new instance of ShopOrderManagement with the specified dependencies.
	 *
	 * @param orderManagement The order management service.
	 * @param employeeManagement The employee management service.
	 * @param shopInventoryManagement The inventory management service for the shop.
	 * @param gfcManagement The management service for Grandfather Clocks (GFC).
	 */
	public ShopOrderManagement(OrderManagement<Order> orderManagement,
							   EmployeeManagement employeeManagement,
							   ShopInventoryManagement shopInventoryManagement,
							   GFCManagement gfcManagement) {
		this.orderManagement = orderManagement;
		this.employeeManagement = employeeManagement;
		this.shopInventoryManagement = shopInventoryManagement;
		this.gfcManagement = gfcManagement;
	}

	/**
	 * Retrieves the list of items in the order.
	 *
	 * @return The list of items in the order.
	 */
	public List<GFCCartItem> getOrderItems() {
		return orderItems;
	}


	/**
	 * @param searchTerm name of GFC that is to be found
	 * @return list of all matching articles
	 */
	public List<GFCCartItem> searchGFCCartItemByName(String searchTerm) {
		return orderItems.stream()
			.filter(item -> item.getName().contains(searchTerm))
			.collect(Collectors.toList());
	}

	/**
	 * Retrieves a paginated view of GFCCartItems for display purposes.
	 *
	 * @param page The page number.
	 * @param size The number of items per page.
	 * @return A Page containing a sublist of GFCCartItems based on the provided page and size.
	 */
	public Page<GFCCartItem> getGFCOrdersPage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<GFCCartItem> employeeList = Streamable.of(ShopOrderManagement.this.getOrderItems()).toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<GFCCartItem> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	/**
	 * Sets the list of GFCCartItems for the order.
	 *
	 * @param orderItems The list of GFCCartItems to be associated with the order.
	 */
	public void setOrderItems(List<GFCCartItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * Cancels given Order with given Reason
	 *
	 * @param order  {@link Order}
	 * @param reason String contains cancel-reason
	 */
	public void cancelOrder(Order order, String reason) {
		orderManagement.cancelOrder(order, reason);
	}

	/**
	 * Returns Order from orderManagement
	 *
	 * @param orderIdentifier {@link OrderIdentifier}
	 * @return {@link Order}
	 */
	public Order getOrder(OrderIdentifier orderIdentifier) {
		return orderManagement.get(orderIdentifier).orElse(null);
	}

	/**
	 * Creates and processes a default order using the provided information and updates inventory.
	 *
	 * @param cart        The shopping cart containing items to be included in the order.
	 * @param userAccount The user account associated with the order.
	 * @param forename    The forename of the customer.
	 * @param name        The last name of the customer.
	 * @param address     The address of the customer.
	 * @param telephone   The telephone number of the customer.
	 * @param email       The email address of the customer.
	 * @return The created ShopOrder.
	 * @throws DocumentException If there is an issue with document processing.
	 * @throws IOException       If there is an issue with file input/output.
	 * @throws WriterException   If there is an issue with generating QR code.
	 */
	public Order defaultOrder(Cart cart,
							  UserAccount userAccount,
							  String forename,
							  String name,
							  String address,
							  String telephone,
							  String email) throws DocumentException, IOException, WriterException {
		ShopOrder shopOrder = new ShopOrder(Objects.requireNonNull(userAccount.getId()),
			Cash.CASH,
			forename,
			name,
			address,
			telephone,
			email);
		cart.addItemsTo(shopOrder);
		shopOrder.addChargeLine(Money.of(0,EURO),"ORDER");
		orderManagement.payOrder(shopOrder);
		orderManagement.completeOrder(shopOrder);
		for (CartItem cartItem: cart.get().toList()){
			ShopInventoryItem item = shopInventoryManagement.findSaleItem(cartItem.getProduct().getId());
			shopInventoryManagement.save((ShopInventoryItem) item.decreaseQuantity(cartItem.getQuantity()));
		}
		PDFManagement.pdfOrderFinished(shopOrder,forename,name,address,telephone);
		cart.clear();
		return shopOrder;
	}


	/**
	 * Creates Order for more Items
	 *
	 * @param article {@link Article}
	 * @param number  number of items
	 */
	public void orderMoreItems(Article article, int number, Principal principal) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(principal.getName());
		user.ifPresent(currentUser -> {
			var order = new Order(Objects.requireNonNull(currentUser.getUserAccount().getId()), Cash.CASH);

			order.addOrderLine(article, Quantity.of(number));
			order.addChargeLine((article.getPrice().multiply(number*article.getDiscount()*-1)),"MengenRabatt");

			ShopInventoryItem item = (ShopInventoryItem) shopInventoryManagement
				.findStoredItem(article.getId()).increaseQuantity(Quantity.of(-number));
			shopInventoryManagement.save(item);

			order.addChargeLine(Money.of(0,EURO),"BUY");
			orderManagement.payOrder(order);
			orderManagement.completeOrder(order);
		});
	}


	/**
	 * Pays all Salaries
	 */
	public void payAllSalary() {
		employeeManagement.findAll().forEach(employee -> employeeManagement.paySalaryByUsername(employee.getUsername()));
	}

	/**
	 * Completes a repair order by creating a ShopOrder and updating inventory with used materials.
	 *
	 * @param repair     The Repair object representing the repair job.
	 * @param materials  A map of product identifiers and corresponding quantities used in the repair.
	 * @param principal  The principal (authenticated user) initiating the order completion.
	 * @return The identifier of the completed ShopOrder, or {@code null} if the user is not found.
	 */
	public OrderIdentifier completeRepairOrder(Repair repair,
											   Map<Product.ProductIdentifier, Integer> materials,
											   Principal principal) {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(principal.getName());
		if (user.isPresent()) {
			Employee currentUser = user.get();
			var order = new ShopOrder(Objects.requireNonNull(
				currentUser.getUserAccount().getId()),
				Cash.CASH,
				repair.getCustomerForename(),
				repair.getCustomerName(),
				repair.getCustomerAddress(),
				repair.getTelephoneNumber(),
				repair.getEmail());
			for (Product.ProductIdentifier id : materials.keySet()) {
				order.addOrderLine(shopInventoryManagement.findProduct(id), Quantity.of(materials.get(id)));
				ShopInventoryItem item = shopInventoryManagement.findSaleItem(id);
				shopInventoryManagement.save((ShopInventoryItem) item.decreaseQuantity(Quantity.of(materials.get(id))));
			}

			order.addChargeLine(Money.of(repair.getCostEstimate(), EURO), "Kostenvoranschlag");

			order.addChargeLine(Money.of(0,EURO),"REPAIR");
			orderManagement.payOrder(order);
			orderManagement.completeOrder(order);
			return order.getId();
		}
		return null;
	}

	/**
	 * Creates and pays order based on {@link GFCCartItem}
	 *  @param gfcCartItems contains all information for the order
	 * @param principal current user
	 * @return {@link OrderIdentifier}
	 */
	public OrderIdentifier orderGFC(List<GFCCartItem> gfcCartItems,
									Principal principal,
									String forename,
									String name,
									String address,
									String telephone,
									String email) throws DocumentException, IOException, WriterException {
		Optional<Employee> user = employeeManagement.findEmployeeByUsername(principal.getName());
		ShopOrder shopOrder = new ShopOrder(user.get().getUserAccount().getId(),
			Cash.CASH,
			forename,
			name,
			address,
			telephone,
			email);
		List<ShopInventoryItem> itemList = new ArrayList<>();

		for (GFCCartItem item : gfcCartItems) {
			ShopInventoryItem inventoryItem = new ShopInventoryItem(gfcManagement.findById(item.getId()).get(),
				item.getQuantity(),
				"");

			itemList.add(inventoryItem);
			shopInventoryManagement.save(inventoryItem);

			shopOrder.addOrderLine(gfcManagement.findById(item.getId()).get(),item.getQuantity());
			shopOrder.addChargeLine(Money.of(gfcManagement.findById(item.getId()).get().getDiscount()
						 * item.getPrice().getNumber().floatValue() - item.getPrice().getNumber().floatValue(),
					EURO),
				"Provision für" +
					gfcManagement.findById(item.getId()).get().getName());
		}

		shopOrder.addChargeLine(Money.of(0,EURO),"GFC");
		orderManagement.payOrder(shopOrder);
		orderManagement.completeOrder(shopOrder);
		for (ShopInventoryItem inventoryItem : itemList){
			shopInventoryManagement.delete(inventoryItem);
		}
		return shopOrder.getId();
	}


	/**
	 * Retrieves a streamable collection of completed orders from the order management system.
	 *
	 * @return A Streamable<Order> containing completed orders.
	 */
	public Streamable<Order> getCompletedOrders(){
		return orderManagement.findBy(OrderStatus.COMPLETED);
	}
}



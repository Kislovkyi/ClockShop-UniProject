package clockshop.order;


import clockshop.catalog.Article;
import clockshop.grandfatherclock.GFCController;
import clockshop.grandfatherclock.GFCCartItem;
import clockshop.grandfatherclock.GFCManagement;
import clockshop.grandfatherclock.GrandfatherClock;
import clockshop.grandfatherclock.StatusType;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@SessionAttributes("cart")
public class OrderController {

	private final ShopOrderManagement shopOrderManagement;
	private final ShopInventoryManagement shopInventoryManagement;
	private static final String CARTSTR = "cart";

	/**
	 * Constructs an OrderController with dependencies on ShopOrderManagement and ShopInventoryManagement.
	 *
	 * @param shopOrderManagement      The ShopOrderManagement instance for managing shop orders.
	 * @param shopInventoryManagement The ShopInventoryManagement instance for managing shop inventory.
	 */
	OrderController(ShopOrderManagement shopOrderManagement,
					ShopInventoryManagement shopInventoryManagement) {
		this.shopOrderManagement = shopOrderManagement;
		this.shopInventoryManagement = shopInventoryManagement;
	}

	/**
	 * @return new Cart
	 */
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	/**
	 * @param article added to cart
	 * @param number quantity of articles
	 * @param cart
	 * @return template
	 */
	@PostMapping("/cart")
	String addItem(@RequestParam("pid") Article article,
				   @RequestParam("number") int number,
				   @ModelAttribute Cart cart) {
		Quantity futureCartValue = cart.getQuantity(article).add(Quantity.of(number));


		if (!shopInventoryManagement.findSaleItem(article.getId()).getQuantity().isLessThan(futureCartValue)) {
			cart.addOrUpdateItem(article, Quantity.of(number));
		}

		return "redirect:/catalog";
	}

	/**
	 * @param cart of articles
	 * @param grandfatherClock articles in cart
	 * @param number quantity of article
	 * @return
	 */
	@PostMapping("/gfccart")
	String addGfcCartItems(@ModelAttribute Cart cart,
						   @RequestParam("pid") GrandfatherClock grandfatherClock,
						   @RequestParam("number") int number) {
		cart.addOrUpdateItem(grandfatherClock, Quantity.of(number));
		return "redirect:/gfclists";
	}

	/**
	 * Handles the GET request for the "/cart" endpoint, displaying the cart view.
	 * Requires BOSS, WATCH, or SALE role access.
	 *
	 * @param model The Spring MVC model for supplying attributes to the view.
	 * @return The logical view name for the cart.
	 */
	@GetMapping("/cart")
	@Secured({"BOSS", "WATCH", "SALE"})
	String basket(Model model) {
		return CARTSTR;
	}

	@PostMapping("/checkout")
	@Secured({"BOSS", "WATCH", "SALE"})
	String buy(@ModelAttribute Cart cart,
			   Principal principal,
			   @LoggedIn Optional<UserAccount> userAccount,
			   @RequestParam("forename") String forename,
			   @RequestParam("name") String name,
			   @RequestParam("address") String address,
			   @RequestParam("telephone") String telephone,
			   @RequestParam("email") String email) throws DocumentException, IOException, WriterException {

		List<GFCCartItem> cartItems = new ArrayList<>();

		for (CartItem cartItem : cart.stream().toList()) {
			if (cartItem.getProduct() instanceof GrandfatherClock grandfatherClock) {
				cart.addOrUpdateItem(cartItem.getProduct(), cartItem.getQuantity().negate());
				cartItems.add(new GFCCartItem(grandfatherClock.getName(),
					(Money) cartItem.getPrice(),
					grandfatherClock.getCompanyName(),
					cartItem.getQuantity(),
					grandfatherClock.getId()));
			}
		}
		if (!cartItems.isEmpty()) {
			Order.OrderIdentifier orderIdentifier = shopOrderManagement.orderGFC(cartItems,
				principal,
				forename,
				name,
				address,
				telephone,
				email);
		cartItems.forEach(gfcCartItem -> gfcCartItem.setStatusType(StatusType.ORDERED));
		cartItems.forEach(gfcCartItem -> gfcCartItem.setOrderId(orderIdentifier));
		shopOrderManagement.getOrderItems().addAll(cartItems);
		cartItems.clear();
		}
		if (!cart.isEmpty()) {
			shopOrderManagement.defaultOrder(cart,
				userAccount.get(),
				forename,
				name,
				address,
				telephone,
				email);
		}


		return "redirect:/cart";
	}

	/**
	 * @param model never null
	 * @return template
	 */
	@GetMapping("/orders")
	@Secured({"BOSS", "WATCH", "SALE"})
	String orders(Model model) {
		model.addAttribute("ordersCompleted", shopOrderManagement.getCompletedOrders());
		return "orders";
	}

	/**
	 * @param cart
	 * clears cart
	 * @return template
	 */
	@GetMapping("/clear")
	@Secured({"BOSS", "WATCH", "SALE"})
	String clearCart(@ModelAttribute Cart cart) {
		cart.clear();
		return "redirect:/cart";
	}

	/**
	 * @param cart
	 * @param id productidentifier of article that is about to be removed
	 * @param userAccount logged in user
	 * @return template
	 */
	@GetMapping("/delete-cartItem")
	@Secured({"BOSS", "WATCH", "SALE"})
	public String deleteCartItem(@ModelAttribute Cart cart,
								 @RequestParam("id") Product.ProductIdentifier id,
								 @LoggedIn Optional<UserAccount> userAccount) {
		for (CartItem cartItem : cart.stream().toList()) {
			if (cartItem.getProduct().hasId(id)) {
				cart.addOrUpdateItem(cartItem.getProduct(), -1);
			}

		}
		return CARTSTR;
	}
}

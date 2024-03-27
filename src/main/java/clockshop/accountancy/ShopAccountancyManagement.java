package clockshop.accountancy;

import clockshop.catalog.Article;
import clockshop.extras.PageManager;
import clockshop.staff.Employee;
import org.javamoney.moneta.Money;
import org.salespointframework.accountancy.Accountancy;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

/**
 * This class is going to be the primary accountancy class and should bee accessed by everyone who needs it
 */
@Service
public class ShopAccountancyManagement implements PageManager<AccountancyEntry> {



	static class DateComparator implements java.util.Comparator<AccountancyEntry> {
		/**
		 * Compares two AccountancyEntries based on their date in descending order.
		 *
		 * @param a The first AccountancyEntry to be compared.
		 * @param b The second AccountancyEntry to be compared.
		 * @return  A negative integer, zero, or a positive integer as the first entry is
		 *          less than, equal to, or greater than the second entry, respectively.
		 */
		@Override
		public int compare(AccountancyEntry a, AccountancyEntry b) {
			final LocalDateTime[] dateA = new LocalDateTime[1];
			a.getDate().ifPresent(date -> dateA[0] = date);
			final LocalDateTime[] dateB = new LocalDateTime[1];
			b.getDate().ifPresent(date -> dateB[0] = date);

			if (dateA[0].isBefore(dateB[0])) {
				return 1;
			}
			if (dateB[0].isBefore(dateA[0])) {
				return -1;
			}
			return 0;
		}
	}


	private final Accountancy accountancy;

	/**
	 * Constructs a new ShopAccountancyManagement with the specified Accountancy instance.
	 *
	 * @param accountancy The Accountancy instance to be managed by the shop accountancy management.
	 */
	ShopAccountancyManagement(Accountancy accountancy) {

        this.accountancy = accountancy;
	}


	/**
	 * Method to find all AccountancyEntries sorted by date
	 *
	 * @return {@link List<AccountancyEntry>}
	 */
	public List<AccountancyEntry> findAllByOrderByDateDesc() {
		List<AccountancyEntry> list = new ArrayList<>(accountancy.findAll().stream().toList());
		list.sort(new DateComparator());
		return list;
	}


	/**
	 * Returns a {@link List<AccountancyEntry>} based on year and month
	 *
	 * @param year  Integer
	 * @param month Integer
	 * @return {@link List<AccountancyEntry>}
	 */
	public List<AccountancyEntry> findOrderByMonthAndYear(Integer year, Integer month) {
		List<AccountancyEntry> list = new ArrayList<>(accountancy.findAll().stream().toList());
		List<AccountancyEntry> returnList = new ArrayList<>();
		list.forEach(entry -> {
			if (entry.getDate().isPresent() && entry.getDate().get().getYear() == year && entry.getDate()
				.get().getMonthValue() == month) {
				returnList.add(entry);
			}
		});
		return returnList;
	}

	/**
	 * Calculates salesVolume of given Month
	 *
	 * @param year  Integer
	 * @param month Integer
	 * @return float of Money
	 */
	public float salesVolumeOfMonth(Integer year, Integer month) {
		final float[] summ = {0};
		findOrderByMonthAndYear(year, month).forEach(entry ->
			summ[0] += entry.getValue().getNumber().intValue()
		);
		return summ[0];
	}


	/**
	 * Finds all AccountancyEntries saved in accountancy
	 *
	 * @return void
	 */
	public Streamable<AccountancyEntry> findAll() {
		return accountancy.findAll();
	}


	/**
	 * Searches {@link Accountancy} for AccountancyEntries matching the searchTerm
	 * Old name searchAllOrderByID
	 * @param searchTerm {@link String}
	 * @return {@link List<AccountancyEntry>}
	 */
	public List<AccountancyEntry> search(String searchTerm) {
		return ShopAccountancyManagement.this.findAll().filter(
			item -> Objects.requireNonNull(item.getId()).toString().contains(searchTerm)).stream().toList();
	}

	/**
	 * Retrieves a paginated list of all accountancy entries based on the specified page and size.
	 *
	 * @param page The page number, starting from 0.
	 * @param size The number of entries to include on each page.
	 * @return A Page containing the accountancy entries for the specified page and size.
	 */
	public Page<AccountancyEntry> getAllOrderPage(Integer page, Integer size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<AccountancyEntry> employeeList = ShopAccountancyManagement.this.findAll().stream().toList();

		Integer start = Math.toIntExact(pageRequest.getOffset());
		Integer end = Math.min((start + pageRequest.getPageSize()),employeeList.size());

		List<AccountancyEntry> pageContent = employeeList.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, employeeList.size());
	}

	/**
	 * Gets AccountancyEntry from accountancy based on AccountancyEntryIdentifier
	 *
	 * @param id AccountancyEntryIdentifier
	 * @return AccountancyEntry
	 */
	public AccountancyEntry getEntry(AccountancyEntry.AccountancyEntryIdentifier id) {
		Optional<AccountancyEntry> accountancyEntry = accountancy.get(id);
		return accountancyEntry.orElse(null);

	}

	/**
	 * Creates a SortOutAccountancyEntry based on Parameters
	 *
	 * @param amount      Amount of Money
	 * @param description Description what kind of AccountancyEntry this is
	 * @param article     associated Article
	 * @param quantity    quantity-change of associated Article
	 */
	public void sortOutItem(MonetaryAmount amount, String description, Article article, Quantity quantity) {
		accountancy.add(new SortoutAccountancyEntry(amount, description, article.getId(), quantity));
	}

	/**
	 * Creates SalaryAccountancyEntry
	 * @param amount {@link MonetaryAmount}
	 * @param description {@link String}
	 * @param employeeId {@link clockshop.staff.Employee.EmployeeIdentifier}
	 */
	public void paySalary(MonetaryAmount amount, String description, Employee.EmployeeIdentifier employeeId) {
		accountancy.add(new SalaryAccountancyEntry(amount, description, employeeId));
	}

	/**
	 * Creates AccountancyEntry for contract pay
	 * @param amount {@link MonetaryAmount}
	 * @param description {@link String}
	 */
	public void payContract(MonetaryAmount amount,
							String description,
							String company,
							String contactPerson,
							String address) {
		accountancy.add(new MaintenanceAccountancyEntry(amount,
			description,
			company,
			contactPerson,
			address));
	}

	/**
	 * Retrieves a paginated list of accountancy entries ordered by date in descending order.
	 *
	 * @param page The page number, starting from 0.
	 * @param size The number of entries to include on each page.
	 * @return A Page containing the accountancy entries for the specified page and size,
	 * ordered by date in descending order.
	 */
	public Page<AccountancyEntry> getAccountancyPage(int page, int size){

		Pageable pageRequest = PageRequest.of(page, size);

		List<AccountancyEntry> accountancyEntries = findAllByOrderByDateDesc();

		int start = Math.toIntExact(pageRequest.getOffset());
		int end = Math.min((start + pageRequest.getPageSize()),accountancyEntries.size());

		List<AccountancyEntry> pageContent = accountancyEntries.subList(start, end);

		return new PageImpl<>(pageContent, pageRequest, accountancyEntries.size());
	}


	/**
	 * Calculates the Totals form all AccountancyEntries
	 * @return MonetaryAmount[] {totalProfit, totalExpense}
	 */
	public MonetaryAmount[] calculateTotals(){
		MonetaryAmount totalProfit = Money.of(0,EURO);
		MonetaryAmount totalExpense = Money.of(0,EURO);

		for (var entry: accountancy.findAll()){
			totalProfit = totalProfit.add(entry.getValue());
			if(entry.isExpense()){
				totalExpense = totalExpense.add(entry.getValue());
			}
		}
		return new MonetaryAmount[]{totalProfit, totalExpense};
	}

}









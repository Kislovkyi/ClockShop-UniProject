package clockshop.accountancy;

import clockshop.catalog.Article;
import clockshop.inventory.ShopInventoryItem;
import clockshop.inventory.ShopInventoryManagement;
import clockshop.order.ShopOrderManagement;
import clockshop.repair.Repair;
import clockshop.repair.RepairManagement;
import org.salespointframework.accountancy.AccountancyEntry;
import org.salespointframework.accountancy.OrderPaymentEntry;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.order.Order;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsManagement {

	private final BusinessTime businessTime;
	private final ShopOrderManagement shopOrderManagement;

	private final ShopInventoryManagement shopInventoryManagement;

	private final ShopAccountancyManagement shopAccountancyManagement;

	private final RepairManagement repairManagement;


	public static class SellData{
		private final Article article;
		private final int number;

		/**
		 * Constructs a new SellData object representing the sale of a specific article with a given quantity.
		 *
		 * @param article The Article object representing the sold item.
		 * @param number The quantity of the sold item.
		 */
		SellData(Article article, int number){
			this.article = article;
			this.number = number;
		}

		public Article getArticle() {
			return article;
		}

		public int getNumber() {
			return number;
		}
	}


	/**
	 * Constructs a new StatisticsManagement instance with the provided dependencies.
	 *
	 * @param businessTime The BusinessTime instance providing information about the current business time.
	 * @param shopOrderManagement The ShopOrderManagement instance responsible for managing shop orders.
	 * @param shopInventoryManagement The ShopInventoryManagement instance responsible for managing shop inventory.
	 * @param shopAccountancyManagement The ShopAccountancyManagement instance handling accountancy operations.
	 * @param repairManagement The RepairManagement instance responsible for managing repairs.
	 */
	StatisticsManagement(BusinessTime businessTime,
						 ShopOrderManagement shopOrderManagement,
						 ShopInventoryManagement shopInventoryManagement,
						 ShopAccountancyManagement shopAccountancyManagement,
						 RepairManagement repairManagement){

        this.businessTime = businessTime;
        this.shopOrderManagement = shopOrderManagement;
        this.shopInventoryManagement = shopInventoryManagement;
        this.shopAccountancyManagement = shopAccountancyManagement;
        this.repairManagement = repairManagement;
    }

	/**
	 * Sets the nonSeller argument in items to true if they weren't sold in the last month
	 */
	public void findNonSellers(){
		int month = businessTime.getTime().minusMonths(1).getMonthValue();
		int year = businessTime.getTime().minusMonths(1).getYear();

		List<AccountancyEntry> entries = shopAccountancyManagement.findOrderByMonthAndYear(year,month)
			.stream().filter(OrderPaymentEntry.class::isInstance).toList();
		List<Article> sellers = new ArrayList<>();
		List<Article> allArticles = new ArrayList<>(shopInventoryManagement.findAllArticles().stream().toList());

		for(Article article: allArticles){
			InventoryItems<ShopInventoryItem> items = shopInventoryManagement.findByArticleID(article.getId());
			for (ShopInventoryItem item: items){
				item.setNonSeller(false);
				shopInventoryManagement.save(item);
			}


		}

		for(var entry:entries){
			Order order = shopOrderManagement.getOrder(((OrderPaymentEntry) entry).getOrderIdentifier());
			order.getOrderLines().get().forEach(orderLine -> sellers.add(
				shopInventoryManagement.findProduct(orderLine.getProductIdentifier())));
		}
		allArticles.removeAll(sellers);

		for(Article article: allArticles){
			InventoryItems<ShopInventoryItem> items = shopInventoryManagement.findByArticleID(article.getId());
			for (ShopInventoryItem item: items){
				item.setNonSeller(true);
				shopInventoryManagement.save(item);
			}
		}


	}


	/**
	 * Retrieves the number of finished repairs by month and year.
	 *
	 * @return A map of the number of finished repairs by month and year.
	 */

	public long getFinishedRepairsCount() {
		return repairManagement.findAll().stream()
			.filter(Repair::isFinished)
			.count();
	}

	public long getCompletedOrdersCount() {
		Streamable<Order> completedOrders = shopOrderManagement.getCompletedOrders();
		List<Order> completedOrdersList = completedOrders.toList();
		return completedOrdersList.size();
	}


	/**
	 * Retrieves a list of the top-selling articles based on sales quantity.
	 *
	 * @return A list of the top-selling articles and their sales quantities for the current month.
	 */
	public List<SellData> getMostSells(){
		int month = businessTime.getTime().getMonthValue();
		int year = businessTime.getTime().getYear();

		Map<Article,Integer> topTen;
		var mostSells =  new ArrayList<SellData>();
		var sells =  new HashMap<Article, Integer>();

		var entries = shopAccountancyManagement.findOrderByMonthAndYear(year,month)
			.stream().filter(OrderPaymentEntry.class::isInstance);

		for (var entry: entries.toList()){
			Order order = shopOrderManagement.getOrder(((OrderPaymentEntry) entry).getOrderIdentifier());
			for (var orderline: order.getOrderLines().toList()){
				Article article = shopInventoryManagement.findProduct(orderline.getProductIdentifier());
				int number = orderline.getQuantity().getAmount().intValue();
				if (sells.containsKey(article) && number > 0){
					sells.put(article, sells.get(article) + number);
				}else if(number > 0){
					sells.put(article,number);
				}
			}

		}
		topTen =
			sells.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(10)
				.collect(Collectors.toMap(
					Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (int i = 0; i < 10; i++) {
			if (sells.size()-1 < i){
				break;
			}
			mostSells.add(
				new SellData(
					topTen.keySet().stream().toList()
						.get(i),topTen.entrySet().stream()
					.toList().get(i).getValue()));
		}
		return mostSells;
	}


	/**
	 * Retrieves a list of recent order payment entries.
	 *
	 * @return A list containing recent order payment entries.
	 */
	public List<OrderPaymentEntry> getRecentOrders(){
		List<OrderPaymentEntry> orderPaymentEntryList = new ArrayList<>();

		var entries = shopAccountancyManagement.findAll().stream().filter(OrderPaymentEntry.class::isInstance);

		for (var entry: entries.toList()){
			orderPaymentEntryList.add((OrderPaymentEntry)entry);
		}
		return orderPaymentEntryList;
	}


	/**
	 * Retrieves data for a pie chart representing different types of transactions.
	 *
	 * @return A map containing data for a pie chart, where keys represent transaction types and values represent
	 *         the total values for each type.
	 */
	public Map<String, Double> getPieChartData(){
		Map<String, Double> pieData = new LinkedHashMap<>();
		pieData.put("Normal",0.0);
		pieData.put("Repair",0.0);
		pieData.put("Maintenance",0.0);
		pieData.put("GFC",0.0);

		for (AccountancyEntry entry: shopAccountancyManagement.findAll()){

			if (entry instanceof OrderPaymentEntry entryOrder){

				if (!shopOrderManagement.getOrder(entryOrder.getOrderIdentifier()).getChargeLines().get().toList()
					.stream().filter(chargeLine -> chargeLine.getDescription().equals("ORDER")).toList().isEmpty()){
					pieData.put("Normal", pieData.get("Normal") + entry.getValue().getNumber().floatValue());

				}else if (!shopOrderManagement.getOrder(entryOrder.getOrderIdentifier()).getChargeLines().get().toList()
					.stream().filter(chargeLine -> chargeLine.getDescription().equals("REPAIR")).toList().isEmpty()){
					pieData.put("REPAIR", pieData.get("Repair") + entry.getValue().getNumber().floatValue());

				}else if (!shopOrderManagement.getOrder(entryOrder.getOrderIdentifier()).getChargeLines().get().toList()
					.stream().filter(chargeLine -> chargeLine.getDescription().equals("GFC")).toList().isEmpty()) {
					pieData.put("GFC", pieData.get("GFC") + entry.getValue().getNumber().floatValue());

				}
			}else if (entry instanceof MaintenanceAccountancyEntry){
				pieData.put("Maintenance", pieData.get("Maintenance") + entry.getValue().getNumber().floatValue());
			}
		}

		return pieData;
	}

	/**
	 * Retrieves data for a graph representing sales volume across different months.
	 *
	 * @return A map containing data for a graph, where keys represent months,
	 * and values represent the sales volume for each month.
	 */
	public Map<String, Integer> getGraphData(){

		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		Map<String, Integer> graphData = new LinkedHashMap<>();

		for (int i = 0; i < months.length; i++) {
			int salesVolume = (int) shopAccountancyManagement.salesVolumeOfMonth(businessTime.getTime().getYear(),
				i + 1);
			graphData.put(months[i], salesVolume);
		}
		return graphData;
	}



}

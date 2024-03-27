package clockshop.repair;

import clockshop.staff.Employee;
import org.jmolecules.ddd.annotation.Repository;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.List;

@Repository
interface RepairRepository extends CrudRepository<Repair, ProductIdentifier> {

	/**
	 * Retrieves all repair entities in a streamable format.
	 *
	 * @return A streamable collection of Repair entities.
	 */
	@Override
	Streamable<Repair> findAll();

	/**
	 * Finds repair entities by searching for the provided search term in the customer name.
	 *
	 * @param searchTerm The term to search for in the customer name.
	 * @return A list of Repair entities matching the search term in the customer name.
	 */
	List<Repair> findByCustomerNameContainingIgnoreCase(String searchTerm);

}

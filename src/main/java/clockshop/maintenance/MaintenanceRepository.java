package clockshop.maintenance;

import clockshop.maintenance.MaintenanceContract.ContractIdentifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends CrudRepository<MaintenanceContract, ContractIdentifier> {

	/**
	 * Retrieves all maintenance contracts as a streamable collection.
	 *
	 * @return A streamable collection of MaintenanceContract objects.
	 */
	@Override
	Streamable<MaintenanceContract> findAll();

	/**
	 * Retrieves a list of maintenance contracts based on a partial match of the contact person's name, ignoring case.
	 *
	 * @param contactPerson The partial or full name of the contact person.
	 * @return A list of MaintenanceContract objects matching the provided contact person name.
	 */
	List<MaintenanceContract> findByContactPersonContainingIgnoreCase(String contactPerson);

}

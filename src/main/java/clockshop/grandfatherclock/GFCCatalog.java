package clockshop.grandfatherclock;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Grandfather Clocks in the catalog.
 */
@Repository
public interface GFCCatalog extends Catalog<GrandfatherClock> {

	/**
	 * Finds Grandfather Clocks whose names contain the specified case-insensitive search term.
	 *
	 *
	 * @return A streamable collection of Grandfather Clocks matching the search term.
	 */
	Streamable<GrandfatherClock> findByNameContainingIgnoreCase(String searchTerm);

	/**
	 * Checks if an entity with the given name exists, ignoring case sensitivity.
	 *
	 * @param name The name to check for existence.
	 * @return {@code true} if an entity with the given name exists (case-insensitive), otherwise {@code false}.
	 */
	boolean existsByNameIgnoreCase(String name);
}

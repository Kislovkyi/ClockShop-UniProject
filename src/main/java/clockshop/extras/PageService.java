package clockshop.extras;




import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class PageService{

	private PageService(){}

	/**
	 * This calculates the Data for creating Pages on the Website
	 * @param size PageSize
	 * @param page Page
	 * @param searchTerm Searchterm
	 * @param service associated Management
	 * @return {@link PageData}
	 * @param <S> extends PageManager
	 * @param <E> some type of entry
	 */
	public static <S extends PageManager, E> PageData<E> calculatePageData(Integer size,
																		Integer page,
																		String searchTerm,
																		S service){
		int maxPages = 1;
		int totalResults = 0;
		List<E> paginatedResults = new ArrayList<>();

		if (page == null) {
			page = 0;
		}
		if (size == null) {
			size = 5;
		}

		if (searchTerm != null && !searchTerm.isEmpty()) {
			List<E> searchResults = service.search(searchTerm);


			totalResults = searchResults.size();
			maxPages = (int) Math.ceil((double) totalResults / size);
			if (page < 0) {
				page = 0;
			} else if (page >= maxPages && maxPages > 0) {
				page = maxPages - 1;
			}
			int start = page * size;
			int end = Math.min((start + size), totalResults);
			paginatedResults = searchResults.subList(start, end);

		} else {
			double dSize = size;
			double amount = service.findAll().toList().size();
			maxPages = (int) Math.ceil(amount / dSize);


		}

		return new PageData<>(page, maxPages, size, totalResults, paginatedResults);
	}


}






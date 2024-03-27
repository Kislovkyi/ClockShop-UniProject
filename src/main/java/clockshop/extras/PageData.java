package clockshop.extras;

import java.util.List;

public class PageData<E>{

	private int page;
	private int maxPages;
	private int size;
	private int totalResults;
	private List<E> paginatedResults;

	public int getPage() {
		return page;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public int getSize() {
		return size;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public List<E> getPaginatedResults() {
		return paginatedResults;
	}

	PageData(int page, int maxPages, int size, int totalResults , List<E> paginatedResults){
		this.page = page;
		this.maxPages = maxPages;
		this.size = size;
		this.totalResults = totalResults;
		this.paginatedResults = paginatedResults;
	}

}

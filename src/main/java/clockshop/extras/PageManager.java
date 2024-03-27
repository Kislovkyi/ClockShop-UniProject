package clockshop.extras;



import org.springframework.data.util.Streamable;

import java.util.List;


public interface PageManager<E> {

	public List<E> search(String searchTerm);


	public Streamable<E> findAll();
}



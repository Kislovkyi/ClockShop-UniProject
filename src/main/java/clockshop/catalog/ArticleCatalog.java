package clockshop.catalog;

import clockshop.catalog.Article.ArticleType;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * An extension of {@link Catalog} to add specific methods.
 */

@Repository
public interface ArticleCatalog extends Catalog<Article> {


	Sort DEFAULT_SORT = Sort.sort(Article.class).by(Article::getId).descending();

	/**
	 * Returns all {@link Article}s by type ordered by the given {@link Sort}.
	 *
	 * @param type must not be {@literal null}.
	 * @param sort must not be {@literal null}.
	 * @return the articles of the given type, never {@literal null}.
	 */
	Streamable<Article> findByType(ArticleType type, Sort sort);
	/**
	 * Retrieves all {@link Article}s of a specific type, ordered by the default sorting criteria.
	 *
	 * @param type The type of articles to retrieve, must not be {@literal null}.
	 * @return A streamable collection of articles of the specified type, never {@literal null}.
	 */

	default Streamable<Article> findByType(ArticleType type) {
		return findByType(type, DEFAULT_SORT);
	}
	/**
	 * Finds articles by name, ignoring case.
	 *
	 * @param name The name or part of the name to search for.
	 * @return A list of articles whose names match the given search term.
	 */
	List<Article> findByNameContainingIgnoreCase(String name);

}

package clockshop.catalog;

import jdk.jfr.Description;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

/**
 * Tests for {@link Article}
 */
class ArticleTest {

	private Article article;


	@BeforeEach
	@Test
	void testArticleConstructor(){
		article = new Article("articleName",
			Money.of(1, EURO),
			Article.ArticleType.CLOCK,
			"Description",0.1);
		assertNotNull(article);
		assertEquals("articleName", article.getName());
		assertEquals(Money.of(1, EURO), article.getPrice());
		assertEquals("CLOCK", article.getCategories().stream().toList().get(0));
	}


	@Test
	void getDescription() {
		assertEquals("Description",article.getDescription());
	}

	@Test
	void setDescription() {
		article.setDescription("Description2");
		assertEquals("Description2",article.getDescription());
	}

	@Test
	void getDiscount() {
		assertEquals(0.1,article.getDiscount());
	}

	@Test
	void setDiscount() {
		article.setDiscount(0.5);
		assertEquals(0.5,article.getDiscount());
	}
}
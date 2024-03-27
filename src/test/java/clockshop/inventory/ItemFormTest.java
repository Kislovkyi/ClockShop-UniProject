package clockshop.inventory;

import clockshop.catalog.Article;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;
/**
 * Tests for {@link ItemForm}
 */
class ItemFormTest {

	private ItemForm itemForm;

	@BeforeEach
	void setUp() {
		itemForm =  new ItemForm("name", 1, "CLOCK","L:0815","V:5180" ,"Description",0.1);
	}

	@Test
	void getArticleName() {
		assertEquals("name",itemForm.getArticleName());
	}

	@Test
	void getPrice() {
		assertEquals(Money.of(1, EURO),itemForm.getPrice());
	}

	@Test
	void getType() {
		assertEquals(Article.ArticleType.CLOCK,itemForm.getType());
	}



	@Test
	void getDescription() {
		assertEquals("Description",itemForm.getDescription());
	}

	@Test
	void getLocationL() {
		assertEquals("L:0815", itemForm.getLocationL());

	}
	@Test
	void getLocationV() {
		assertEquals("V:5180",itemForm.getLocationV());
	}
}
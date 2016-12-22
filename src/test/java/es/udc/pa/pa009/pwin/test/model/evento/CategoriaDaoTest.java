package es.udc.pa.pa009.pwin.test.model.evento;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class CategoriaDaoTest {

	@Autowired
	private CategoriaDao categoriaDao;

	@Qualifier("sessionFactory")
	@Autowired
	private SessionFactory sessionFactory;


	/**
	 * Método para crear una Categoria en la base de tests para probar las
	 * operaciones de los DAO's
	 * 
	 * @return categoria con el identificador
	 */
	private Categoria insertCategory(String categoryName) {
		Categoria categoria = new Categoria(categoryName);
		categoria.setIdCategoria((Long) sessionFactory.getCurrentSession().save(categoria));

		return categoria;
	}

	@Test
	public void testPR_UN_CD_01() {
		// initialize
		Categoria category1 = insertCategory("categoria prueba 1");
		Categoria category2 = insertCategory("categoria prueba 2");

		// test
		List<Categoria> categoriesFound = categoriaDao.findCategories();

		assertEquals(2, categoriesFound.size());

	}

	/**
	 * Test de comprobación de que non se permite gardar en base de datos duas
	 * categorias co mesmo nome. Debido a que non se permite crear categorias na
	 * aplicacion, o test realizase sobre o método proporcionado pola factoría.
	 */
	@Test(expected = ConstraintViolationException.class)
	public void testPR_UN_CD_02() {
		// test
		Categoria category1 = insertCategory("categoria prueba 1");
		Categoria category2 = insertCategory("categoria prueba 1");
	}
}

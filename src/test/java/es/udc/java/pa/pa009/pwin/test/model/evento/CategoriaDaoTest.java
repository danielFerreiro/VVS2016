package es.udc.java.pa.pa009.pwin.test.model.evento;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
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
	private Categoria insertCategory() {
		Categoria categoria = new Categoria("categoria de prueba");
		categoria.setIdCategoria((Long) sessionFactory.getCurrentSession().save(categoria));

		return categoria;
	}

	@Test
	public void testPR_UN_CD_01() {
		// initialize
		Categoria category1 = insertCategory();
		Categoria category2 = insertCategory();

		// test
		List<Categoria> categoriesFound = categoriaDao.findCategories();

		assertEquals(2, categoriesFound.size());

	}
}

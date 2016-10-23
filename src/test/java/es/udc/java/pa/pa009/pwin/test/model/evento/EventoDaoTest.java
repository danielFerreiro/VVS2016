package es.udc.java.pa.pa009.pwin.test.model.evento;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
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
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class EventoDaoTest {

	@Autowired
	private EventoDao eventoDao;

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

	/**
	 * Método para crear un Evento asociado a una Categoria en la base de tests
	 * para probar las operaciones de los DAO's
	 * 
	 * @param category
	 *            Categoria a la que asociar el evento
	 * @return evento con su identificador y asociado a la categoria
	 */
	private Evento insertEvent(Categoria category) {

		Evento event = new Evento("eventoPrueba", Calendar.getInstance(), category);
		// para evitar problemas con las fechas
		event.getFecha().add(Calendar.SECOND, 1);
		event.setIdEvento((Long) sessionFactory.getCurrentSession().save(event));

		return event;
	}

	@Test
	public void testPR_UN_ED_01() {
		// initialize
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);
		Evento event3 = insertEvent(category);
		Evento event4 = insertEvent(category);

		// set some events with a future date and the others with a past date
		event.getFecha().add(Calendar.HOUR, 1);
		event2.getFecha().add(Calendar.HOUR, 2);
		event3.getFecha().set(Calendar.YEAR, 2014);
		event4.getFecha().set(Calendar.YEAR, 2015);

		eventoDao.save(event);
		eventoDao.save(event2);
		eventoDao.save(event3);
		eventoDao.save(event4);

		// test
		List<Evento> eventsFound = eventoDao.findEvent(category.getIdCategoria(), event.getNombreEvento(), 0, 2);

		assertEquals(2, eventsFound.size());
		assertTrue(eventsFound.contains(event));
		assertTrue(eventsFound.contains(event2));
	}

	@Test
	public void testPR_UN_ED_02() {
		// initialize
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);
		Evento event3 = insertEvent(category);
		Evento event4 = insertEvent(category);

		// set some events with a future date and the others with a past date
		event.getFecha().add(Calendar.HOUR, 1);
		event2.getFecha().add(Calendar.HOUR, 2);
		event3.getFecha().set(Calendar.YEAR, 2014);
		event4.getFecha().set(Calendar.YEAR, 2015);

		eventoDao.save(event);
		eventoDao.save(event2);
		eventoDao.save(event3);
		eventoDao.save(event4);

		// test
		List<Evento> eventsFound = eventoDao.findEvent(category.getIdCategoria(), event.getNombreEvento(), 0, 10);

		assertEquals(2, eventsFound.size());
		assertTrue(eventsFound.contains(event));
		assertTrue(eventsFound.contains(event2));
	}

	@Test
	public void testPR_UN_ED_03() {
		// initialize
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);
		Evento event3 = insertEvent(category);
		Evento event4 = insertEvent(category);

		// set some events with a future date and the others with a past date
		event.getFecha().add(Calendar.HOUR, 1);
		event2.getFecha().add(Calendar.HOUR, 2);
		event3.getFecha().set(Calendar.YEAR, 2014);
		event4.getFecha().set(Calendar.YEAR, 2015);

		eventoDao.save(event);
		eventoDao.save(event2);
		eventoDao.save(event3);
		eventoDao.save(event4);

		// test
		List<Evento> eventsFound = eventoDao.findEvent(category.getIdCategoria(), "immposibleWord", 0, 10);

		assertEquals(0, eventsFound.size());
	}

	@Test
	public void testPR_UN_ED_04() {

		// intialize
		Categoria category = insertCategory("categoria prueba 1");
		Categoria category2 = insertCategory("categoria prueba 2");

		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);
		Evento event3 = insertEvent(category);
		Evento event4 = insertEvent(category2);
		Evento event5 = insertEvent(category2);

		event.getFecha().add(Calendar.YEAR, 1);
		event2.getFecha().add(Calendar.YEAR, 1);
		event3.getFecha().add(Calendar.YEAR, 1);
		event4.getFecha().add(Calendar.YEAR, 1);
		event5.getFecha().add(Calendar.YEAR, 1);

		eventoDao.save(event);
		eventoDao.save(event2);
		eventoDao.save(event3);
		eventoDao.save(event4);
		eventoDao.save(event5);

		// test
		List<Evento> eventsFound = eventoDao.findEvent(category.getIdCategoria(), null, 0, 10);

		assertEquals(3, eventsFound.size());
		assertTrue(eventsFound.contains(event));
		assertTrue(eventsFound.contains(event2));
		assertTrue(eventsFound.contains(event3));
		assertFalse(eventsFound.contains(event4));
		assertFalse(eventsFound.contains(event5));

	}

	@Test
	public void testPR_UN_ED_05() {

		// initialize
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);

		// test
		boolean exists = eventoDao.findDuplicateEvents(event.getNombreEvento(), event.getFecha(),
				category.getIdCategoria());

		assertTrue(exists);

	}
}

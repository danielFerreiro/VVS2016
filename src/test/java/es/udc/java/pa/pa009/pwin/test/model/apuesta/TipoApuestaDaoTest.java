package es.udc.java.pa.pa009.pwin.test.model.apuesta;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class TipoApuestaDaoTest {

	@Autowired
	private TipoApuestaDao tipoApuestaDao;

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

	/**
	 * Método para crear un TipoApuesta con una única opcion ganadora asociado a
	 * un Evento en la base de tests para probar las operaciones de los DAO's
	 * 
	 * @param event
	 *            Evento a la que asociar el TipoApuesta
	 * @return el tipoapuesta con su identificador y el evento asociado
	 */
	private TipoApuesta insertBetTypeWithoutMultipleWinnerOptions(Evento event) {

		TipoApuesta betType = new TipoApuesta(event, "¿pregunta prueba?", false);
		betType.setIdTipo((Long) sessionFactory.getCurrentSession().save(betType));

		return betType;
	}



	@Test
	public void testPR_UN_TAD_01() {

		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);

		boolean exists = tipoApuestaDao.findDuplicateBetTypes(betType.getPregunta(), event.getIdEvento());

		assertTrue(exists);
	}
}

package es.udc.java.pa.pa009.pwin.test.model.apuesta;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
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

import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class OpcionDaoTest {

	@Autowired
	private OpcionDao opcionDao;

	@Qualifier("sessionFactory")
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Método para crear un Usuario en la base de tests para probar las
	 * operaciones de los DAO's
	 * 
	 * @param loginName
	 *            el login del usuario para evitar la restricción de unicidad en
	 *            la base de datos
	 * @return usuario con el identificador
	 */
	private UserProfile insertUser(String loginName) {
		UserProfile user = new UserProfile(loginName, "nanie", "nanie", "nanie", "nanie@nanie.com");

		user.setUserProfileId((Long) sessionFactory.getCurrentSession().save(user));

		return user;
	}

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

	/**
	 * Método para crear una Opcion sin estar marcada asociado a un TipoApuesta
	 * en la base de tests para probar las operaciones de los DAO's
	 * 
	 * @param betType
	 *            TipoApuesta a la que asociar la Opcion
	 * @return opcion con su identificador y asociado al TipoApuesta
	 */
	private Opcion insertOptionWithoutState(TipoApuesta betType) {

		Opcion option = new Opcion("opcion Prueba", 0.5, null, betType);
		option.setIdOpcion((Long) sessionFactory.getCurrentSession().save(option));

		return option;
	}

	/**
	 * Método para crear una Opcion marcada como ganadora asociado a un
	 * TipoApuesta en la base de tests para probar las operaciones de los DAO's
	 * 
	 * @param betType
	 *            TipoApuesta a la que asociar la Opcion
	 * @return opcion con su identificador y asociado al TipoApuesta
	 */
	private Opcion insertWinnerOption(TipoApuesta betType) {

		Opcion option = new Opcion("opcion Prueba", 0.5, true, betType);
		option.setIdOpcion((Long) sessionFactory.getCurrentSession().save(option));

		return option;
	}

	/**
	 * Método para crear una Opcion marcada como perdedora asociado a un
	 * TipoApuesta en la base de tests para probar las operaciones de los DAO's
	 * 
	 * @param betType
	 *            TipoApuesta a la que asociar la Opcion
	 * @return opcion con su identificador y asociado al TipoApuesta
	 */
	private Opcion insertLoserOption(TipoApuesta betType) {

		Opcion option = new Opcion("opcion Prueba", 0.5, false, betType);
		option.setIdOpcion((Long) sessionFactory.getCurrentSession().save(option));

		return option;
	}



	@Test
	public void testPR_UN_OD_01() {
		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		Categoria category = insertCategory();
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);

		TipoApuesta betType10 = insertBetTypeWithoutMultipleWinnerOptions(event);
		TipoApuesta betType20 = insertBetTypeWithoutMultipleWinnerOptions(event2);

		Opcion option11 = insertOptionWithoutState(betType10);
		Opcion option12 = insertOptionWithoutState(betType10);
		Opcion option13 = insertLoserOption(betType10);
		Opcion option21 = insertWinnerOption(betType20);
		Opcion option22 = insertLoserOption(betType20);

		// test
		List<Opcion> winnerOption = opcionDao.showWinners(betType10.getIdTipo());

		assertTrue(winnerOption.isEmpty());

	}

	@Test
	public void testPR_UN_OD_02() {
		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		Categoria category = insertCategory();
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);

		TipoApuesta betType10 = insertBetTypeWithoutMultipleWinnerOptions(event);
		TipoApuesta betType20 = insertBetTypeWithoutMultipleWinnerOptions(event2);

		Opcion option11 = insertWinnerOption(betType10);
		Opcion option12 = insertWinnerOption(betType10);
		Opcion option13 = insertLoserOption(betType10);
		Opcion option21 = insertWinnerOption(betType20);
		Opcion option22 = insertLoserOption(betType20);

		// test
		List<Opcion> winnerOptions = opcionDao.showWinners(betType10.getIdTipo());

		assertEquals(2, winnerOptions.size());
		assertTrue(winnerOptions.contains(option11));
		assertTrue(winnerOptions.contains(option12));
	}
}

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

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.ApuestaDao;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class ApuestaDaoTest {

	@Autowired
	private ApuestaDao apuestaDao;

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
	 * Método para crear una Apuesta asociada a un usuario y sobre una opción en
	 * la base de tests para poder realizar las operaciones de los DAO's
	 * 
	 * @param user
	 *            Usuario que realiza la apuesta
	 * @param option
	 *            Opcion sobre la que se quiere realizar la apuesta
	 * @return apuesta con su identificador y asociada al usuario y a la opcion
	 */
	private Apuesta insertApuesta(UserProfile user, Opcion option) {

		Apuesta bet = new Apuesta(Calendar.getInstance(), user, 20.0, option);
		bet.setIdApuesta((Long) sessionFactory.getCurrentSession().save(bet));

		return bet;
	}

	@Test
	public void testPR_UN_AD_01() {
		// initialize
		UserProfile user = insertUser("usuario1");
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);
		Opcion option = insertOptionWithoutState(betType);

		// test
		Apuesta bet = new Apuesta(Calendar.getInstance(), user, 20.0, option);
		apuestaDao.save(bet);
	}

	@Test
	public void testPR_UN_AD_02() {
		// initialize
		UserProfile user = insertUser("usuario1");
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);
		Opcion option = insertOptionWithoutState(betType);
		Apuesta bet = insertApuesta(user, option);

		// test
		bet.setApostado(666.00);
		apuestaDao.save(bet);
	}

	@Test
	public void testPR_UN_AD_03() throws InstanceNotFoundException {
		// initialize
		UserProfile user = insertUser("usuario1");
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);
		Opcion option = insertOptionWithoutState(betType);
		Apuesta bet = insertApuesta(user, option);

		// test
		Apuesta betFound = apuestaDao.find(bet.getIdApuesta());
		assertEquals(betFound, bet);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testPR_UN_AD_04() throws InstanceNotFoundException {
		Apuesta apuestaEncontrada = apuestaDao.find(new Long(-1));
	}

	@Test
	public void testPR_UN_AD_05() {

	}

	@Test
	public void testPR_UN_AD_06() {

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testPR_UN_AD_07() throws InstanceNotFoundException {
		apuestaDao.remove(new Long(-1));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testPR_UN_AD_08() throws InstanceNotFoundException {

		// initialize
		UserProfile user = insertUser("usuario1");
		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);
		Opcion option = insertOptionWithoutState(betType);

		// test
		Apuesta bet = new Apuesta(Calendar.getInstance(), user, 20.0, option);

		apuestaDao.save(bet);
		apuestaDao.find(bet.getIdApuesta());
		apuestaDao.remove(bet.getIdApuesta());
		apuestaDao.find(bet.getIdApuesta());

	}

	@Test
	public void testPR_UN_AD_09() {

		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);

		TipoApuesta betType10 = insertBetTypeWithoutMultipleWinnerOptions(event);
		TipoApuesta betType20 = insertBetTypeWithoutMultipleWinnerOptions(event2);

		Opcion option10 = insertOptionWithoutState(betType10);
		Opcion option11 = insertOptionWithoutState(betType10);
		Opcion option12 = insertOptionWithoutState(betType10);
		Opcion option20 = insertOptionWithoutState(betType20);
		Opcion option21 = insertOptionWithoutState(betType20);

		Apuesta bet1 = insertApuesta(user, option11);
		Apuesta bet2 = insertApuesta(user2, option12);
		Apuesta bet3 = insertApuesta(user, option20);
		Apuesta bet4 = insertApuesta(user, option10);

		bet1.getFecha().add(Calendar.SECOND, 2);
		bet3.getFecha().add(Calendar.SECOND, 5);
		bet4.getFecha().add(Calendar.SECOND, 8);

		// modify the dates to check if the method find the bets ordered by date
		apuestaDao.save(bet1);
		apuestaDao.save(bet3);
		apuestaDao.save(bet4);

		// test
		List<Apuesta> foundBets = apuestaDao.findApuestasByIdUsuario(user.getUserProfileId(), 0, 2);

		assertEquals(2, foundBets.size());

		for (int index = foundBets.size() - 1; index > 0; index--) {
			Apuesta bet = foundBets.get(index);
			Apuesta beforeBet = foundBets.get(index - 1);

			assertTrue(bet.getFecha().after(beforeBet.getFecha()));
		}

	}

	@Test
	public void testPR_UN_AD_10() {

		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);

		TipoApuesta betType10 = insertBetTypeWithoutMultipleWinnerOptions(event);
		TipoApuesta betType20 = insertBetTypeWithoutMultipleWinnerOptions(event2);

		Opcion option10 = insertOptionWithoutState(betType10);
		Opcion option11 = insertOptionWithoutState(betType10);
		Opcion option12 = insertOptionWithoutState(betType10);
		Opcion option20 = insertOptionWithoutState(betType20);
		Opcion option21 = insertOptionWithoutState(betType20);

		Apuesta bet1 = insertApuesta(user, option11);
		Apuesta bet2 = insertApuesta(user2, option12);
		Apuesta bet3 = insertApuesta(user, option20);

		bet1.getFecha().add(Calendar.SECOND, 2);
		bet3.getFecha().add(Calendar.SECOND, 5);

		// modify the dates to check if the method find the bets ordered by date
		apuestaDao.save(bet1);
		apuestaDao.save(bet3);

		// test
		List<Apuesta> foundBets = apuestaDao.findApuestasByIdUsuario(user.getUserProfileId(), 0, 4);

		assertEquals(2, foundBets.size());

		for (int index = foundBets.size() - 1; index > 0; index--) {
			Apuesta bet = foundBets.get(index);
			Apuesta beforeBet = foundBets.get(index - 1);

			assertTrue(bet.getFecha().after(beforeBet.getFecha()));
		}

	}

	@Test
	public void testPR_UN_AD_11() {

		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		Categoria category = insertCategory("categoria prueba 1");
		Evento event = insertEvent(category);
		Evento event2 = insertEvent(category);

		TipoApuesta betType10 = insertBetTypeWithoutMultipleWinnerOptions(event);
		TipoApuesta betType20 = insertBetTypeWithoutMultipleWinnerOptions(event2);

		Opcion option10 = insertOptionWithoutState(betType10);
		Opcion option11 = insertOptionWithoutState(betType10);
		Opcion option12 = insertOptionWithoutState(betType10);
		Opcion option20 = insertOptionWithoutState(betType20);
		Opcion option21 = insertOptionWithoutState(betType20);

		Apuesta bet1 = insertApuesta(user, option11);
		Apuesta bet2 = insertApuesta(user2, option12);
		Apuesta bet3 = insertApuesta(user, option20);
		Apuesta bet4 = insertApuesta(user, option10);

		bet1.getFecha().add(Calendar.SECOND, 2);
		bet3.getFecha().add(Calendar.SECOND, 5);
		bet4.getFecha().add(Calendar.SECOND, 8);

		// test
		int numberOfBets = apuestaDao.findNumberOfBets(user.getUserProfileId());

		assertEquals(3, numberOfBets);
	}

	@Test
	public void testPR_UN_AD_12() {

		// initialize
		UserProfile user = insertUser("usuario1");
		UserProfile user2 = insertUser("usuario2");

		// test
		int numberOfBets = apuestaDao.findNumberOfBets(user.getUserProfileId());

		assertEquals(0, numberOfBets);

	}
}

package es.udc.pa.pa009.pwin.test.vvs;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.AdminServiceImpl;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.ApuestaDao;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.BetServiceImpl;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfileDao;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class VVSTest {

	@InjectMocks
	private AdminService adminServiceMockito = new AdminServiceImpl();

	@InjectMocks
	private BetService betServiceMockito = new BetServiceImpl();

	@Mock
	private ApuestaDao apuestaDaoMockito;

	@Mock
	private CategoriaDao categoriaDaoMockito;

	@Mock
	private TipoApuestaDao tipoApuestaDaoMockito;

	@Mock
	private OpcionDao opcionDaoMockito;

	@Mock
	private UserProfileDao userProfileDaoMockito;

	@Mock
	private EventoDao eventoDaoMockito;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@InjectMocks
	@Autowired
	private ApuestaDao apuestaDao;

	@InjectMocks
	@Autowired
	private CategoriaDao categoriaDao;

	@InjectMocks
	@Autowired
	private TipoApuestaDao tipoApuestaDao;

	@InjectMocks
	@Autowired
	private OpcionDao opcionDao;

	@InjectMocks
	@Autowired
	private EventoDao eventoDao;

	@InjectMocks
	@Autowired
	private UserProfileDao userProfileDao;

	@InjectMocks
	@Autowired
	private AdminService adminService;

	@InjectMocks
	@Autowired
	private BetService betService;

	@Qualifier("sessionFactory")
	@Autowired
	private SessionFactory sessionFactory;


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

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
	 * Método para crear un TipoApuesta con múltiples opciones ganadoras
	 * asociado a un Evento en la base de tests para probar las operaciones de
	 * los DAO's
	 * 
	 * @param event
	 *            Evento a la que asociar el TipoApuesta
	 * @return el tipoapuesta con su identificador y el evento asociado
	 */
	private TipoApuesta insertBetTypeWithMultipleWinnerOptions(Evento event) {

		TipoApuesta betType = new TipoApuesta(event, "¿pregunta prueba?", true);
		betType.setIdTipo((Long) sessionFactory.getCurrentSession().save(betType));

		return betType;
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



	/**
	 * Este test se debería ejecutar siempre uno de los primeros, puesto que los
	 * demás dependen de el.
	 */
	@Test
	public void testPR_UN_AD_01() {
		// initialize
		UserProfile user = insertUser("usuario1");
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
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

		Categoria category = insertCategory();
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

		Categoria category = insertCategory();
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

		Categoria category = insertCategory();
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

	@Test
	public void testPR_UN_TAD_01() {

		Categoria category = insertCategory();
		Evento event = insertEvent(category);
		TipoApuesta betType = insertBetTypeWithoutMultipleWinnerOptions(event);

		boolean exists = tipoApuestaDao.findDuplicateBetTypes(betType.getPregunta(), event.getIdEvento());

		assertTrue(exists);
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

	@Test
	public void testPR_UN_ED_01() {
		// initialize
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
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
		Categoria category = insertCategory();
		Categoria category2 = insertCategory();

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
		Categoria category = insertCategory();
		Evento event = insertEvent(category);

		// test
		boolean exists = eventoDao.findDuplicateEvents(event.getNombreEvento(), event.getFecha(),
				category.getIdCategoria());

		assertTrue(exists);

	}

	@Test
	public void testPR_UN_AS_01()
			throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException {
		// initialize
		Categoria c = new Categoria("categoria prueba");
		c.setIdCategoria(new Long(20));

		Evento evento = new Evento("eventoPrueba",Calendar.getInstance(),c);
		evento.getFecha().add(Calendar.HOUR, 1);

		// test
		adminServiceMockito.createEvent(evento, c.getIdCategoria());


		verify(eventoDaoMockito, times(1)).save(evento);

	}

	@Test
	public void testPR_UN_AS_02()
			throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException {

		// initialize
		thrown.expect(DuplicateInstanceException.class);

		Categoria categoria = new Categoria("categoriaMockito");
		Evento evento = new Evento("eventoMockito", Calendar.getInstance(), categoria);
		evento.getFecha().add(Calendar.SECOND, 1);

		when(eventoDaoMockito.findDuplicateEvents(evento.getNombreEvento(), evento.getFecha(),
				categoria.getIdCategoria())).thenReturn(true);

		// test
		adminServiceMockito.createEvent(evento, categoria.getIdCategoria());

		verify(eventoDaoMockito, times(1)).save(evento);

	}

	@Test
	public void testPR_UN_AS_03() {

	}

	@Test
	public void testPR_UN_AS_04() {

	}

	@Test
	public void testPR_UN_AS_05() {

	}

	@Test
	public void testPR_UN_AS_06() {

	}

	@Test
	public void testPR_UN_BS_01() {

	}

	@Test
	public void testPR_UN_BS_02() {

	}

	@Test
	public void testPR_UN_BS_03() {

	}

}

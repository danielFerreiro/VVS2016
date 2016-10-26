package es.udc.java.pa.pa009.pwin.test.model;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.AlreadySetOptionException;
import es.udc.pa.pa009.pwin.model.adminservice.IllegalParameterException;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.adminservice.NotMultipleOptionsException;
import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.ApuestaBlock;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.NegativeAmountException;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userservice.UserProfileDetails;
import es.udc.pa.pa009.pwin.model.userservice.UserService;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class IntegrationTest {

	@Autowired
	private CategoriaDao categoriaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private UserService userService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private BetService betService;

	/**
	 * Metodo para poder crear en base de datos una Categoria y devolver la
	 * misma para poder realizar consultas en base a su ID,el cual no se puede
	 * recuperar de otra forma
	 * 
	 */
	private Categoria createCategory(String name) {

		Categoria category = new Categoria(name);
		categoriaDao.save(category);
		return category;
	}

	/**
	 * Metodo para crear en base de datos un usuario, devolviendo posteriormente
	 * el mismo para poder realizar operaciones en base a su ID, puesto que es
	 * el unico método para recuperar los datos de un usuario
	 * 
	 */
	private UserProfile createUser() throws DuplicateInstanceException {
		UserProfileDetails userProfileDetails = new UserProfileDetails("test", "integracion", "test@test");

		return userService.registerUser("test", "test", userProfileDetails);
	}

	/**
	 * Metodo para insertar en base de datos un evento pasado para poder
	 * comprobar que las busquedas filtran los eventos pasados. Sin embargo, no
	 * se permite crear eventos en el pasado, por lo que la unica manera de
	 * probarlo es introduciendo eventos a través de este método
	 * 
	 */
	private void insertPastEvent(String eventName, Categoria category) {
		Evento event = new Evento(eventName, Calendar.getInstance(), category);
		event.getFecha().set(Calendar.YEAR, 2015);

		eventoDao.save(event);
	}

	@Test
	public void testPR_IN_01() throws DuplicateInstanceException, InstanceNotFoundException, InvalidEventDateException {
		// initialize
		Categoria category = createCategory("categoria de test");

		// test
		Evento event = new Evento("evento prueba", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.HOUR, 1);
		event = adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test?", false);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option1 = new Opcion("opcion1", 20.0, null, betType);
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		optionsToAdd.add(option1);
		optionsToAdd.add(option2);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Opcion addedOption = adminService.addOption(betType.getIdTipo(), "opcion annadida", 333.0, null);

		List<Evento> eventsFound = adminService.findAllEvents(category.getIdCategoria(), null);

		// assertions
		assertEquals(1, eventsFound.size());
		assertTrue(eventsFound.contains(event));
		Evento eventFound = eventsFound.get(eventsFound.indexOf(event));

		assertTrue(eventFound.getTipos().contains(betType));
		TipoApuesta betTypeFound = eventFound.getTipos().get(eventFound.getTipos().indexOf(betType));

		assertEquals(3, betTypeFound.getOpciones().size());
		assertTrue(betTypeFound.getOpciones().contains(option1));
		assertTrue(betTypeFound.getOpciones().contains(option2));
		assertTrue(betTypeFound.getOpciones().contains(addedOption));

	}

	@Test
	public void testPR_IN_02() throws DuplicateInstanceException, InstanceNotFoundException, InvalidEventDateException,
	ExpiredEventException, NegativeAmountException {
		// initialize
		Categoria category = createCategory("categoria de test");
		UserProfile user = createUser();

		// test
		Evento event = new Evento("evento test", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.HOUR, 1);
		event = adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test", false);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option1 = new Opcion("opcion1", 20.0, null, betType);
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		optionsToAdd.add(option1);
		optionsToAdd.add(option2);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Opcion betOption = betType.getOpciones().get(0);
		Apuesta bet = betService.bet(betOption.getIdOpcion(), 20.0, user.getUserProfileId());

		ApuestaBlock userBets = betService.checkBet(user.getUserProfileId(), 0, 20);

		// assertions
		assertTrue(userBets.getApuestas().contains(bet));
		Apuesta betDone = userBets.getApuestas().get(userBets.getApuestas().indexOf(bet));

		assertEquals(betOption, betDone.getOpcionElegida());
		assertEquals(user, betDone.getUsuario());
	}

	@Test
	public void testPR_IN_03() throws DuplicateInstanceException, InstanceNotFoundException, InvalidEventDateException {

		// initialize
		Categoria category = createCategory("categoria de test");

		// test
		Evento event1 = new Evento("evento test 1", Calendar.getInstance(), category);
		Evento event2 = new Evento("evento test 2", Calendar.getInstance(), category);
		event1.getFecha().add(Calendar.HOUR, 2);
		event2.getFecha().add(Calendar.HOUR, 1);

		Evento event1Created = adminService.createEvent(event1, category.getIdCategoria());
		Evento event2Created = adminService.createEvent(event2, category.getIdCategoria());


		// to can test if the user loads only the future events
		insertPastEvent("evento test pasado", category);

		Calendar now = Calendar.getInstance();
		List<Evento> eventsFound = betService.findEvent(category.getIdCategoria(), null, 0, 20).getEventos();

		// assertions
		assertEquals(2, eventsFound.size());
		assertTrue(eventsFound.contains(event1Created));
		assertTrue(eventsFound.contains(event2Created));
	}

	@Test
	public void testPR_IN_04() throws DuplicateInstanceException, InstanceNotFoundException, InvalidEventDateException,
	ExpiredEventException, NegativeAmountException, IllegalParameterException, NotMultipleOptionsException,
	AlreadySetOptionException, InterruptedException {
		// initialize
		Categoria category = createCategory("categoria de test");
		UserProfile user = createUser();

		// test
		Evento event = new Evento("evento test", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.SECOND, 3);
		event = adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test", false);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option1 = new Opcion("opcion1", 20.0, null, betType);
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		optionsToAdd.add(option1);
		optionsToAdd.add(option2);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Opcion addedOption = adminService.addOption(betType.getIdTipo(), "opcion annadida", 333.0, null);

		Apuesta bet = betService.bet(option2.getIdOpcion(), 111.0, user.getUserProfileId());

		List<Long> winnerOptions = new ArrayList<>();
		winnerOptions.add(option2.getIdOpcion());

		// need to wait until the event has passed to cant mark it as Winner
		Thread.sleep(3000);
		adminService.markAsWinner(winnerOptions, betType.getIdTipo());

		List<Apuesta> userBets = betService.checkBet(user.getUserProfileId(), 0, 20).getApuestas();

		// assertions
		assertTrue(userBets.contains(bet));
		Apuesta betDone = userBets.get(userBets.indexOf(bet));

		assertTrue(betDone.getOpcionElegida().getEstado());
	}

	@Test(expected = ExpiredEventException.class)
	public void testPR_IN_05() throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException,
	ExpiredEventException, NegativeAmountException, InterruptedException {

		// initialize
		Categoria category = createCategory("categoria de test");
		UserProfile user = createUser();

		// test
		Evento event = new Evento("evento de test", Calendar.getInstance(), category);
		event.getFecha().add(Calendar.SECOND, 1);
		adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test", false);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		optionsToAdd.add(option2);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Thread.sleep(1000);
		betService.bet(option2.getIdOpcion(), 10.0, user.getUserProfileId());

	}

	@Test
	public void testPR_IN_06() throws DuplicateInstanceException, InstanceNotFoundException, InvalidEventDateException,
	ExpiredEventException, IllegalParameterException, NotMultipleOptionsException, AlreadySetOptionException,
	InterruptedException {
		// initialize
		Categoria category = createCategory("categoria de test");

		// test
		Evento event = new Evento("evento test", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.SECOND, 1);
		event = adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test", true);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option1 = new Opcion("opcion1", 20.0, null, betType);
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		Opcion option3 = new Opcion("opcion 3", 14.0, null, betType);
		optionsToAdd.add(option1);
		optionsToAdd.add(option2);
		optionsToAdd.add(option3);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Thread.sleep(1000);

		List<Long> winnerOptions = new ArrayList<>();
		winnerOptions.add(option1.getIdOpcion());
		winnerOptions.add(option2.getIdOpcion());
		adminService.markAsWinner(winnerOptions, betType.getIdTipo());

		List<Opcion> winnerOptionsFound = betService.showWinners(betType.getIdTipo());

		// assertions
		assertEquals(2, winnerOptionsFound.size());
		for (Opcion op : winnerOptionsFound) {
			assertTrue(op.getEstado());
		}

	}

	@Test(expected = NotMultipleOptionsException.class)
	public void testPR_IN_07() throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException,
	InterruptedException, ExpiredEventException, IllegalParameterException, NotMultipleOptionsException,
	AlreadySetOptionException {
		// initialize
		Categoria category = createCategory("categoria de test");

		// test
		Evento event = new Evento("evento test", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.SECOND, 1);
		event = adminService.createEvent(event, category.getIdCategoria());

		TipoApuesta betType = new TipoApuesta(event, "pregunta test", false);
		List<Opcion> optionsToAdd = new ArrayList<>();
		Opcion option1 = new Opcion("opcion1", 20.0, null, betType);
		Opcion option2 = new Opcion("opcion2", 666.0, null, betType);
		Opcion option3 = new Opcion("opcion 3", 14.0, null, betType);
		optionsToAdd.add(option1);
		optionsToAdd.add(option2);
		optionsToAdd.add(option3);
		betType = adminService.addBettingType(betType, event.getIdEvento(), optionsToAdd);

		Thread.sleep(1000);

		List<Long> winnerOptions = new ArrayList<>();
		winnerOptions.add(option1.getIdOpcion());
		winnerOptions.add(option2.getIdOpcion());
		adminService.markAsWinner(winnerOptions, betType.getIdTipo());

	}

}

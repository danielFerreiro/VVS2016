package es.udc.pa.pa009.pwin.test.model.betservice;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.ApuestaDao;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.betservice.ApuestaBlock;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfileDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class BetServiceTest {
	
	
	@Autowired
	private BetService betService;

	@Autowired
	private CategoriaDao categoriaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private TipoApuestaDao tipoApuestaDao;

	@Autowired
	private OpcionDao opcionDao;

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private ApuestaDao apuestaDao;

	@Test
	public void testFindAllCategories() {

		Categoria c = new Categoria("categoria1");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("categoria2");
		categoriaDao.save(c2);

		Categoria c3 = new Categoria("esto ya es vicio");
		categoriaDao.save(c3);

		List<Categoria> categorias = betService.findAllCategories();

		assertEquals(categorias.size(), 3);
		assertTrue(categorias.contains(c));
		assertTrue(categorias.contains(c2));
		assertTrue(categorias.contains(c3));

	}

	// ------------------------------

	@Test
	public void testFindOptionById() throws InstanceNotFoundException,
			ExpiredEventException {

		Categoria c = new Categoria("nombreCategoria");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2018);

		Evento evento = new Evento("evento1", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", false);
		tipoApuestaDao.save(tipo);

		Opcion o = new Opcion("opcion1", new Double(2), null, tipo);
		opcionDao.save(o);

		Opcion opcionEncontrada = betService.findOptionById(o.getIdOpcion());

		assertEquals(o, opcionEncontrada);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindOptionByIdConIdInexistente()
			throws InstanceNotFoundException, ExpiredEventException {

		Opcion opcionInexistente = betService.findOptionById(new Long(1245));

	}

	@Test(expected = ExpiredEventException.class)
	public void testFindOptionByIdEventoPasado()
			throws InstanceNotFoundException, ExpiredEventException {
		
		Categoria c = new Categoria("nombreCategoria");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2015);
		
		Evento evento = new Evento("evento1", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", false);
		tipoApuestaDao.save(tipo);

		Opcion o = new Opcion("opcion1", new Double(2), null, tipo);
		opcionDao.save(o);

		Opcion opcionEncontrada = betService.findOptionById(o.getIdOpcion());

		assertEquals(o, opcionEncontrada);
	}
	
	// --------------------------------

	@Test
	public void findEventTodosYFiltraPorFecha() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria 2");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2017);

		Evento evento1 = new Evento("Evento1", fecha, c);
		Evento evento2 = new Evento("Evento2", fecha, c);
		Evento evento3 = new Evento("Evento3", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		
		EventoBlock eventos = betService.findEvent(null, null, 0, 10);
		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 3);

		assertTrue(eventosEncontrados.contains(evento1));
		assertTrue(eventosEncontrados.contains(evento2));
		assertTrue(eventosEncontrados.contains(evento3));


	}

	@Test
	public void testFindEventPorCategoria() {

		Categoria c = new Categoria("Categori");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria 2");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2017);

		Evento evento1 = new Evento("Evento1", fecha, c);
		Evento evento2 = new Evento("Evento2", fecha, c);
		Evento evento3 = new Evento("Evento3", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		
		EventoBlock eventos = betService.findEvent(c.getIdCategoria(), null, 0,
				10);
		List<Evento> eventosEncontrados = eventos.getEventos();

		
		assertEquals(2,eventosEncontrados.size());



		assertTrue(eventosEncontrados.contains(evento1));
		assertTrue(eventosEncontrados.contains(evento2));
		assertFalse(eventosEncontrados.contains(evento3));
		
	}

	@Test
	public void testFindEventPorPalabras() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria 2");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2017);

		Evento evento1 = new Evento("Evento1", fecha, c);
		Evento evento2 = new Evento("nombre inventado", fecha, c);
		Evento evento3 = new Evento("Evento3", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		

		EventoBlock eventos = betService.findEvent(null, "evento", 0, 10);
		List<Evento> eventosEncontrados = eventos.getEventos();

		
		assertEquals(eventosEncontrados.size(), 2);

		assertTrue(eventosEncontrados.contains(evento1));
		assertFalse(eventosEncontrados.contains(evento2));
		assertTrue(eventosEncontrados.contains(evento3));


	}

	@Test
	public void testFindEventPorCategoriaYPalabras() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria 2");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2017);

		Evento evento1 = new Evento("Evento1", fecha, c);
		Evento evento2 = new Evento("nombre inventado", fecha, c);
		Evento evento3 = new Evento("Evento3", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		

		EventoBlock eventos = betService.findEvent(c.getIdCategoria(),
				"evento", 0, 10);
		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 1);

		assertTrue(eventosEncontrados.contains(evento1));
		assertFalse(eventosEncontrados.contains(evento2));
		assertFalse(eventosEncontrados.contains(evento3));


	}

	// --------------------------------------------

	@Test
	public void testBet() throws ExpiredEventException,
			InstanceNotFoundException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR,2018);

		Evento evento = new Evento("Evento1", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", true);
		tipoApuestaDao.save(tipo);

		Opcion op = new Opcion("Opcion de ejemplo", new Double(2), null, tipo);
		opcionDao.save(op);

		UserProfile usuario = new UserProfile("nanie", "hoal", "daniel",
				"ferreiro", "nanie@correo.com");
		userProfileDao.save(usuario);

		Apuesta apuestaRealizada = betService.bet(op.getIdOpcion(), new Double(
				25), usuario.getUserProfileId());

		Apuesta apuestaEncontrada = apuestaDao.find(apuestaRealizada.getIdApuesta());
		
		Opcion opcionApostada = opcionDao.find(apuestaEncontrada
				.getOpcionElegida().getIdOpcion());

		TipoApuesta tipoApuestaApostado = opcionApostada.getTipoApuesta();

		Evento eventoApostado = tipoApuestaApostado.getEvento();

		
		assertEquals(apuestaRealizada.getIdApuesta(), apuestaEncontrada.getIdApuesta());
	
		
		

	}

	@Test(expected = ExpiredEventException.class)
	public void testBetEventoExpirado() throws ExpiredEventException,
			InstanceNotFoundException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fechaPasada = Calendar.getInstance();
		fechaPasada.set(Calendar.YEAR, 2014);

		Evento evento = new Evento("Evento1", fechaPasada, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", false);
		tipoApuestaDao.save(tipo);

		Opcion op = new Opcion("los buenos", new Double(5), null, tipo);
		opcionDao.save(op);

		UserProfile usuario = new UserProfile("nanie", "hoal", "daniel",
				"ferreiro", "nanie@correo.com");
		userProfileDao.save(usuario);

		Apuesta apuestaImposible = betService.bet(op.getIdOpcion(), new Double(
				20), usuario.getUserProfileId());

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testBetOpcionInexistente() throws ExpiredEventException,
			InstanceNotFoundException {

		Apuesta apuestaImposible = betService.bet(new Long(2141),
				new Double(2), null);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testBetUsuarioInexistente() throws ExpiredEventException,
			InstanceNotFoundException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR,2018);

		Evento evento = new Evento("Evento1", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", true);
		tipoApuestaDao.save(tipo);

		Opcion op = new Opcion("Opcion de ejemplo", new Double(2), null, tipo);
		opcionDao.save(op);

		Apuesta apuestaRealizada = betService.bet(op.getIdOpcion(), new Double(
				25), new Long(2145));

	}

	// -----------------------------------

	@Test
	public void testCheckBet() throws ExpiredEventException,
			InstanceNotFoundException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR,2018);

		Evento evento = new Evento("Evento1", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganara?", true);
		tipoApuestaDao.save(tipo);

		Opcion op = new Opcion("Opcion de ejemplo", new Double(2), null, tipo);
		opcionDao.save(op);

		Opcion op2 = new Opcion("opcion 2 de ejemplo", new Double(2), null,
				tipo);
		opcionDao.save(op2);

		UserProfile usuario = new UserProfile("nanie", "hoal", "daniel",
				"ferreiro", "nanie@correo.com");
		userProfileDao.save(usuario);

		UserProfile usuario2 = new UserProfile("paco", "hoal", "poa",
				"yoquese", "paco@arobar.com");
		userProfileDao.save(usuario2);

		Apuesta apuesta1 = betService.bet(op.getIdOpcion(), new Double(25),
				usuario.getUserProfileId());

		Apuesta apuesta2 = betService.bet(op2.getIdOpcion(), new Double(14),
				usuario.getUserProfileId());

		Apuesta apuesta3 = betService.bet(op2.getIdOpcion(), new Double(18),
				usuario2.getUserProfileId());

		ApuestaBlock apuestas = betService.checkBet(usuario.getUserProfileId(),
				0, 10);
		List<Apuesta> apuestasRealizadas = apuestas.getApuestas();
		
		assertEquals(apuestasRealizadas.size(), 2);
		assertTrue(apuestasRealizadas.contains(apuesta1));
		assertTrue(apuestasRealizadas.contains(apuesta2));
		assertFalse(apuestasRealizadas.contains(apuesta3));
		

	}

	// ---------------------

	@Test
	public void testFindEventById() throws InstanceNotFoundException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();

		Evento evento = new Evento("nombre", fecha, c);
		eventoDao.save(evento);

		Evento eventoEncontrado = betService
				.findEventById(evento.getIdEvento());

		assertEquals(evento, eventoEncontrado);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindEventByIdEventoInexistente()
			throws InstanceNotFoundException {

		Evento eventoImposible = betService.findEventById(new Long(1245));

	}
	
	
}

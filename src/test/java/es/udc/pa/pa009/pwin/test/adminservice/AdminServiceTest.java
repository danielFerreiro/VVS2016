package es.udc.pa.pa009.pwin.test.adminservice;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.AlreadySetOptionException;
import es.udc.pa.pa009.pwin.model.adminservice.IllegalParameterException;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.adminservice.NotMultipleOptionsException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class AdminServiceTest {

	// @Rollback(false) <- para que non faga rolback e poder ver a BD (executar
	// mvn test para borralos)

	@Autowired
	private AdminService adminService;

	@Autowired
	private OpcionDao opcionDao;

	@Autowired
	private TipoApuestaDao tipoApuestaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private CategoriaDao categoriaDao;

	@Test
	public void testCreateEvent() throws InstanceNotFoundException,
			DuplicateInstanceException, InvalidEventDateException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar siguiente = Calendar.getInstance();
		siguiente.add(Calendar.MONTH, 1);

		Evento e = new Evento("Deportivo-Sevilla", siguiente, null);

		Evento eventoCreado = adminService.createEvent(e, c.getIdCategoria());

		Evento eventoEncontrado = eventoDao.find(eventoCreado.getIdEvento());

		assertEquals(eventoCreado, eventoEncontrado);

	}

	@Test(expected = InvalidEventDateException.class)
	public void testCreateEventFechaInvalida()
			throws InstanceNotFoundException, DuplicateInstanceException,
			InvalidEventDateException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar anterior = Calendar.getInstance();
		anterior.set(Calendar.YEAR, 2013);

		Evento evento = new Evento("Deportivo - Sevilla", anterior, c);

		Evento eventoCreado = adminService.createEvent(evento,
				c.getIdCategoria());

	}

	// Dos eventos son iguales cuando tienen mismo nombre, misma fecha ¿y misma
	// categoria?

	@Test(expected = DuplicateInstanceException.class)
	public void testCreateEventDuplicado() throws InstanceNotFoundException,
			DuplicateInstanceException, InvalidEventDateException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria distinta");
		categoriaDao.save(c2);

		Calendar fechaRepetida = Calendar.getInstance();
		fechaRepetida.set(Calendar.YEAR, 2018);

		String nombreRepetido = "Evento de Ejemplo";

		Evento evento = new Evento(nombreRepetido, fechaRepetida, c);
		Evento eventoOriginal = adminService.createEvent(evento,
				c.getIdCategoria());

		Evento eventoCreado = adminService.createEvent(evento,
				c.getIdCategoria());

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testCreateEventConCategoriaInexistente()
			throws InstanceNotFoundException, DuplicateInstanceException,
			InvalidEventDateException {

		Calendar hoy = Calendar.getInstance();
		hoy.set(Calendar.YEAR, 2018);

		Categoria c = new Categoria("Cat no afecta");
		categoriaDao.save(c);

		Evento evento = new Evento("nombreEvento", hoy, c);

		Evento eventoCreado = adminService.createEvent(evento, new Long(52141));

	}

	// ------------------------

	@Test
	public void testFindEventTodos() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria de ej");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento1 = new Evento("Evento1", fecha, c);

		fecha.set(Calendar.YEAR, 2017);
		Evento evento2 = new Evento("Evento2", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);

		EventoBlock eventos = adminService.findEvent(null, null, 0, 10);

		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 2);
		assertTrue(eventosEncontrados.contains(evento1));
		assertTrue(eventosEncontrados.contains(evento2));

	}

	@Test
	public void testFindEventPorCategoria() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria2");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento1 = new Evento("Evento1", fecha, c);

		fecha.set(Calendar.YEAR, 2017);
		Evento evento2 = new Evento("Evento2", fecha, c2);

		fecha.set(Calendar.YEAR, 2014);
		Evento evento3 = new Evento("Evento3", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		EventoBlock eventos = adminService.findEvent(c2.getIdCategoria(), null,
				0, 10);

		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 2);

		assertFalse(eventosEncontrados.contains(evento1));
		assertTrue(eventosEncontrados.contains(evento2));
		assertTrue(eventosEncontrados.contains(evento3));

	}

	@Test
	public void testFindEventPorNombre() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria ejemplo");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento1 = new Evento("Evento1", fecha, c);

		fecha.set(Calendar.YEAR, 2017);
		Evento evento2 = new Evento("Evento2", fecha, c2);

		Evento evento3 = new Evento("nombre que no tiene que coger", fecha, c);

		eventoDao.save(evento1);
		eventoDao.save(evento2);
		eventoDao.save(evento3);

		EventoBlock eventos = adminService.findEvent(null, "evento", 0, 10);

		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 2);
		assertTrue(eventosEncontrados.contains(evento1));
		assertTrue(eventosEncontrados.contains(evento2));
		assertFalse(eventosEncontrados.contains(evento3));

	}

	@Test
	public void testFindEventPorNombreYCategoria() {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Categoria c2 = new Categoria("Categoria ejemplo");
		categoriaDao.save(c2);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento1 = new Evento("Evento1", fecha, c);

		fecha.set(Calendar.YEAR, 2017);
		Evento evento2 = new Evento("Evento2", fecha, c2);

		eventoDao.save(evento1);
		eventoDao.save(evento2);

		EventoBlock eventos = adminService.findEvent(c.getIdCategoria(),
				"evento", 0, 10);

		List<Evento> eventosEncontrados = eventos.getEventos();

		assertEquals(eventosEncontrados.size(), 1);
		assertTrue(eventosEncontrados.contains(evento1));
		assertFalse(eventosEncontrados.contains(evento2));

	}

	// --------------------------------

	@Test
	public void testMarkAsWinner() throws InstanceNotFoundException,
			ExpiredEventException, IllegalParameterException,
			NotMultipleOptionsException, AlreadySetOptionException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento = new Evento("Evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganará? ", true);
		tipoApuestaDao.save(tipo);

		TipoApuesta otroTipo = new TipoApuesta(evento, "¿quien morira?", true);
		tipoApuestaDao.save(otroTipo);

		Opcion op1 = new Opcion("los buenos", new Double(2), null, tipo);
		opcionDao.save(op1);

		Opcion op2 = new Opcion("los malos", new Double(2), null, tipo);
		opcionDao.save(op2);

		Opcion op3 = new Opcion("no se ve modificada", new Double(2), null,
				tipo);
		opcionDao.save(op3);

		Opcion op4 = new Opcion("no se ve afectada", new Double(2), null,
				otroTipo);
		opcionDao.save(op4);

		List<Long> opciones = new ArrayList<>();
		opciones.add(op1.getIdOpcion());
		opciones.add(op3.getIdOpcion());

		adminService.markAsWinner(opciones, tipo.getIdTipo());

		// comprobamos que actualice a true las que se les mande,
		// que las que no se le manden las ponga a false
		// y las que se ponga pero ya tengan un estado no se modifiquen
		assertTrue(op1.getEstado());
		assertFalse(op2.getEstado());
		assertTrue(op3.getEstado());
		assertEquals(op4.getEstado(),null);
		

	}

	@Test(expected = IllegalParameterException.class)
	public void testMarkAsWinnerNoPertenecenTipo()
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2013);

		Evento evento = new Evento("Evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganará? ", true);
		tipoApuestaDao.save(tipo);

		TipoApuesta otroTipo = new TipoApuesta(evento, "¿quien morira?", true);
		tipoApuestaDao.save(otroTipo);

		Opcion op1 = new Opcion("los buenos", new Double(2), null, tipo);
		opcionDao.save(op1);

		Opcion op2 = new Opcion("los malos", new Double(2), null, tipo);
		opcionDao.save(op2);

		Opcion op3 = new Opcion("no se ve modificada", new Double(2), null,
				tipo);
		opcionDao.save(op3);

		Opcion op4 = new Opcion("no se ve afectada", new Double(2), null,
				otroTipo);
		opcionDao.save(op4);

		List<Long> opciones = new ArrayList<>(2);
		opciones.add(op1.getIdOpcion());
		opciones.add(op3.getIdOpcion());

		adminService.markAsWinner(opciones, otroTipo.getIdTipo());

	}

	@Test(expected = ExpiredEventException.class)
	public void testMarkAsWinnerEventoFuturo()
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException {
		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2018);

		Evento evento = new Evento("Evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganará? ", true);
		tipoApuestaDao.save(tipo);

		TipoApuesta otroTipo = new TipoApuesta(evento, "¿quien morira?", true);
		tipoApuestaDao.save(otroTipo);

		Opcion op1 = new Opcion("los buenos", new Double(2), null, tipo);
		opcionDao.save(op1);

		Opcion op2 = new Opcion("los malos", new Double(2), null, tipo);
		opcionDao.save(op2);

		Opcion op3 = new Opcion("no se ve modificada", new Double(2), null,
				tipo);
		opcionDao.save(op3);

		Opcion op4 = new Opcion("no se ve afectada", new Double(2), null,
				otroTipo);
		opcionDao.save(op4);

		List<Long> opciones = new ArrayList<>();
		opciones.add(op1.getIdOpcion());
		opciones.add(op3.getIdOpcion());

		adminService.markAsWinner(opciones, tipo.getIdTipo());

		// deberia petar porque el evento aun no ha empezado
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testMarkAsWinnerDeUnTipoApuestaInexistente()
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException {

		adminService.markAsWinner(null, new Long(1245));

	}

	@Test(expected = NotMultipleOptionsException.class)
	public void testMarkAsWinnerSinMultiplesGanadoras()
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2018);

		Evento evento = new Evento("Evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganará? ", false);
		tipoApuestaDao.save(tipo);

		Opcion op1 = new Opcion("los buenos", new Double(2), null, tipo);
		opcionDao.save(op1);

		Opcion op2 = new Opcion("los malos", new Double(2), null, tipo);
		opcionDao.save(op2);

		Opcion op3 = new Opcion("no se ve modificada", new Double(2), null,
				tipo);
		opcionDao.save(op3);

		List<Long> opciones = new ArrayList<>();
		opciones.add(op1.getIdOpcion());
		opciones.add(op3.getIdOpcion());

		adminService.markAsWinner(opciones, tipo.getIdTipo());

	}

	@Test(expected = AlreadySetOptionException.class)
	public void testMarkAsWinnerYaSeteadas() throws InstanceNotFoundException,
			ExpiredEventException, IllegalParameterException,
			NotMultipleOptionsException, AlreadySetOptionException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2015);

		Evento evento = new Evento("Evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien ganará? ", true);
		tipoApuestaDao.save(tipo);

		Opcion op1 = new Opcion("los buenos", new Double(2), null, tipo);
		opcionDao.save(op1);

		Opcion op2 = new Opcion("los malos", new Double(2), null, tipo);
		opcionDao.save(op2);

		Opcion op3 = new Opcion("no se ve modificada", new Double(2), false,
				tipo);
		opcionDao.save(op3);

		List<Long> opciones = new ArrayList<>();
		opciones.add(op1.getIdOpcion());
		opciones.add(op3.getIdOpcion());

		adminService.markAsWinner(opciones, tipo.getIdTipo());
	}

	// --------------------------------

	@Test
	public void testAddBettingType() throws InstanceNotFoundException,
			InvalidEventDateException, DuplicateInstanceException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2018);

		Evento evento = new Evento("evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien marcará gol?", true);

		TipoApuesta tipoCreado = adminService.addBettingType(tipo,
				evento.getIdEvento(),new ArrayList<Opcion>());

		TipoApuesta tipoEncontrado = tipoApuestaDao
				.find(tipoCreado.getIdTipo());

		assertEquals(tipoEncontrado, tipoCreado);
	}

	@Test(expected = DuplicateInstanceException.class)
	public void testAddBettingTypeTipoDuplicado()
			throws InstanceNotFoundException, InvalidEventDateException,
			DuplicateInstanceException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2017);

		Evento evento = new Evento("evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien marcará gol?", true);
		tipoApuestaDao.save(tipo);

		TipoApuesta tipo2 = new TipoApuesta(evento, "¿quien marcará gol?", true);

		adminService.addBettingType(tipo2, evento.getIdEvento(),null);

	}

	@Test(expected = InvalidEventDateException.class)
	public void testAddBettingTypeEventoPasado()
			throws InstanceNotFoundException, InvalidEventDateException,
			DuplicateInstanceException {

		Categoria c = new Categoria("Categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();
		fecha.set(Calendar.YEAR, 2014);

		Evento evento = new Evento("evento de ejemplo", fecha, c);
		eventoDao.save(evento);

		TipoApuesta tipo = new TipoApuesta(evento, "¿quien marcara?", true);

		TipoApuesta tipoExplota = adminService.addBettingType(tipo,
				evento.getIdEvento(),null);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testAddBettingTypeEventoInventado()
			throws InstanceNotFoundException, InvalidEventDateException,
			DuplicateInstanceException {

		Categoria c = new Categoria("categoria de ejemplo");
		categoriaDao.save(c);

		Calendar fecha = Calendar.getInstance();

		Evento evento = new Evento("evento1", fecha, c);

		TipoApuesta tipo = new TipoApuesta(evento, "sd", true);

		TipoApuesta tipoExplota = adminService.addBettingType(tipo, new Long(
				2145),null);

	}

}

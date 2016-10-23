package es.udc.java.pa.pa009.pwin.test.model.adminservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.AdminServiceImpl;
import es.udc.pa.pa009.pwin.model.adminservice.AlreadySetOptionException;
import es.udc.pa.pa009.pwin.model.adminservice.IllegalParameterException;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.adminservice.NotMultipleOptionsException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class AdminServiceTest {

	@InjectMocks
	private AdminService adminServiceMockito = new AdminServiceImpl();

	@Mock
	private EventoDao eventoDaoMockito;

	@Mock
	private TipoApuestaDao tipoApuestaDaoMockito;

	@Mock
	private OpcionDao opcionDaoMockito;

	@Mock
	private CategoriaDao categoriaDaoMockito;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Test
	public void testPR_UN_AS_01()
			throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException {
		// initialize
		Categoria c = new Categoria("categoria prueba");
		c.setIdCategoria(new Long(20));

		Evento evento = new Evento("eventoPrueba", Calendar.getInstance(), c);
		evento.getFecha().add(Calendar.HOUR, 1);

		when(eventoDaoMockito.findDuplicateEvents(evento.getNombreEvento(), evento.getFecha(), c.getIdCategoria()))
		.thenReturn(false);

		when(categoriaDaoMockito.find(c.getIdCategoria())).thenReturn(c);

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
		// Inicialize
		Categoria categoria = new Categoria("categoriaMockito");
		List<Evento> mockedList = new ArrayList<Evento>();
		for (int i = 0; i < 11; i++) {
			mockedList.add(new Evento("eventoMockito", Calendar.getInstance(), categoria));
		}
		when(eventoDaoMockito.findEventNoDate(categoria.getIdCategoria(), "", 0, 11)).thenReturn(mockedList);
		// Test
		EventoBlock evento = adminServiceMockito.findEvent(categoria.getIdCategoria(), "", 0, 10);

		assertTrue(evento.isExistMoreEvents());
	}

	@Test
	public void testPR_UN_AS_04() throws InstanceNotFoundException, ExpiredEventException, IllegalParameterException,
	NotMultipleOptionsException, AlreadySetOptionException {
		// Inicialize
		Long id = (long) 1;
		Categoria categoria = new Categoria("categoriaMockito");
		Evento evento = new Evento("eventoMockito", Calendar.getInstance(), categoria);
		TipoApuesta tipoApuesta = new TipoApuesta(evento, "pregunta", true);
		List<Opcion> opciones = new ArrayList<Opcion>();
		for (int i = 0; i < 5; i++) {
			opciones.add(new Opcion("option1", 0.5, null, tipoApuesta));
		}
		List<Long> ganadoras = new ArrayList<Long>();
		ganadoras.add(opciones.get(0).getIdOpcion());
		ganadoras.add(opciones.get(1).getIdOpcion());
		when(tipoApuestaDaoMockito.find(tipoApuesta.getIdTipo())).thenReturn(tipoApuesta);
		when(opcionDaoMockito.find(opciones.get(0).getIdOpcion())).thenReturn(opciones.get(0));
		when(opcionDaoMockito.find(opciones.get(1).getIdOpcion())).thenReturn(opciones.get(1));

		// Test
		adminServiceMockito.markAsWinner(ganadoras, tipoApuesta.getIdTipo());
		assertTrue(opciones.get(0).getEstado());
		assertTrue(opciones.get(1).getEstado());
	}

	@Test
	public void testPR_UN_AS_05() throws InstanceNotFoundException, ExpiredEventException, IllegalParameterException,
	NotMultipleOptionsException, AlreadySetOptionException {
		// Inicialize
		thrown.expect(AlreadySetOptionException.class);
		Categoria categoria = new Categoria("categoriaMockito");
		Evento evento = new Evento("eventoMockito", Calendar.getInstance(), categoria);
		TipoApuesta tipoApuesta = new TipoApuesta(evento, "pregunta", true);
		List<Opcion> opciones = new ArrayList<Opcion>();
		for (int i = 0; i < 5; i++) {
			opciones.add(new Opcion("option1", 0.5, null, tipoApuesta));
		}
		List<Long> ganadoras = new ArrayList<Long>();
		opciones.get(0).setEstado(true);
		ganadoras.add(opciones.get(0).getIdOpcion());
		ganadoras.add(opciones.get(1).getIdOpcion());
		when(tipoApuestaDaoMockito.find(tipoApuesta.getIdTipo())).thenReturn(tipoApuesta);
		when(opcionDaoMockito.find(opciones.get(0).getIdOpcion())).thenReturn(opciones.get(0));
		when(opcionDaoMockito.find(opciones.get(1).getIdOpcion())).thenReturn(opciones.get(1));

		// Test
		adminServiceMockito.markAsWinner(ganadoras, tipoApuesta.getIdTipo());
	}

	@Test
	public void testPR_UN_AS_06()
			throws InstanceNotFoundException, InvalidEventDateException, DuplicateInstanceException {
		// Inicialize
		Categoria categoria = new Categoria("categoriaMockito");
		Calendar fecha = Calendar.getInstance();
		fecha.add(Calendar.YEAR, 4);
		Evento evento = new Evento("eventoMockito", fecha, categoria);
		TipoApuesta tipoApuesta = new TipoApuesta(evento, "pregunta", true);
		List<Opcion> opciones = new ArrayList<Opcion>();
		for (int i = 0; i < 5; i++) {
			opciones.add(new Opcion("option1", 0.5, null, tipoApuesta));
		}
		when(eventoDaoMockito.find(evento.getIdEvento())).thenReturn(evento);
		when(tipoApuestaDaoMockito.findDuplicateBetTypes(tipoApuesta.getPregunta(), evento.getIdEvento()))
		.thenReturn(false);
		// Test
		adminServiceMockito.addBettingType(tipoApuesta, evento.getIdEvento(), opciones);
		assertEquals(tipoApuesta.getEvento(), evento);
		for (int j = 0; j < tipoApuesta.getOpciones().size(); j++) {
			assertTrue(opciones.contains(tipoApuesta.getOpciones().get(j)));
		}
	}

}

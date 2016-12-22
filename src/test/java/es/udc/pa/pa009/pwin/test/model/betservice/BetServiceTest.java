package es.udc.pa.pa009.pwin.test.model.betservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.ApuestaDao;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.ApuestaBlock;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.BetServiceImpl;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.betservice.NegativeAmountException;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfileDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class BetServiceTest {

	@InjectMocks
	private BetService betServiceMockito = new BetServiceImpl();

	@Mock
	private EventoDao eventoDaoMockito;

	@Mock
	private OpcionDao opcionDaoMockito;

	@Mock
	private UserProfileDao userProfileDaoMockito;

	@Mock
	private ApuestaDao apuestaDaoMockito;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testPR_UN_BS_01() {
		// Inicialize
		Categoria categoria = new Categoria("categoriaMockito");
		List<Evento> mockedList = new ArrayList<Evento>();
		Calendar fecha = Calendar.getInstance();
		fecha.add(Calendar.YEAR, 4);
		for (int i = 0; i < 11; i++) {
			mockedList.add(new Evento("eventoMockito", fecha, categoria));
		}
		when(eventoDaoMockito.findEvent(categoria.getIdCategoria(), "", 0, 11)).thenReturn(mockedList);
		// Test
		EventoBlock evento = betServiceMockito.findEvent(categoria.getIdCategoria(), "", 0, 10);

		assertTrue(evento.isExistMoreEvents());

	}

	@Test
	public void testPR_UN_BS_02() throws InstanceNotFoundException, ExpiredEventException, NegativeAmountException {
		// Inicialize
		UserProfile user = new UserProfile("nanie", "nanie", "nanie", "nanie", "nanie@nanie.com");
		Categoria categoria = new Categoria("categoriaMockito");
		Calendar fecha = Calendar.getInstance();
		fecha.add(Calendar.YEAR, 4);
		Evento evento = new Evento("eventoMockito", fecha, categoria);
		TipoApuesta tipoApuesta = new TipoApuesta(evento, "pregunta", true);
		Opcion opcion = new Opcion("option1", 0.5, null, tipoApuesta);
		when(opcionDaoMockito.find(opcion.getIdOpcion())).thenReturn(opcion);
		when(userProfileDaoMockito.find(user.getUserProfileId())).thenReturn(user);

		// Test
		Apuesta apuesta = betServiceMockito.bet(opcion.getIdOpcion(), 32.3, user.getUserProfileId());
		assertEquals(apuesta.getUsuario(), user);
		assertEquals(apuesta.getOpcionElegida(), opcion);
	}

	@Test
	public void testPR_UN_BS_03() {
		// Inicialize
		UserProfile user = new UserProfile("nanie", "nanie", "nanie", "nanie", "nanie@nanie.com");
		Categoria categoria = new Categoria("categoriaMockito");
		Calendar fecha = Calendar.getInstance();
		fecha.add(Calendar.YEAR, 4);
		Evento evento = new Evento("eventoMockito", fecha, categoria);
		TipoApuesta tipoApuesta = new TipoApuesta(evento, "pregunta", true);
		Opcion opcion = new Opcion("option1", 0.5, null, tipoApuesta);
		List<Apuesta> apuestas = new ArrayList<Apuesta>();
		for (int i = 0; i < 11; i++) {
			apuestas.add(new Apuesta(Calendar.getInstance(), user, 32.3, opcion));
		}

		when(apuestaDaoMockito.findApuestasByIdUsuario(user.getUserProfileId(), 0, 11)).thenReturn(apuestas);
		// Test
		ApuestaBlock apuesta = betServiceMockito.checkBet(user.getUserProfileId(), 0, 10);
		assertTrue(apuesta.isExistMoreBets());
	}

	@Test
	public void testPR_UN_BS_04() throws ExpiredEventException, InstanceNotFoundException, NegativeAmountException {

		// initialize
		thrown.expect(NegativeAmountException.class);

		UserProfile user = new UserProfile("nanie", "nanie", "nanie", "nanie", "nanie");
		Evento event = new Evento("evento", Calendar.getInstance(), null);
		event.getFecha().add(Calendar.YEAR, 2);

		TipoApuesta betType = new TipoApuesta(event, "sdkvjs", true);
		Opcion option = new Opcion("opcion", 20.0, null, betType);

		when(opcionDaoMockito.find(option.getIdOpcion())).thenReturn(option);

		// test
		betServiceMockito.bet(option.getIdOpcion(), -43.0, user.getUserProfileId());

	}
}

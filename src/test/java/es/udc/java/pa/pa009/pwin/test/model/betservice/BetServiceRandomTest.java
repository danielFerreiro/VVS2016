package es.udc.java.pa.pa009.pwin.test.model.betservice;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.NegativeAmountException;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfileDao;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class BetServiceRandomTest {

	@Autowired
	private BetService betService;

	@Autowired
	private UserProfileDao userDao;

	@Autowired
	private CategoriaDao categoriaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private TipoApuestaDao tipoApuestaDao;

	@Autowired
	private OpcionDao opcionDao;

	@Test
	@Repeat(10)
	public void test_PR_RANDOM_02()
			throws InstanceNotFoundException, DuplicateInstanceException, InvalidEventDateException,
			ExpiredEventException, NegativeAmountException {

		UserProfile user = new UserProfile("user test", "test", "test", "random", "test@random");
		userDao.save(user);

		Categoria c = new Categoria("test categoria");
		categoriaDao.save(c);

		Evento evento = new Evento("evento test rd", Calendar.getInstance(), c);
		eventoDao.save(evento);
		evento.getFecha().add(Calendar.SECOND, 10);

		TipoApuesta t = new TipoApuesta(evento, "pregunta test random", true);
		tipoApuestaDao.save(t);

		Opcion opcion = new Opcion("test opcion", 10.0, null, t);
		opcionDao.save(opcion);

		Generator<Double> generador = PrimitiveGenerators.doubles(0, Double.MAX_VALUE);

		betService.bet(opcion.getIdOpcion(), generador.next(), user.getUserProfileId());

	}

	@Test(expected = NegativeAmountException.class)
	@Repeat(10)
	public void test_PR_RANDOM_03() throws InstanceNotFoundException, DuplicateInstanceException,
	InvalidEventDateException, ExpiredEventException, NegativeAmountException {

		UserProfile user = new UserProfile("user test", "test", "test", "random", "test@random");
		userDao.save(user);

		Categoria c = new Categoria("test categoria");
		categoriaDao.save(c);

		Evento evento = new Evento("evento test rd", Calendar.getInstance(), c);
		eventoDao.save(evento);
		evento.getFecha().add(Calendar.SECOND, 10);

		TipoApuesta t = new TipoApuesta(evento, "pregunta test random", true);
		tipoApuestaDao.save(t);

		Opcion opcion = new Opcion("test opcion", 10.0, null, t);
		opcionDao.save(opcion);

		Generator<Double> generador = PrimitiveGenerators.doubles(Double.MIN_VALUE, 0);

		betService.bet(opcion.getIdOpcion(), generador.next(), user.getUserProfileId());

	}

}

package es.udc.java.pa.pa009.pwin.test.model.adminservice;

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

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class AdminServiceRandomTest {

	@Autowired
	private AdminService adminService;

	@Autowired
	private CategoriaDao categoriaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private TipoApuestaDao tipoApuestaDao;


	@Test
	@Repeat(10)
	public void test_PR_RANDOM_01() throws InvalidEventDateException, InstanceNotFoundException {
		Categoria c = new Categoria("test random");
		categoriaDao.save(c);

		Evento evento = new Evento("evento test rd", Calendar.getInstance(), c);
		eventoDao.save(evento);
		evento.getFecha().add(Calendar.SECOND, 10);

		TipoApuesta t = new TipoApuesta(evento, "pregunta test random", true);
		tipoApuestaDao.save(t);

		Generator<Double> generador = PrimitiveGenerators.doubles(0, Double.MAX_VALUE);

		Opcion o = new Opcion("test rd", generador.next(), null, t);
		adminService.addOption(t.getIdTipo(), "test opcion", generador.next(), null);

	}

}

package es.udc.pa.pa009.pwin.model.betservice;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.ApuestaDao;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfileDao;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@Service("betService")
@Transactional
public class BetServiceImpl implements BetService {

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private OpcionDao opcionDao;

	@Autowired
	private ApuestaDao apuestaDao;

	@Autowired
	private CategoriaDao categoriaDao;

	@Autowired
	private UserProfileDao userProfileDao;

	@Override
	@Transactional(readOnly = true)
	public List<Categoria> findAllCategories() {

		return categoriaDao.findCategories();

	}

	@Override
	@Transactional(readOnly = true)
	public Opcion findOptionById(Long idOpcion)
			throws InstanceNotFoundException, ExpiredEventException {

		Opcion o = opcionDao.find(idOpcion);

		TipoApuesta tipo = o.getTipoApuesta();

		Evento evento = tipo.getEvento();

		if (evento.getFecha().before(Calendar.getInstance() ) ) {
			throw new ExpiredEventException(evento, "evento");
		}

		return o;
	}

	@Override
	@Transactional(readOnly = true)
	public EventoBlock findEvent(Long idCategoria, String keywords,
			int startIndex, int count) {

		List<Evento> listEventos = eventoDao.findEvent(idCategoria, keywords,
				startIndex, count + 1);

		Boolean existMore = listEventos.size() == (count + 1);

		if (existMore) {
			listEventos.remove(listEventos.size() - 1);
		}

		return new EventoBlock(listEventos, existMore);
	}

	@Override
	@Transactional
	public Apuesta bet(Long idOpcion, Double cantidadApostada, Long userId)
			throws ExpiredEventException, InstanceNotFoundException, NegativeAmountException {

		Opcion opcionElegida = opcionDao.find(idOpcion);

		TipoApuesta tipoApuesta = opcionElegida.getTipoApuesta();

		Evento eventoAsociado = tipoApuesta.getEvento();

		Calendar fechaActual = Calendar.getInstance();

		// comprobamos que el evento por el que se quiera apostar no se haya
		// cerrado ya
		if (fechaActual.after(eventoAsociado.getFecha())) {
			throw new ExpiredEventException(eventoAsociado, "Evento");
		}

		if (cantidadApostada.isNaN() || cantidadApostada <= 0) {
			throw new NegativeAmountException(cantidadApostada, "Double");
		}

		UserProfile usuario = userProfileDao.find(userId);

		Apuesta apuestaRealizada = new Apuesta(fechaActual, usuario,
				cantidadApostada, opcionElegida);
		apuestaDao.save(apuestaRealizada);

		return apuestaRealizada;

	}

	@Override
	@Transactional(readOnly = true)
	public ApuestaBlock checkBet(Long userId, int startIndex, int count) {

		List<Apuesta> apuestas = apuestaDao.findApuestasByIdUsuario(userId,
				startIndex, count + 1);

		Boolean existMore = apuestas.size() == (count + 1);

		if (existMore) {
			apuestas.remove(apuestas.size() - 1);
		}

		return new ApuestaBlock(apuestas, existMore);

	}

	@Override
	@Transactional(readOnly = true)
	public Evento findEventById(Long idEvento) throws InstanceNotFoundException {

		Evento e = eventoDao.find(idEvento);
		return e;
	}

	@Override
	public int getNumberOfEvents(String keywords, Long categoryId) {
		return eventoDao.getNumberOfEvents(categoryId, keywords);
	}

	@Override
	public int getNumberOfBets(Long userID){
		return apuestaDao.findNumberOfBets(userID);
	}

	@Override
	public List<Opcion> showWinners(Long idTipoApuesta) {
		return opcionDao.showWinners(idTipoApuesta);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Evento> findAllEvents(Long idCategoria, String keywords){
		return eventoDao.findAllEvents(idCategoria, keywords);
	}
}

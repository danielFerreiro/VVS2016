package es.udc.pa.pa009.pwin.model.adminservice;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service("adminService")
public class AdminServiceImpl implements AdminService {

	@Autowired
	private OpcionDao opcionDao;

	@Autowired
	private TipoApuestaDao tipoApuestaDao;

	@Autowired
	private EventoDao eventoDao;

	@Autowired
	private CategoriaDao categoriaDao;

	@Override
	@Transactional
	public Evento createEvent(Evento evento, Long idCategoria)
			throws InstanceNotFoundException, DuplicateInstanceException,
			InvalidEventDateException {

		// comprobamos que no se inserte un evento pasado
		if (evento.getFecha().before(Calendar.getInstance())) {
			throw new InvalidEventDateException(evento, "evento");
		}

		boolean hayDuplicados = eventoDao.findDuplicateEvents(
				evento.getNombreEvento(), evento.getFecha(), idCategoria);

		// comprobamos que no existan eventos con el mismo nombre y fecha
		if (hayDuplicados) {
			throw new DuplicateInstanceException(evento, "evento");
		}

		Categoria categoria = categoriaDao.find(idCategoria);

		evento.setCategoria(categoria);

		// guardamos el evento
		eventoDao.save(evento);
		return evento;

	}

	@Override
	@Transactional(readOnly = true)
	public EventoBlock findEvent(Long idCategoria, String keywords,
			int startIndex, int count) {

		// recuperamos los eventos sin importar la fecha
		List<Evento> eventos = eventoDao.findEventNoDate(idCategoria, keywords,
				startIndex, count + 1);

		Boolean existMore = eventos.size() == (count + 1);

		if (existMore) {
			eventos.remove(eventos.size() - 1);
		}

		return new EventoBlock(eventos, existMore);
	}

	@Override
	@Transactional
	public void markAsWinner(List<Long> opciones, Long idTipoApuesta)
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException {

		TipoApuesta tipoApuesta = tipoApuestaDao.find(idTipoApuesta);

		boolean aceptaMultiples = tipoApuesta.isMultiple();

		if ((opciones.size() > 1) && (!aceptaMultiples)) {
			throw new NotMultipleOptionsException(tipoApuesta, "tipoApuesta");
		}

		for (Long id : opciones) {

			// comprobamos que todas las opciones pertenezcan al tipo de apuesta
			Opcion oEncontrada = opcionDao.find(id);
			if (oEncontrada.getTipoApuesta().getIdTipo().compareTo(idTipoApuesta) != 0) {
				throw new IllegalParameterException(oEncontrada, "opcion");
			}
		}

		Evento evento = tipoApuesta.getEvento();

		// comprobamos que se marcan opciones sobre un evento futuro
		if (evento.getFecha().after(Calendar.getInstance())) {
			throw new ExpiredEventException(evento, "evento");
		}

		List<Opcion> opcionesDelTipo = tipoApuesta.getOpciones();

		for (Opcion o : opcionesDelTipo) {

			// actualizamos las opciones

			if (o.getEstado() != null) {
				throw new AlreadySetOptionException(o, "opcion");
			} else {
				if (opciones.contains(o.getIdOpcion())) {
					o.setEstado(true);
				} else {
					o.setEstado(false);
				}
			}
		}
	}

	@Override
	@Transactional
	public TipoApuesta addBettingType(TipoApuesta tipoApuesta, Long idEvento,
			List<Opcion> opciones) throws InstanceNotFoundException,
	InvalidEventDateException, DuplicateInstanceException {

		Evento evento = eventoDao.find(idEvento);

		// comprobamos que no se este creando un tipoApuesta sobre un evento ya
		// pasado
		if (evento.getFecha().before(Calendar.getInstance())) {
			throw new InvalidEventDateException(evento, "evento");
		}

		boolean hayDuplicados = tipoApuestaDao.findDuplicateBetTypes(
				tipoApuesta.getPregunta(), idEvento);

		// comprobamos que no exista ya ese tipo de apuesta
		if (hayDuplicados) {
			throw new DuplicateInstanceException(tipoApuesta.getPregunta(), "tipoApuesta");
		}

		tipoApuesta.setEvento(evento);

		tipoApuesta.setOpciones(opciones);

		tipoApuestaDao.save(tipoApuesta);

		for (Opcion o:opciones){
			opcionDao.save(o);
		}

		return tipoApuesta;

	}

	@Override
	public Categoria findCategoriaByID(Long idCategoria)
			throws InstanceNotFoundException {

		return categoriaDao.find(idCategoria);

	}

	@Override
	public int getNumberOfEvents(String keywords, Long categoryId) {
		return eventoDao.getNumberOfEvents2(categoryId, keywords);
	}

	@Override
	@Transactional
	public Opcion addOption(Long idTipoApuesta, String nombreOpcion,
			Double cuota, Boolean estado) throws InstanceNotFoundException,
	InvalidEventDateException {
		TipoApuesta tipoApuesta = tipoApuestaDao.find(idTipoApuesta);
		if (tipoApuesta.getEvento().getFecha().before(Calendar.getInstance())) {
			throw new InvalidEventDateException(tipoApuesta.getEvento(),
					"evento");
		}
		Opcion opcion = new Opcion(nombreOpcion, cuota, null, tipoApuesta);
		opcionDao.save(opcion);

		tipoApuestaDao.save(tipoApuesta);

		return opcion;
	}

	@Override
	public TipoApuesta findTipoApuesta(Long idTipoApuesta)
			throws InstanceNotFoundException {
		return tipoApuestaDao.find(idTipoApuesta);
	}

	@Override
	@Transactional(readOnly = true)
	public Opcion findOptionById(Long idOpcion)
			throws InstanceNotFoundException {

		Opcion o = opcionDao.find(idOpcion);

		TipoApuesta tipo = o.getTipoApuesta();

		Evento evento = tipo.getEvento();

		return o;
	}


	@Override
	@Transactional(readOnly = true)
	public List<Evento> findAllEvents(Long idCategoria, String keywords) {
		return eventoDao.findAllEventsNoDate(idCategoria, keywords);
	}



}

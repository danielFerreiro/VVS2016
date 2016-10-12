package es.udc.pa.pa009.pwin.model.adminservice;

import java.util.List;

import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.EventoBlock;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

public interface AdminService {

	public Evento createEvent(Evento evento, Long idCategoria)
			throws InstanceNotFoundException, DuplicateInstanceException,
			InvalidEventDateException;

	public EventoBlock findEvent(Long idCategoria, String keywords,
			int startIndex, int count);

	public Categoria findCategoriaByID(Long idCategoria)
			throws InstanceNotFoundException;

	public void markAsWinner(List<Long> opciones, Long idTipoApuesta)
			throws InstanceNotFoundException, ExpiredEventException,
			IllegalParameterException, NotMultipleOptionsException,
			AlreadySetOptionException;

	public TipoApuesta addBettingType(TipoApuesta tipoApuesta, Long idEvento,
			List<Opcion> opciones) throws InstanceNotFoundException,
			InvalidEventDateException, DuplicateInstanceException;

	public int getNumberOfEvents(String keywords, Long categoryId);

	public TipoApuesta findTipoApuesta(Long idTipoApuesta)
			throws InstanceNotFoundException;

	public Opcion addOption(Long idTipoApuesta, String nombreOpcion,
			Double cuota, Boolean estado) throws InstanceNotFoundException,
			InvalidEventDateException;

	public Opcion findOptionById(Long idOpcion)
			throws InstanceNotFoundException;
	
	public List<Evento> findAllEvents(Long idCategoria, String keywords);
}

package es.udc.pa.pa009.pwin.model.betservice;

import java.util.List;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

public interface BetService {

	public List<Categoria> findAllCategories();

	public Opcion findOptionById(Long idOpcion)
			throws InstanceNotFoundException, ExpiredEventException;

	public EventoBlock findEvent(Long idCategoria, String keywords,
			int startIndex, int count);

	public Apuesta bet(Long idOpcion, Double cantidadApostada, Long userId)
			throws ExpiredEventException, InstanceNotFoundException;

	public ApuestaBlock checkBet(Long userId, int startIndex, int count);

	public Evento findEventById(Long idEvento) throws InstanceNotFoundException;
	
	public int getNumberOfEvents(String keywords, Long categoryId);
	
	public int getNumberOfBets(Long userID);
	
	public List<Opcion> showWinners (Long idTipoApuesta);

	public List<Evento> findAllEvents(Long idCategoria, String keywords);
}

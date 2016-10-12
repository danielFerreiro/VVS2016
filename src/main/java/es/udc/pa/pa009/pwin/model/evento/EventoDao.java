package es.udc.pa.pa009.pwin.model.evento;

import java.util.Calendar;
import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface EventoDao extends GenericDao<Evento, Long> {

	public List<Evento> findEvent(Long idCategoria, String keywords,
			int startIndex, int count);

	public List<Evento> findEventNoDate(Long idCategoria, String keywords,
			int startIndex, int count);

	public boolean findDuplicateEvents(String eventName, Calendar date,
			Long idCategoria);
	
	public int getNumberOfEvents(Long categoryId, String keywords);
	
	public int getNumberOfEvents2(Long categoryId, String keywords);

	public List<Evento> findAllEvents(Long idCategoria, String keywords);
	
	public List<Evento> findAllEventsNoDate(Long idCategoria, String keywords);
}

/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.betPages;


import java.text.DateFormat;
import java.util.Locale;

import javax.inject.Inject;

import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.web.util.EventoGridDataSource;


public class EventsFound {

	private final static int EVENTS_PER_PAGE = 10;
	private Long categoryId;
	private String keywords;
	private int startIndex = 0;
	private Evento evento;
	private EventoGridDataSource eventoGridDataSource;
	@Inject
	private BetService betService;
	
	@Inject
	private Locale locale;
	

	public Object[] onPassivate() {
		return new Object[] { categoryId, keywords, startIndex };
	}

	public void onActivate(Long categoryId, String keywords, int startIndex) {
		this.categoryId = categoryId;
		this.keywords = keywords;
		this.startIndex = startIndex;
		this.eventoGridDataSource = new EventoGridDataSource(categoryId, keywords,
				betService);
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public EventoGridDataSource getEventoGridDataSource() {
		return eventoGridDataSource;
	}

	public void setEventoGridDataSource(EventoGridDataSource eventoGrid) {
		this.eventoGridDataSource = eventoGrid;
	}

	public int getRowsPerPage() {
		return EVENTS_PER_PAGE;
	}
	
	
	
	public String getFechaFormateada() {
		
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM, locale);
		
		return formatter.format(evento.getFecha().getTime());
		
	}
	
	

}
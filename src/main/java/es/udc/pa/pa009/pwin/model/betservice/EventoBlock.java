package es.udc.pa.pa009.pwin.model.betservice;

import java.util.List;

import es.udc.pa.pa009.pwin.model.evento.Evento;

public class EventoBlock {

	private List<Evento> eventos;
	private boolean existMoreEvents;
	
	public EventoBlock(List<Evento> eventos, boolean existMoreEvents) {
		this.eventos = eventos;
		this.existMoreEvents = existMoreEvents;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}

	public boolean isExistMoreEvents() {
		return existMoreEvents;
	}

	public void setExistMoreEvents(boolean existMoreEvents) {
		this.existMoreEvents = existMoreEvents;
	}
	
	
	
	
}

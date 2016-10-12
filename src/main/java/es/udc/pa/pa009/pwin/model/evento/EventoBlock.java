package es.udc.pa.pa009.pwin.model.evento;

import java.util.List;


public class EventoBlock {
	
	
	private List<Evento> eventos;
	private boolean existMoreEvents;
	
	
	public List<Evento> getEventos() {
		return eventos;
	}
	
	public EventoBlock(){
		
	}
	public EventoBlock(List<Evento> eventos, boolean existMoreEvents){
		this.eventos=eventos;
		this.existMoreEvents=existMoreEvents;
		
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

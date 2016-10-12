/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.betPages;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

/**
 * @author user
 *
 */
public class EventDetails {

	private Long idEvento;
	private Evento evento;
	
	private TipoApuesta tipoApuesta;
	private Opcion opcion;

	@Inject
	private BetService betService;
	@Inject
	private Locale locale;

	
	public void setOpcion(Opcion o) {
		opcion=o;
	}
	
	public Opcion getOpcion() {
		return opcion;
	}
	
	public void setTipoApuesta(TipoApuesta t) {
		tipoApuesta=t;
	}
	
	public TipoApuesta getTipoApuesta() {
		return tipoApuesta;
	}
	
	public Evento getEvento() {
		return evento;
	}
	
	public void setEvento(Evento e) {
		evento=e;
		
	}
	
	
	public DateFormat getDateFormat() {
		return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale);
	}
	
	
	void onActivate(Long idEvento) {

		this.idEvento = idEvento;

		try {
			evento = betService.findEventById(idEvento);
		} catch (InstanceNotFoundException e) {
			
		}

	}

	Long onPassivate() {
		return idEvento;
	}

}
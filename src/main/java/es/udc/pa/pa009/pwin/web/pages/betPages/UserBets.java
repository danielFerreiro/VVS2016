/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.betPages;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.ApuestaGridDataSource;
import es.udc.pa.pa009.pwin.web.util.UserSession;

/**
 * @author David
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class UserBets {

	private final static int BETS_PER_PAGE = 10;
	@SessionState(create = false)
	private UserSession usuario;
	private Apuesta apuesta;
	private double ganancia;
	private int startIndex;
	private ApuestaGridDataSource apuestaGridDataSource;
	@Inject
	private BetService betService;
	@Inject
	private Locale locale;
	@Inject
	private Messages messages;
	
	
	private DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.MEDIUM, locale);

	
	public void onActivate(int startIndex) {
		this.startIndex=startIndex;
		this.apuestaGridDataSource = new ApuestaGridDataSource(
				usuario.getUserProfileId(), startIndex, betService);
	}
	
	public int onPassivate(){
		return startIndex;
	}

	public Apuesta getApuesta() {
		return apuesta;
	}

	public void setApuesta(Apuesta apuesta) {
		this.apuesta = apuesta;
	}

	public ApuestaGridDataSource getApuestaGridDataSource() {
		return apuestaGridDataSource;
	}

	public void setApuestaGridDataSource(
			ApuestaGridDataSource apuestaGridDataSource) {
		this.apuestaGridDataSource = apuestaGridDataSource;
	}

	public int getRowsPerPage() {
		return BETS_PER_PAGE;
	}
	
	public Double getGanancia(){
		
		Boolean estado = apuesta.getOpcionElegida().getEstado();
		
		if (estado != null && estado) {
			return (apuesta.getApostado()) * (apuesta.getOpcionElegida().getCuota());
		}
		return null;
	}
	
	public String getFechaDeApuesta() {
		return formatter.format(apuesta.getFecha().getTime());
	}
	
	public String getFechaDeEvento() {
		return formatter.format(
				apuesta.getOpcionElegida().getTipoApuesta().getEvento().getFecha().getTime()
				);
	}
	
	public String getEstadoApuesta() {
		Boolean estado = apuesta.getOpcionElegida().getEstado();
		
		if (estado==null) {
			return messages.get("pendiente-label");
		}
		else if (estado) {
			return messages.get("ganadora-label");
		}
		else {
			return messages.get("perdedora-label");
		}
		
	}
	
	
	public String getOpcionesGanadoras() {
		
		StringBuilder builder = new StringBuilder();
		
		List<Opcion> opciones = betService.showWinners(apuesta.getOpcionElegida().
				getTipoApuesta().getIdTipo());
		
		for (Opcion o : opciones) {
			builder.append(o.getNombreOpcion()+" - ");
		}
		if (!opciones.isEmpty())
			builder.deleteCharAt( builder.length() - 2  );
		
		return builder.toString();
	}
	
}
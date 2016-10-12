/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.betPages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.web.pages.user.Login;
import es.udc.pa.pa009.pwin.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

/**
 * @author user
 *
 */
public class OptionDetails {

	private Opcion opcion;
	private Long idOp;
	
	@SessionState(create=false)
    private UserSession userSession;
	
	@SessionState(create=false)
	private Long idOpcion;
	
	
	@Inject
	private BetService betService;
	
	@Inject
	private Messages messages;
	
	@InjectPage
	private Login loginUser;
	
	@InjectPage
	private Bet bet;
	
	private String estadoConvertido;
	//devolver pagina bet
	
	void onActivate(Long opcionId) {
		idOp = opcionId;
		
		try {
			opcion = betService.findOptionById(opcionId);
		} catch (InstanceNotFoundException e) {
			
		} catch (ExpiredEventException e) {
		
		}
		
	}
	
	Long onPassivate() {
		return idOp;
	}
	
	
	public Opcion getOpcion() {
		return opcion;
	}
	
	public void setOpcion(Opcion o){
		opcion = o;		
	}
	
	public void setEstadoConvertido() {
		
		
	}
	
	public String getEstadoConvertido() {
		if (opcion.getEstado()==null) {
			return messages.get("pendiente-label");
		}
		else if (opcion.getEstado()==true) {
			return messages.get("ganadora-label");
		}
		else {
			return messages.get("perdedora-label");	
		}
	}

	
	Object onActionFromBetForOption(Long idOption){
		idOpcion=idOption;		
		bet.setIdOpcion(idOption);
		return bet;
	}	
	
	
}
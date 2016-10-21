/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.betPages;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.apuesta.Apuesta;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.betservice.NegativeAmountException;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pa.pa009.pwin.web.pages.adminPages.SuccessfulCreation;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

/**
 * @author user
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class Bet {

	@Component
	private Form betForm;

	@SessionState(create=false)
	private UserSession userSession;

	@Property
	private Double cantidad;

	@Inject
	private BetService betService;

	@Inject
	private Messages messages;

	@Inject
	private Locale locale;

	private Long idOpcion;
	private Apuesta apuestaRealizada;

	@Property
	private Evento evento;
	@Property
	private TipoApuesta tipoApuesta;
	@Property
	private Opcion opcion;



	void onActivate(Long idOpcion){


		this.idOpcion=idOpcion;

		try {
			opcion=betService.findOptionById(idOpcion);
		} catch (InstanceNotFoundException e) {
			betForm.recordError( messages.get("error-optionNotFound") );

		} catch (ExpiredEventException e) {
			betForm.recordError( messages.get("error-expiredEvent") );				

		}

		tipoApuesta=opcion.getTipoApuesta();
		evento=tipoApuesta.getEvento();
	}

	Long onPassivate() {
		return idOpcion;
	}




	public void setIdOpcion(Long id) {
		idOpcion=id;
	}

	public Long getIdOpcion() {
		return idOpcion;
	}

	public NumberFormat getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}


	void onValidateFromBetForm(){
		Long userId = userSession.getUserProfileId();

		try {
			apuestaRealizada = betService.bet(idOpcion, cantidad, userId);
		} catch (ExpiredEventException e) {
			betForm.recordError( messages.get("error-expiredEvent")  );

		} catch (InstanceNotFoundException e) {
			betForm.recordError( messages.get("error-optionNotFound"));

		} catch (NegativeAmountException e) {
			betForm.recordError(messages.get("error-optionNotDound"));
		}

	}

	Object onSuccess() {

		return SuccessfulCreation.class;

	}





}
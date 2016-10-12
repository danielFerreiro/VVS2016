/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.AlreadySetOptionException;
import es.udc.pa.pa009.pwin.model.adminservice.IllegalParameterException;
import es.udc.pa.pa009.pwin.model.adminservice.NotMultipleOptionsException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.model.evento.ExpiredEventException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

import java.util.List;

/**
 * @author User
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.ADMIN_USERS)
public class EventDet {
	
	private Long idEvento;
	private Evento evento;
	
	private TipoApuesta tipoApuesta;
	private Opcion opcion;
	
	@SessionState(create = false)
	private List<Long> listaId;


	@Inject
	private BetService betService;
	@Inject 
	private AdminService adminService;
	@Inject
	private Locale locale;
	@Inject 
	private Messages messages;
	
	@Component
	private Form eventAdminDetails;
	
	
	
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
		return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
	}
	
	
	void onActivate(Long idEvento) {
		if (listaId==null)
			listaId = new ArrayList<>();
		
		this.idEvento = idEvento;

		try {
			evento = betService.findEventById(idEvento);
		} catch (InstanceNotFoundException e) {
			eventAdminDetails.recordError(messages.get("error-eventInexistent") );
		}

	}

	Long onPassivate() {
		
		return idEvento ;
	}

	public boolean noEmpezado(){
		return evento.getFecha().after(Calendar.getInstance());
	}

	public boolean empezado(){
		return evento.getFecha().before(Calendar.getInstance());
	}
	
	public boolean esNulo(){
		
		return (opcion.getEstado()==null);
	}
	
	void onActionFromMarcar(Long id){
		listaId.add(id);
		
		
	}
	
	void onActionFromQuitar(Long id){
		listaId.remove(id);
		
	}
	
	public boolean estaInsertado(){
			
			return !listaId.contains(opcion.getIdOpcion());
	}
   
	
	void onValidateFromEventAdminDetails(){
		long idOp;
		long idTap = 0;
		Opcion o;
		if (!listaId.isEmpty()){
			idOp = listaId.get(0);
			try {
				o = adminService.findOptionById(idOp);
				idTap= o.getTipoApuesta().getIdTipo();
			} catch (InstanceNotFoundException e) {
				eventAdminDetails.recordError(messages.get("error-optionNotFound"));
				listaId = null;
			} 
			
		}
		
		try {
			adminService.markAsWinner(listaId, idTap);
			
		} catch (InstanceNotFoundException e) {
			eventAdminDetails.recordError(messages.get("error-betTypeNotFound"));
			
		} catch (ExpiredEventException e) {
			eventAdminDetails.recordError(messages.get("error-notStartedEvent"));
			
		} catch (IllegalParameterException e) {
			eventAdminDetails.recordError(messages.get("error-notThisBetType"));
			
		} catch (NotMultipleOptionsException e) {
			eventAdminDetails.recordError(messages.get("error-notMultiple"));
			
		} catch (AlreadySetOptionException e) {
			
			
		}
		listaId = null;

	}
	
	Object onSuccess(){

				return this;
		
	}
	
	
	
}
/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.SubmitMode;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

/**
 * @author user
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.ADMIN_USERS)
public class AddBetType {

	private Long idEvento;

	private Long idTipoApuesta;

	private SubmitMode submitMode;
	
	@Property
	private String pregunta;

	@Property
	private boolean multiples;
	
	
	
	@Inject
	private AdminService adminService;

	@Inject
	private BetService betService;

	@Inject
	private Messages messages;
	
	@Inject
	private Request request;
	
	@Inject
	private AjaxResponseRenderer render;
	

	@Component
	private Form addOptionsForm;

	@Component
	private Form addBetTypeForm;
	
	
	@Property
	private String nombreOpcion;

	@Property
	private Double cuota;
	
	@Property
	private Opcion opcion;

	@InjectPage
	private OptionCreated optionCreated;
	
	@InjectPage
	private AddOptions addOptions;

	@InjectComponent
	private Zone formZone;

	@InjectComponent
	private Zone opcionesAnnadidas;
	
	@InjectComponent
	private Zone betTypeZone;
	
	
	@SessionState(create = false)
	@Property
	private List<Opcion> opciones;

	@SessionState(create = false)
	private TipoApuesta tipoApuesta;

	
	
	
	
	
	public Long  onPassivate() {
		return idEvento ;
	}

	public void onActivate(Long idEvento) {
		if (opciones==null)
			opciones = new ArrayList<Opcion>();
			
			this.idEvento = idEvento;

	
		if (tipoApuesta != null) {
			pregunta = tipoApuesta.getPregunta();
			multiples = tipoApuesta.isMultiple();
		}
			
	}
	
	
	public boolean getExisteTipoApuesta() {
		return tipoApuesta == null ;
		
	}
	

	public Long getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
		
	}
	
	public SubmitMode getSubmitMode() {
		return SubmitMode.UNCONDITIONAL;
	}
	
	
	
	public Object onValidateFromAddBetTypeForm(){
		
		if (!addBetTypeForm.isValid()) {
			return this;
		}
		
		try {
			Evento evento = betService.findEventById(idEvento);
			tipoApuesta = new TipoApuesta(evento,pregunta,multiples);
			
			
		} catch (InstanceNotFoundException e) {
			addBetTypeForm.recordError(messages.get("eventoNoEncontrado"));
			render.addRender("betTypeZone", betTypeZone.getBody());
		}
		
		return null;
		
	}
	
	
	public Object onSubmitFromAddBetTypeForm() {
		
		
		if (request.isXHR()){
			return formZone.getBody();
			
		} else {
			return this;
		}
	}

	@OnEvent(component="finalizar" , value="selected")
	public void onFinalizar() {
		
		Opcion op = new Opcion(nombreOpcion, cuota, null, tipoApuesta);
		opciones.add(op);
		
		if (request.isXHR()) {
			render.addRender("formZone",formZone.getBody()).
				addRender("opcionesAnnadidas", opcionesAnnadidas.getBody() );
		}
		
	}
	
	
	
	public void onActionFromEliminarOpcion(String nombre) {
		
		if (opciones!=null) {
			
			List <Opcion> aux = new ArrayList<Opcion>(opciones);
			
			for (Opcion o : opciones) {
				if (o.getNombreOpcion().equals(nombre) )
						aux.remove(o);
			}
			
			opciones = aux;
			
		}
		
		if (request.isXHR()) {
			render.addRender("opcionesAnnadidas", opcionesAnnadidas.getBody() )
				.addRender("formZone", formZone.getBody());
		}
		
	}
	
	
	@OnEvent(component="finalButton", value="selected")
	public Object onFinalButton() {
			
			boolean haPasado = false;
		
			try {
				TipoApuesta ta = adminService.addBettingType(tipoApuesta, idEvento, opciones);
				
			} catch (InstanceNotFoundException e) {
				addBetTypeForm.recordError(messages.get("eventoNoEncontrado") );
				haPasado = true;
				
			} catch (InvalidEventDateException e) {
				addBetTypeForm.recordError(messages.get("fechaNoValida"));
				haPasado = true;
				
			} catch (DuplicateInstanceException e) {
				
				addBetTypeForm.recordError(messages.get("tipoApuestaDuplicado"));
				haPasado = true;
				
			}
			
			
			
			tipoApuesta = null;
			opciones = null;
			if (haPasado) {

				render.addRender("betTypeZone",betTypeZone.getBody()).addRender("formZone", formZone.getBody() );
				return null;
			}
			else
				return optionCreated;
		
	}

	
	public Long getIdTipoApuesta() {
		return idTipoApuesta;
	}

	public void setIdTipoApuesta(Long idTipoApuesta) {
		this.idTipoApuesta = idTipoApuesta;
	}

}
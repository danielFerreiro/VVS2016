/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

/**
 * @author David
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.ADMIN_USERS)
public class AddOptions {

	@Component
	private Form addOptionsForm;

	@Inject
	private AdminService adminService;

	private Long idTipoApuesta;

	@Property
	private String nombreOpcion;

	@Property
	private Double cuota;

	@InjectPage
	private OptionCreated optionCreated;

	@Inject
	private Messages messages;

	@InjectPage
	private AddOptions addOptions;

	public Object[] onPassivate() {
		return new Object[] { idTipoApuesta };
	}

	public void onActivate(Long idTipoApuesta) {
		this.idTipoApuesta = idTipoApuesta;
	}

	void onValidateFromAddOptionsForm() {

		try {
			adminService.addOption(idTipoApuesta, nombreOpcion, cuota, null);
		} catch (InstanceNotFoundException e) {
			addOptionsForm.recordError(messages.get("error-betTypeNotFound"));

		} catch (InvalidEventDateException e) {
			addOptionsForm.recordError(messages.get("error-eventStarted"));

		}

		addOptions.setIdTipoApuesta(idTipoApuesta);

	}

	@OnEvent(component = "finalizar", value = "selected")
	public Object onFinalizar() {

		return optionCreated;
	}

	@OnEvent(component = "masOpciones", value = "selected")
	public Object onMasOpciones() {

		return addOptions;
	}

	public Long getIdTipoApuesta() {
		return idTipoApuesta;
	}

	public void setIdTipoApuesta(Long idTipoApuesta) {
		this.idTipoApuesta = idTipoApuesta;
	}

}
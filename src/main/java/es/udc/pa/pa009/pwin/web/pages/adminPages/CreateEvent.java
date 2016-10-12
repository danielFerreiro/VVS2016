/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.adminservice.InvalidEventDateException;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
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
public class CreateEvent {

	@Component
	private Form createEventForm;

	@Component(id = "category")
	private Select categoriaSelect;

	@Inject
	private AdminService adminService;

	@Inject
	private BetService betService;

	@Inject
	private Messages messages;

	@InjectPage
	private EventCreated eventCreated;

	@Property
	private String nombreEvento;

	@Property
	private Date dateEvento;

	@Property
	private String categoryComboBox;
	@Property
	private List<Categoria> listaCategorias;

	@Property
	private String categoriaSeleccionada;

	@Inject
	private Locale locale;

	private String horaEvento;
	private Calendar fechaEvento;
	private Categoria c;

	public DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}

	public void setupRender() {
		categoryComboBox = "";
		listaCategorias = betService.findAllCategories();
		int lastPos = listaCategorias.size();
		int currentPos = 1;
		for (Categoria c : listaCategorias) {
			if (currentPos == lastPos) {
				categoryComboBox += c.getIdCategoria() + "="
						+ c.getNombreCategoria();
			} else {
				categoryComboBox += c.getIdCategoria() + "="
						+ c.getNombreCategoria() + ", ";
			}
		}
	}

	public void setHoraEvento(String h) {
		horaEvento = h;
	}

	public String getHoraEvento() {
		return horaEvento;
	}

	void onValidateFromCreateEventForm() {

		if (!createEventForm.isValid()) {
			return;
		}

		String[] hours = horaEvento.split(":");

		fechaEvento = Calendar.getInstance();

		fechaEvento.setTime(dateEvento);
		fechaEvento.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours[0]));
		fechaEvento.set(Calendar.MINUTE, Integer.parseInt(hours[1]));
		fechaEvento.set(Calendar.SECOND, Integer.parseInt(hours[2]));

		Long idCategoria = Long.parseLong(categoriaSeleccionada);

		try {
			c = adminService.findCategoriaByID(idCategoria);
		} catch (InstanceNotFoundException e1) {
			createEventForm.recordError(categoriaSelect, messages.format(
					"error-inexistentCategory", categoriaSeleccionada));
		}

		Evento evento = new Evento(nombreEvento, fechaEvento, c);

		try {
			adminService.createEvent(evento, c.getIdCategoria());
		} catch (InstanceNotFoundException e) {
			createEventForm.recordError(categoriaSelect, messages.format(
					"error-inexistentCategory", categoriaSeleccionada));
		} catch (DuplicateInstanceException e) {
			createEventForm.recordError(messages.format(
					"error-eventAlreadyExists", evento.getNombreEvento()));
		} catch (InvalidEventDateException e) {

			DateFormat dateFormatter = DateFormat.getDateTimeInstance(
					DateFormat.SHORT, DateFormat.SHORT, locale);

			createEventForm.recordError(messages.format(
					"error-futureDateEvent",
					dateFormatter.format(fechaEvento.getTime())));
		}

		eventCreated.setIdEvento(evento.getIdEvento());

		try {
			c = adminService.findCategoriaByID(idCategoria);
		} catch (InstanceNotFoundException e) {
			createEventForm.recordError(categoriaSelect, messages.format(
					"error-inexistentCategory", categoriaSeleccionada));
		}

	}

	Object onSuccess() {

		return eventCreated;
	}

}
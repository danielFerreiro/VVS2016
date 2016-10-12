package es.udc.pa.pa009.pwin.web.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.web.pages.Index;
import es.udc.pa.pa009.pwin.web.pages.adminPages.EventDet;
import es.udc.pa.pa009.pwin.web.pages.adminPages.FindEvents;
import es.udc.pa.pa009.pwin.web.pages.betPages.EventsFound;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.CookiesManager;
import es.udc.pa.pa009.pwin.web.util.UserSession;

@Import(library = { "tapestry5/bootstrap/js/collapse.js",
		"tapestry5/bootstrap/js/dropdown.js" }, stylesheet = "tapestry5/bootstrap/css/bootstrap-theme.css")
public class Layout {

	@Property
	@Parameter(required = true, defaultPrefix = "message")
	private String title;

	@Parameter(defaultPrefix = "literal")
	private Boolean showTitleInBody;

	@Property
	@SessionState(create = false)
	private UserSession userSession;

	@Inject
	private Cookies cookies;

	@Property
	private List<Categoria> listaCategorias;

	@Property
	private String categoriaSeleccionada;

	@Persist
	@Property
	private String categoryComboBox;

	@Property
	private String keywords;

	@Property
	private Long categoryId;

	private int startIndex = 0;

	@Component
	private Form findEventForm;
	

	@Inject
	private BetService betService;
	
	@Inject
	private AdminService adminService;

	@InjectPage
	private EventsFound eventsFound;
	
	@InjectPage
	private FindEvents eventsAdmin;

	
	public boolean getShowTitleInBody() {

		if (showTitleInBody == null) {
			return true;
		} else {
			return showTitleInBody;
		}

	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
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
	
	public Object onSuccess(){
		if (categoriaSeleccionada != null){
			categoryId= Long.parseLong(categoriaSeleccionada);
		} else{
			categoryId=null;
		}
		
		
		if ( userSession!=null && userSession.isAdmin() ) {
			eventsAdmin.setCategoryId(categoryId);
			eventsAdmin.setKeywords(keywords);
			return eventsAdmin;
		}
		else {
			eventsFound.setCategoryId(categoryId);
			eventsFound.setKeywords(keywords);
			return eventsFound;
		}
		
		
	}


	List<String> onProvideCompletionsFromKeywordsField(String entrada) {
		
		List<String> resultado = new ArrayList<String>();
		List<Evento> eventos;
		
		if (userSession != null && userSession.isAdmin() )
			eventos = adminService.findAllEvents(categoryId, entrada);
		else
			eventos = betService.findAllEvents(categoryId, entrada);
		
		
		for (Evento e : eventos) {
			resultado.add( e.getNombreEvento() );
		}		
		
		return resultado;
		
	}
	
	
	@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
	Object onActionFromLogout() {
		userSession = null;
		CookiesManager.removeCookies(cookies);
		return Index.class;
	}

}

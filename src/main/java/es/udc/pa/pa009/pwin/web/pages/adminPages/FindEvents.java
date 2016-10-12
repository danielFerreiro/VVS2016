/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.betservice.BetService;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.AdminEventoGridDataSource;
import es.udc.pa.pa009.pwin.web.util.EventoGridDataSource;

/**
 * @author User
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.ADMIN_USERS)
public class FindEvents {

	private final static int EVENTS_PER_PAGE = 10;
	private Long categoryId;
	private String keywords;
	private int startIndex = 0;
	private Evento evento;
	
	private AdminEventoGridDataSource adminEventoGridDataSource;
	
	@Inject
	private AdminService adminService;

	public Object[] onPassivate() {
		return new Object[] { categoryId, keywords, startIndex };
	}

	public void onActivate(Long categoryId, String keywords, int startIndex) {
		this.categoryId = categoryId;
		this.keywords = keywords;
		this.startIndex = startIndex;
		this.adminEventoGridDataSource = new AdminEventoGridDataSource(categoryId, keywords,
				adminService);
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public AdminEventoGridDataSource getAdminEventoGridDataSource() {
		return adminEventoGridDataSource;
	}

	public void setAdminEventoGridDataSource(AdminEventoGridDataSource eventoGrid) {
		this.adminEventoGridDataSource = eventoGrid;
	}

	public int getRowsPerPage() {
		return EVENTS_PER_PAGE;
	}
	
	
	

	
}
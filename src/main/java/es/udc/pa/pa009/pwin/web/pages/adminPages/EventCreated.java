/**
 * 
 */
package es.udc.pa.pa009.pwin.web.pages.adminPages;

import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;

/**
 * @author user
 *
 */
@AuthenticationPolicy(AuthenticationPolicyType.ADMIN_USERS)
public class EventCreated {

	private Long idEvento;

	public Long getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
	}
	
	Long onPassivate() {
		return idEvento;
	}
	
	void onActivate(Long id) {
		this.idEvento = id;
	}
	
}
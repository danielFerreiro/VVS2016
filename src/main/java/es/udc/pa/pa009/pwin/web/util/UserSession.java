package es.udc.pa.pa009.pwin.web.util;

import java.util.ArrayList;
import java.util.List;

import es.udc.pa.pa009.pwin.model.apuesta.Opcion;


public class UserSession {

	private Long userProfileId;
	private String firstName;
	private boolean admin;

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void setAdmin(boolean v) {
		admin=v;		
	}

	
}

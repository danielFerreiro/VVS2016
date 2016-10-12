package es.udc.pa.pa009.pwin.web.pages.user;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;

import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userservice.IncorrectPasswordException;
import es.udc.pa.pa009.pwin.model.userservice.UserService;
import es.udc.pa.pa009.pwin.web.pages.Index;
import es.udc.pa.pa009.pwin.web.pages.betPages.Bet;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicy;
import es.udc.pa.pa009.pwin.web.services.AuthenticationPolicyType;
import es.udc.pa.pa009.pwin.web.util.CookiesManager;
import es.udc.pa.pa009.pwin.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@AuthenticationPolicy(AuthenticationPolicyType.NON_AUTHENTICATED_USERS)
public class Login {

    @Property
    private String loginName;

    @Property
    private String password;

    @Property
    private boolean rememberMyPassword;

    @SessionState(create=false)
    private UserSession userSession;
    
    @SessionState(create=false)
    private Long idOpcion;
    

    @Inject
    private Cookies cookies;

    @Component
    private Form loginForm;

    @Inject
    private Messages messages;

    @Inject
    private UserService userService;
    
    @InjectPage
    private Bet betPage;

    private UserProfile userProfile = null;


    void onValidateFromLoginForm() {

        if (!loginForm.isValid()) {
            return;
        }

        try {
            userProfile = userService.login(loginName, password, false);
        } catch (InstanceNotFoundException e) {
            loginForm.recordError(messages.get("error-authenticationFailed"));
        } catch (IncorrectPasswordException e) {
            loginForm.recordError(messages.get("error-authenticationFailed"));
        }

    }

    Object onSuccess() {
    	userSession = new UserSession();
        userSession.setUserProfileId(userProfile.getUserProfileId());
        userSession.setFirstName(userProfile.getFirstName());
        userSession.setAdmin( userProfile.getLoginName().toLowerCase().equals("admin") );

        if (rememberMyPassword) {
            CookiesManager.leaveCookies(cookies, loginName, userProfile
                    .getEncryptedPassword());
        }
        
        if (idOpcion != null) {
        	betPage.setIdOpcion(idOpcion);
        	idOpcion=null;
        	return betPage;
        }
        
        return Index.class;

    }

}

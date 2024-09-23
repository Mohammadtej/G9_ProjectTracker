package view;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import javax.faces.application.NavigationHandler;


public class LogoutManagedBean {
    public LogoutManagedBean() {
        super();
    }

    public String doLogout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            NavigationHandler navHandler = facesContext.getApplication().getNavigationHandler();
                    
            // Programmatically navigate to the login page
            navHandler.handleNavigation(facesContext, null, "toLogout");
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();  // Invalidates the session
            }

            // Clear session map
            // facesContext.getExternalContext().getSessionMap().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

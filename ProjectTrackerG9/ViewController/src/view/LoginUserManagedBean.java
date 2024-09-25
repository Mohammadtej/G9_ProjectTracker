package view;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.binding.BindingContainer;

@ManagedBean
@SessionScoped
public class LoginUserManagedBean {
    private String email;
    private String password;
    private Map<String, String> positionMap;

    public LoginUserManagedBean() {
        super();
        positionMap = new HashMap<>();
        // Add mappings between UI labels and position values
        positionMap.put("Project Manager", "PM");
        positionMap.put("Project Leader", "PL");
        positionMap.put("Team Leader", "TL");
        positionMap.put("Team Member", "TM");
        positionMap.put("Admin", "AD");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getPositionMap() {
        return positionMap;
    }
    
    public String doSubmit() {
        FacesContext context = FacesContext.getCurrentInstance();
                
        System.out.println(getEmail());
        System.out.println(getPassword());


        try {
            // Access ADF Application Module to validate credentials
            DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
            
            // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
            ViewObject viewObject = bindings.findIteratorBinding("EmployeesIterator").getViewObject();
            
            viewObject.setWhereClause(String.format("email = '%s' AND password = '%s'", getEmail(), getPassword())); // Assuming plain-text password for simplicity, use encryption in production
            //viewObject.setNamedWhereClauseParam("1", getEmail());
            //viewObject.setNamedWhereClauseParam("2", getPassword());
            //viewObject.setNamedWhereClauseParam("3", positionVal);
            viewObject.executeQuery();

            Row userRow = viewObject.first();
            
            if (viewObject.getRowCount() > 1) {
                FacesMessage message = new FacesMessage("Mutiple users may exist with the same email and password. Please consult your admin");
                context.addMessage(null, message);
                return null;
            }
            
            if (userRow != null) {
                System.out.println("Success in Login");
                
                String positionVal = (String)userRow.getAttribute("Position");
                
                System.out.println(positionVal);
                // Valid credentials
                context.getExternalContext().getSessionMap().put("userId", userRow.getAttribute("EmpId"));                
                context.getExternalContext().getSessionMap().put("userEmail", getEmail());
                context.getExternalContext().getSessionMap().put("userName", userRow.getAttribute("EmployeeName"));
                //context.getExternalContext().getSessionMap().put("userPosition", userRow.getAttribute("Position"));
                context.getExternalContext().getSessionMap().put("userPositionVal", positionVal);
                context.getExternalContext().getSessionMap().put("userPhone", userRow.getAttribute("Phonenumber"));
                context.getExternalContext().getSessionMap().put("userEmail", getEmail());
                context.getExternalContext().getSessionMap().put("userPhotoPath", userRow.getAttribute("Photopath"));
                
                if ("PM".equals(positionVal)) {
                    System.out.println(positionVal);
                    return "toPMDashboard"; // Navigate to admin home
                } else if ("PL".equals(positionVal)) {
                    return "toPLDashboard"; // Navigate to user home
                } else if ("TL".equals(positionVal)) {
                    return "toTLDashboard"; // Navigate to user home
                } else if ("TM".equals(positionVal)) {
                    return "toTMDashboard"; // Navigate to user home
                } else if ("AD".equals(positionVal)) {
                    return "toAdminDashboard"; // Navigate to user home
                } else {
                    FacesMessage message = new FacesMessage("Error occurred in finding the role");
                    context.addMessage(null, message);
                    return null; // If no matching role, stay on login
                }
            } else {
                // Invalid credentials
                FacesMessage message = new FacesMessage("Invalid username or password");
                context.addMessage(null, message);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage message = new FacesMessage("Login failed due to an error");
            context.addMessage(null, message);
            return null;
        }
    }
}

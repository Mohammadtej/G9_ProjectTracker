package view;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import java.math.BigDecimal;

import javax.faces.application.NavigationHandler;

import oracle.adf.controller.ControllerContext;

import oracle.adf.controller.TaskFlowContext;

import oracle.adf.controller.TaskFlowId;
import oracle.adf.controller.TaskFlowId;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.internal.binding.DCTaskFlowBinding;

import javax.faces.context.FacesContext;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.BindingContainer;

@ManagedBean
@SessionScoped
public class RegisterUserManagedBean {
    private String email;
    private String name;
    private String phoneNumber;
    private String position;
    private String password;
    private String confirmPassword;
    private Map<String, String> positionMap;
    private int pmRendered;
    private boolean plRendered;
    private boolean tlRendered;
    
    public RegisterUserManagedBean() {
        super();
        positionMap = new HashMap<>();
        // Add mappings between UI labels and position values
        positionMap.put("Project Manager", "PM");
        positionMap.put("Project Leader", "PL");
        positionMap.put("Team Leader", "TL");
        positionMap.put("Team Member", "TM");
        positionMap.put("Admin", "AD");
        pmRendered = 0;
        plRendered = false;
        tlRendered = false;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public Map<String, String> getPositionMap() {
        return positionMap;
    }

    public String doRegister() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        String positionVal = getPositionMap().get(getPosition());
        
        System.out.println(getEmail());
        System.out.println(getPassword());
        System.out.println(getPosition());
        System.out.println(getName());
        System.out.println(getConfirmPassword());
        System.out.println(getPhoneNumber());
        
        try {
            // Access ADF Application Module
            ApplicationModule am = (ApplicationModule) BindingContext.getCurrent().getCurrentBindingsEntry()
                                          .get("PMAppModuleDataControl");
            ViewObject vo = am.findViewObject("EmployeesVO"); // Replace with your ViewObject name

            // Create a new row in UsersView
            Row newRow = vo.createRow();
            newRow.setAttribute("Email", getEmail());
            newRow.setAttribute("EmployeeName", getName()); // In production, store encrypted passwords
            newRow.setAttribute("Password", getPassword());
            newRow.setAttribute("Phonenumber", getPhoneNumber());            
            newRow.setAttribute("Position", positionVal);


            // Insert the new row into the ViewObject
            vo.insertRow(newRow);
            am.getTransaction().commit();
            
            BigDecimal empId = null;
            vo.setWhereClause(String.format("email = '%s' AND password = '%s' AND position = '%s'", getEmail(), getPassword(), positionVal)); // Assuming plain-text password for simplicity, use encryption in production
            vo.executeQuery();

            Row userRow = vo.first();
            if (userRow != null) {
                empId = (BigDecimal)userRow.getAttribute("EmpId");    
            } else {
                FacesMessage message = new FacesMessage("Error occurred in registering the user");
                context.addMessage(null, message);
                am.getTransaction().rollback();
                return null;
            }
            
            if ("PM".equals(positionVal)) {
                ViewObject voSub = am.findViewObject("ProjectManagersVO"); // Replace with your ViewObject name

                // Create a new row in UsersView
                Row newRowSub = voSub.createRow();
                newRowSub.setAttribute("PmId", empId);
                newRowSub.setAttribute("EmployeeName", getName());


                // Insert the new row into the ViewObject
                vo.insertRow(newRow);
                am.getTransaction().commit();
                
                FacesMessage message = new FacesMessage("User registered successfully!");
                context.addMessage(null, message);
                
                return "backToLogin"; // Navigate to admin home
            } else if ("PL".equals(positionVal)) {
                ViewObject voSub = am.findViewObject("ProjectLeadersVO"); // Replace with your ViewObject name

                // Create a new row in UsersView
                Row newRowSub = voSub.createRow();
                newRowSub.setAttribute("PlId", empId);
                newRowSub.setAttribute("PmId", null);
                newRowSub.setAttribute("EmployeeName", getName());
                newRowSub.setAttribute("IsWorkingOnProject", 0);
                
                // Insert the new row into the ViewObject
                vo.insertRow(newRow);
                am.getTransaction().commit();
                
                FacesMessage message = new FacesMessage("User registered successfully!");
                context.addMessage(null, message);

                return "backToLogin"; // Navigate to user home
            } else if ("TL".equals(positionVal)) {
                ViewObject voSub = am.findViewObject("TeamLeadersVO"); // Replace with your ViewObject name

                // Create a new row in UsersView
                Row newRowSub = voSub.createRow();
                newRowSub.setAttribute("TlId", empId);
                newRowSub.setAttribute("PlId", null);
                newRowSub.setAttribute("EmployeeName", getName());
                newRowSub.setAttribute("IsWorkingOnProject", 0);
                
                // Insert the new row into the ViewObject
                vo.insertRow(newRow);
                am.getTransaction().commit();
                
                FacesMessage message = new FacesMessage("User registered successfully!");
                context.addMessage(null, message);

                return "backToLogin"; // Navigate to user home
            } else if ("TM".equals(positionVal)) {
                ViewObject voSub = am.findViewObject("TeamMembersVO"); // Replace with your ViewObject name

                // Create a new row in UsersView
                Row newRowSub = voSub.createRow();
                newRowSub.setAttribute("TmId", empId);
                newRowSub.setAttribute("TlId", null);
                newRowSub.setAttribute("EmployeeName", getName());
                newRowSub.setAttribute("IsWorkingOnProject", 0);
                
                // Insert the new row into the ViewObject
                vo.insertRow(newRow);
                am.getTransaction().commit();
                
                FacesMessage message = new FacesMessage("User registered successfully!");
                context.addMessage(null, message);

                return "backToLogin"; // Navigate to user home
            } else {
                FacesMessage message = new FacesMessage("Error occurred in finding the role");
                context.addMessage(null, message);
                return null; // If no matching role, stay on login
            }

        } catch (Exception e) {
            e.printStackTrace();

            // Show error message
            FacesMessage message = new FacesMessage("User registration failed due to an error");
            context.addMessage(null, message);
            return null;
        }
    }

    public void changePositionListener(ValueChangeEvent valueChangeEvent) {
        String newValue = (String)valueChangeEvent.getNewValue();
        String positionVal = positionMap.get(newValue);
        
        System.out.println("New Value " + positionVal);
        if (positionVal.equals("PL")) {
            System.out.println("Hereeeeeee at last");
            setPmRendered(1);
            this.plRendered = false;
            this.tlRendered = false;
        } else if(positionVal.equals("TL")) {
            setPmRendered(0);
            this.plRendered = true;
            this.tlRendered = false;
        } else if(positionVal.equals("TM")) {
            setPmRendered(0);
            this.plRendered = false;
            this.tlRendered = true;
        } else {
            setPmRendered(0);
            this.plRendered = false;
            this.tlRendered = false;
        }
    }

    public void setPmRendered(int pmRendered) {
        this.pmRendered = pmRendered;
    }

    public int getPmRendered() {
        return pmRendered;
    }

    public String doCancel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
    
        NavigationHandler navHandler = facesContext.getApplication().getNavigationHandler();
                
        // Programmatically navigate to the login page
        navHandler.handleNavigation(facesContext, null, "toLogout");
        
        return null;
    }
}

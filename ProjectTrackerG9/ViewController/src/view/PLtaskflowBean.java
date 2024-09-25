package view;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import oracle.adf.share.ADFContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "PLtaskflowBean")
@ViewScoped
public class PLtaskflowBean{
    private Map<Integer, String> statusMap;
    
    public PLtaskflowBean() {
        System.out.println("In normal constructor");
        statusMap = new HashMap<>();
        statusMap.put(0, "In Progress");
        statusMap.put(1, "Completed");
        //String ref = ViewProjects();
    }
    
    @PostConstruct
    public void init() {
        // Code that runs when the page is loaded or the bean is instantiated
        System.out.println("PL Bean initialized on page load - post constructor");
        ViewProjects();
        //fetchLatestNotifications();
    }
    
    public Map<Integer, String> getStatusMap() {
        return statusMap;
    }
    
    /*
    public Integer getCurrentUser() {
            String userName = ADFContext.getCurrent().getSecurityContext().getUserName();
            System.out.println("In the getcurrentuser method" + userName);
            ;
            try {
                return Integer.parseInt(userName);  // Convert to Integer
            } catch (NumberFormatException e) {
                // Handle exception in case userName is not a number
                return null;  // Or return a default value, e.g., -1
            }
        }
*/
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }

    public String ViewProjects() {
        System.out.println("Inside ViewProject Method");
        //System.out.println(getCurrentUser());
        // Get the current Application Module's DataControl
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
        ViewObject viewObject = bindings.findIteratorBinding("PLProjectsIterator").getViewObject();
        // Set the bind variable with the value from getCurrentUser
        System.out.println(getCurrentUser());
        viewObject.setNamedWhereClauseParam("retrievedPLId", getCurrentUser());
        // Execute the query
        viewObject.executeQuery();
        return null;
    }

    public void updateDocumentStatus(ValueChangeEvent valueChangeEvent) {
        System.out.println("Update Doc status");
        System.out.println(valueChangeEvent.getOldValue());
        System.out.println(valueChangeEvent.getNewValue());
        System.out.println("Came here " + getStatusMap().get((Integer)valueChangeEvent.getNewValue()));
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("PLDocumentsIterator");
            ApplicationModule am = iterator.getViewObject().getApplicationModule();
            // Get the View Object from the iterator
            ViewObject viewObject = iterator.getViewObject();
            Row currentRow = iterator.getCurrentRow();
            if (currentRow != null) {
                currentRow.setAttribute("Status", getStatusMap().get((Integer)valueChangeEvent.getNewValue()));
                viewObject.executeQuery();
            }
            else {
                FacesMessage message = new FacesMessage("Error occurent while changing the document status");
                context.addMessage(null, message);
                return;
            }
            am.getTransaction().commit();
            //createNotification();
            FacesMessage message = new FacesMessage("Status changed successfully");
            context.addMessage(null, message);
        } catch(Exception e) {
            System.out.println("Exception in the Update document status " + e);
                }
    }

    public void updateModuleStatus(ValueChangeEvent valueChangeEvent) {
        System.out.println("Update Module status");
        System.out.println(valueChangeEvent.getOldValue());
        System.out.println(valueChangeEvent.getNewValue());
        System.out.println("Came here " + getStatusMap().get((Integer)valueChangeEvent.getNewValue()));
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("ModulesForProjectIterator");
            ApplicationModule am = iterator.getViewObject().getApplicationModule();
            // Get the View Object from the iterator
            ViewObject viewObject = iterator.getViewObject();
            Row currentRow = iterator.getCurrentRow();
            if (currentRow != null) {
                currentRow.setAttribute("Status", getStatusMap().get((Integer)valueChangeEvent.getNewValue()));
                viewObject.executeQuery();
            }
            else {
                FacesMessage message = new FacesMessage("Error occurent while changing the document status");
                context.addMessage(null, message);
                return;
            }
            am.getTransaction().commit();
            //createNotification();
            FacesMessage message = new FacesMessage("Status changed successfully");
            context.addMessage(null, message);
        } catch(Exception e) {
            System.out.println("Exception in the Update document status " + e);
                }
    }
}

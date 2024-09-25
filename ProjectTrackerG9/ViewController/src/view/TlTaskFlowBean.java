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

@ManagedBean(name = "TlTaskFlowBean")

public class TlTaskFlowBean implements Serializable {
    private Map<Integer, String> statusMap;
    private Map<Integer, String> statusMap2;
    
    public TlTaskFlowBean() {
        statusMap = new HashMap<>();
        statusMap.put(0, "In Progress");
        statusMap.put(1, "Completed");
        
        statusMap2 = new HashMap<>();
        statusMap2.put(1, "In Progress");
        statusMap2.put(2, "Completed");
        
    }
    
    public Map<Integer, String> getStatusMap() {
        return statusMap;
    }
    
    public Map<Integer, String> getStatusMap2() {
        return statusMap2;
    }
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }
    
    public String viewmodules() {
        System.out.println("Inside ViewProject Method");
        //System.out.println(getCurrentUser());
        // Get the current Application Module's DataControl
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
        ViewObject viewObject = bindings.findIteratorBinding("ModuleForPlIterator").getViewObject();
        // Set the bind variable with the value from getCurrentUser
        System.out.println(getCurrentUser());
        viewObject.setNamedWhereClauseParam("UserTlId", getCurrentUser());
        // Execute the query
        viewObject.executeQuery();
        return null;
    }

    public void updateModuleStatus(ValueChangeEvent valueChangeEvent) {
        System.out.println("Update Module status");
        System.out.println(valueChangeEvent.getOldValue());
        System.out.println(valueChangeEvent.getNewValue());
        System.out.println("Came here " + getStatusMap().get((Integer)valueChangeEvent.getNewValue()));
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("ModuleForPlIterator");
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


    public void updateSubmoduleStatus(ValueChangeEvent valueChangeEvent) {
        System.out.println("Update Module status");
        System.out.println(valueChangeEvent.getOldValue());
        System.out.println(valueChangeEvent.getNewValue());
        System.out.println("Came here " + getStatusMap2().get((Integer)valueChangeEvent.getNewValue()));
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("SubmoduleForModuleIterator");
            ApplicationModule am = iterator.getViewObject().getApplicationModule();
            // Get the View Object from the iterator
            ViewObject viewObject = iterator.getViewObject();
            Row currentRow = iterator.getCurrentRow();
            if (currentRow != null) {
                currentRow.setAttribute("Status", getStatusMap2().get((Integer)valueChangeEvent.getNewValue()));
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

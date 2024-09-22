package view;

import java.math.BigDecimal;

import oracle.adf.share.ADFContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.jbo.ViewObject;
import javax.faces.context.FacesContext;

@ManagedBean(name = "taskFlowManagedBean")
@RequestScoped
public class TaskFlowManagedBean {
    public TaskFlowManagedBean() {
        super();
    }
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }
    
    public void applyFilterToViewObject() {
            // Get the current Application Module's DataControl
            DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
    
            // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
            ViewObject viewObject = bindings.findIteratorBinding("PMProjectsIterator").getViewObject();
    
            // Set the bind variable with the value from getCurrentUser
            viewObject.setNamedWhereClauseParam("retrievedPMId", getCurrentUser());
    
            // Execute the query
            viewObject.executeQuery();
    }
}

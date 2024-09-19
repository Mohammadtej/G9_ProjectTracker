package view;

import oracle.adf.share.ADFContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.jbo.ViewObject;

@ManagedBean(name = "taskFlowManagedBean")
@RequestScoped
public class TaskFlowManagedBean {
    public TaskFlowManagedBean() {
        super();
    }
    
    public Integer getCurrentUser() {
        String userName = ADFContext.getCurrent().getSecurityContext().getUserName();
        System.out.println("In the managed bean" + userName);
        try {
            return Integer.parseInt(userName);  // Convert to Integer
        } catch (NumberFormatException e) {
            // Handle exception in case userName is not a number
            return null;  // Or return a default value, e.g., -1
        }
    }
    
    public void applyFilterToViewObject() {
            // Get the current Application Module's DataControl
            DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
    
            // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
            ViewObject viewObject = bindings.findIteratorBinding("PMProjectsIterator").getViewObject();
    
            // Set the bind variable with the value from getCurrentUser
            viewObject.setNamedWhereClauseParam("retrievedId", getCurrentUser());
    
            // Execute the query
            viewObject.executeQuery();
    }
}

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

@ManagedBean
@ViewScoped
public class NewPlTaskFlowBean {
    private Map<Integer, String> statusMap;
    
    public NewPlTaskFlowBean() {
        System.out.println("Called in the new bean constructor");
    }
    
    @PostConstruct
    public void init() {
        // Code that runs when the page is loaded or the bean is instantiated
        System.out.println("PNew PL Bean initialized on page load - post constructor");
        //fetchLatestNotifications();
    }
    
    
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

}

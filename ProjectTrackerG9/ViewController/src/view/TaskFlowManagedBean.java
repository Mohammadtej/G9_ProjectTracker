package view;

import java.math.BigDecimal;

import oracle.adf.share.ADFContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.jbo.ViewObject;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.faces.application.FacesMessage;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.Row;

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

    public String downloadFile() {
        System.out.println("Download File me aa gaye");
        FacesContext context = FacesContext.getCurrentInstance();
        String filePath = null;
        DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iterator = bindings.findIteratorBinding("PMProjectsIterator");
        
        System.out.println("Download File me aa gaye 2");
        // Get the View Object from the iterator
        ViewObject projectVO = iterator.getViewObject();
        Row currentRow = iterator.getCurrentRow();
        
        System.out.println("Download File me aa gaye 3");
        if (currentRow != null || currentRow.getAttribute("ProjectFilePath") == null) {
            filePath = (String)currentRow.getAttribute("ProjectFilePath");
        }
        else {
            FacesMessage message = new FacesMessage("There is no file added");
            context.addMessage(null, message);
            return null;
        }
        
        System.out.println("Download File me aa gaye 4 " + filePath);
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setHeader("Content-Disposition", "attachment;filename=" + file.getFileName());
                response.setContentType(Files.probeContentType(file));
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
                facesContext.responseComplete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FacesMessage message = new FacesMessage("The file was not found");
            context.addMessage(null, message);
            System.out.println("File not found: " + filePath);
        }
        return null;
    }
}

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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


import javax.faces.application.FacesMessage;

import javax.faces.bean.ViewScoped;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;

@ManagedBean
@ViewScoped
public class TaskFlowManagedBean {
    private String endDate;
    private List<Notification> notifications;

    public TaskFlowManagedBean() {
        System.out.println("Pre construct in Task Flow bean");
        notifications = new ArrayList<>();
    }
    
    @PostConstruct
    public void init() {
        // Code that runs when the page is loaded or the bean is instantiated
        System.out.println("PM Bean initialized on page load");
        applyFilterToViewObject();
        //fetchLatestNotifications();
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }
    
    // Method to retrieve the list of notifications
    public List<Notification> getNotifications() {
        return notifications;
    }
    

    // Add more notifications dynamically (for testing purposes or real use cases)
    public void addNotification(Notification message) {
        System.out.println("Here Hello");
        notifications.add(message);
    }
    
    public void clearNotifications() {
        if (notifications != null && !notifications.isEmpty()) {
            notifications.clear();  // Clear the notifications list
        }
    }
    
    public LocalDate convertToLocalDate(Date date) {
        // Format the LocalDate to a String
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String fetchLatestNotifications() {
        clearNotifications();
        
        // Obtain the current BindingContext
        BindingContext bindingContext = BindingContext.getCurrent();

        // Get the current bindings from the page
        DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();

        // Get the iterator binding for your View Object (replace 'YourIteratorName' with the actual iterator name)
        DCIteratorBinding iteratorBinding = bindings.findIteratorBinding("PMDocumentsIterator");
        
        
        // Ensure the iterator is valid and has rows to iterate over
        if (iteratorBinding != null) {
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            
            // Add 5 days to the current date
            calendar.add(Calendar.DATE, 2);
            
            // Get the updated date from the calendar
            Date newDate = calendar.getTime();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // Access the RowSetIterator to navigate through the rows
            RowSetIterator rowSetIterator = iteratorBinding.getViewObject().createRowSetIterator(null);

            // Start with the first row
            Row currentRow = rowSetIterator.first();
            // Access row attributes (replace 'AttributeName' with actual attribute names)
            String docName = (String)currentRow.getAttribute("DocName");
            Date endDate = (Date)currentRow.getAttribute("EndDate");
            String status = (String)currentRow.getAttribute("Status");
            
            long daysBetween = ChronoUnit.DAYS.between(convertToLocalDate(currentDate), convertToLocalDate(endDate));
            if ("In Progress".equals(status)) {
                
                if (daysBetween < 0) {
                    addNotification(new Notification(String.format("You have missed the deadline (%s) for the following document: %s", convertToLocalDate(endDate).format(formatter), docName)));
                }
                
                else if (daysBetween <= 2) {
                    addNotification(new Notification(String.format("You should complete the document %s before the deadline", docName, convertToLocalDate(endDate).format(formatter))));
                }
            }
            
            // Process the row data as needed
            System.out.println("Row Attribute 1: " + docName);
            System.out.println("Row Attribute 2: " + endDate);
            System.out.println("Gap " + daysBetween);
            
            System.out.println("Heavenly " + currentDate);
            do {
                currentRow = rowSetIterator.next();

                // Access row attributes (replace 'AttributeName' with actual attribute names)
                docName = (String)currentRow.getAttribute("DocName");
                endDate = (Date)currentRow.getAttribute("EndDate");
                status = (String)currentRow.getAttribute("Status");
                
                daysBetween = ChronoUnit.DAYS.between(convertToLocalDate(currentDate), convertToLocalDate(endDate));
                if ("In Progress".equals(status)) {
                    
                    if (daysBetween < 0) {
                        addNotification(new Notification(String.format("You have missed the deadline (%s) for the following document: %s", convertToLocalDate(endDate).format(formatter), docName)));
                    }
                    
                    else if (daysBetween <= 2) {
                        addNotification(new Notification(String.format("You should complete the document %s before the deadline", docName, convertToLocalDate(endDate).format(formatter))));
                    }
                }
                
                // Process the row data as needed
                System.out.println("Row Attribute 1: " + docName);
                System.out.println("Row Attribute 2: " + endDate);
                System.out.println("Gap " + daysBetween);
            }
            // Loop through the rows until after the last one
            while (rowSetIterator.hasNext());
            
            

            // Close the row set iterator to release resources
            rowSetIterator.closeRowSetIterator();
        } else {
            System.out.println("Iterator binding not found.");
        }
        return null;
    }
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }

    
    public String applyFilterToViewObject() {
        System.out.println("When the constructor is called");
        // Get the current Application Module's DataControl
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();

        // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
        ViewObject viewObject = bindings.findIteratorBinding("PMProjectsIterator").getViewObject();

        // Set the bind variable with the value from getCurrentUser
        viewObject.setNamedWhereClauseParam("retrievedPMId", getCurrentUser());

        // Execute the query
        viewObject.executeQuery();
        return null;
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

    public String deleteProject() {
        
        try {
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("PMProjectsIterator");
            
            System.out.println("Download File me aa gaye 2");
            // Get the View Object from the iterator
            ViewObject projectsVO = iterator.getViewObject();
            Row currentRow = iterator.getCurrentRow();
            
            String projectCode = (String)currentRow.getAttribute("ProjectCode");
            
            currentRow.setAttribute("IsDeleted", 1);
            projectsVO.executeQuery();
            
            iterator = bindings.findIteratorBinding("PMDocumentsIterator");
                    
            System.out.println("2nd step");
            // Get the View Object from the iterator
            ViewObject documentsVO = iterator.getViewObject();
            RowSetIterator rowSetDocIterator = iterator.getViewObject().createRowSetIterator(null);

            // Start with the first row
            Row currentDocRow = rowSetDocIterator.first();
            currentDocRow.setAttribute("IsDeleted", 0);
            
            while (rowSetDocIterator.hasNext()) {
                currentDocRow = rowSetDocIterator.next();
                currentDocRow.setAttribute("IsDeleted", 0);
            }
            
            documentsVO.executeQuery();
            
            ApplicationModule am = (ApplicationModule) BindingContext.getCurrent().getCurrentBindingsEntry()
                                          .get("PMAppModuleDataControl");
            ViewObject teamLeadersVO = am.findViewObject("TeamLeadersVO");
            teamLeadersVO.setWhereClause(String.format("ProjectCode = %s", projectCode));
            
            teamLeadersVO.executeQuery();
            
            if (teamLeadersVO.getRowCount() != 1) {
                Row teamLeaderRow = teamLeadersVO.first();
                
                if (teamLeaderRow != null) {
                    teamLeaderRow.setAttribute("ProjectCode", null);
                    teamLeaderRow.setAttribute("IsWorkingOnProject", 0);
                }
            }
            
            ViewObject teamMembersVO = am.findViewObject("TeamMembersVO");
            teamMembersVO.setWhereClause(String.format("ProjectCode = %s", projectCode));
            
            teamMembersVO.executeQuery();
            
            if (teamMembersVO.getRowCount() != 1) {
                Row teamMemberRow = teamLeadersVO.first();
                
                if (teamMemberRow != null) {
                    teamMemberRow.setAttribute("ProjectCode", null);
                    teamMemberRow.setAttribute("IsWorkingOnProject", 0);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in delete project " + e);
        }
        
        return null;
    }
}

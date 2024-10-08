package view;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import javax.faces.context.FacesContext;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import oracle.jbo.ViewObject;

@ManagedBean
@ViewScoped

public class TMNotificationBean {
    
    private List<Notification> notifications;

    public TMNotificationBean() {
        System.out.println("Here");
        notifications = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        // Code that runs when the page is loaded or the bean is instantiated
        System.out.println("In post construct notification");
        String ref = ViewSubModules();
        //ViewProjects();
        //fetchLatestNotifications();
    }

    
    // Method to retrieve the list of notifications
    public List<Notification> getNotifications() {
        return notifications;
    }
    

    // Add more notifications dynamically (for testing purposes or real use cases)
    public void addNotification(Notification message) {
        System.out.println("Here Hello message " + message.getMessage());
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
    
    public String viewNotifications() {
        clearNotifications();
        
        System.out.println("Fetch Notifications in ");
        // Obtain the current BindingContext
        BindingContext bindingContext = BindingContext.getCurrent();

        // Get the current bindings from the page
        DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();

        // Get the iterator binding for your View Object (replace 'YourIteratorName' with the actual iterator name)
        DCIteratorBinding iteratorBinding = bindings.findIteratorBinding("SubmoduleForPMIterator");
        
        
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
            BigDecimal docName = (BigDecimal)currentRow.getAttribute("SubmoduleId");
            Date endDate = (Date)currentRow.getAttribute("EndDate");
            String status = (String)currentRow.getAttribute("Status");
            
            long daysBetween = ChronoUnit.DAYS.between(convertToLocalDate(currentDate), convertToLocalDate(endDate));
            if ("In Progress".equals(status)) {
                
                if (daysBetween < 0) {
                    addNotification(new Notification(String.format("You have missed the deadline (%s) for the following Module: %s", convertToLocalDate(endDate).format(formatter), docName)));
                }
                
                else if (daysBetween <= 2) {
                    addNotification(new Notification(String.format("You should complete the Module %s before the deadline", docName, convertToLocalDate(endDate).format(formatter))));
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
                docName = (BigDecimal)currentRow.getAttribute("SubmoduleId");
                endDate = (Date)currentRow.getAttribute("EndDate");
                status = (String)currentRow.getAttribute("Status");
                
                daysBetween = ChronoUnit.DAYS.between(convertToLocalDate(currentDate), convertToLocalDate(endDate));
                if ("In Progress".equals(status)) {
                    
                    if (daysBetween < 0) {
                        addNotification(new Notification(String.format("You have missed the deadline (%s) for the following module: %s", convertToLocalDate(endDate).format(formatter), docName)));
                    }
                    
                    else if (daysBetween <= 2) {
                        addNotification(new Notification(String.format("You should complete the module %s before the deadline", docName, convertToLocalDate(endDate).format(formatter))));
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
    
    public String ViewSubModules() {
        System.out.println("Inside ViewProject Method");
        //System.out.println(getCurrentUser());
        // Get the current Application Module's DataControl
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
        ViewObject viewObject = bindings.findIteratorBinding("SubmoduleForPMIterator").getViewObject();
        // Set the bind variable with the value from getCurrentUser
        System.out.println(getCurrentUser());
        viewObject.setNamedWhereClauseParam("UserTmId", getCurrentUser());
        // Execute the query
        viewObject.executeQuery();
        return null;
    }

}

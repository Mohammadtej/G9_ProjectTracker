package view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.ApplicationModule;
import oracle.jbo.client.Configuration;



public class createProjectBean {
    private BigDecimal pmid;
    private BigDecimal projectCode;
    private String projectName;
    private BigDecimal plid;
    private Date startDate;
    private Date endDate;
    private String status;
    
    public createProjectBean() {
            
    }

    public void setPmid(BigDecimal pmid) {
        this.pmid = pmid;
    }

    public BigDecimal getPmid() {
        return pmid;
    }

    public void setProjectCode(BigDecimal projectCode) {
        this.projectCode = projectCode;
    }

    public BigDecimal getProjectCode() {
        return projectCode;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setPlid(BigDecimal plid) {
        this.plid = plid;
    }

    public BigDecimal getPlid() {
        return plid;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        //return 2024-09-19 12:43:38.346;
        return endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    
    
    public static String convertToDateTime(String inputDate) throws ParseException {
        //try {
            // Input date format (M/d/yyyy)
            // SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
            // Date date = inputFormat.parse(inputDate);
            // Get current time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTime = timeFormat.format(new Date());
            // Output format (yyyy-dd-MM HH24:MI:SS)
            // SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            // String formattedDate = outputFormat.format(date);
            // Return the formatted date with current time
            return inputDate + " " + currentTime;
        //} catch (ParseException e) {
         //   e.printStackTrace();
         //   return null;
        //}
    }
    public static Timestamp convertStringToTimestamp(String dateString) {
        try {
            // Define the format of the input string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // Parse the input string to a Date object
            Date parsedDate = dateFormat.parse(dateString);
            // Convert Date to Timestamp
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public String doSubmit() {
        /*
        Timestamp startDateTimestamp = null;
        Timestamp endDateTimestamp = null;
                
        try {
            setStartDate(convertToDateTime(getStartDate()));
            setEndDate(convertToDateTime(getEndDate()));
            startDateTimestamp = convertStringToTimestamp(getStartDate());
            endDateTimestamp = convertStringToTimestamp(getEndDate());
        }
        catch(Exception e) {
            System.out.println("Exception in Converting the dates");
        }
        */
        
        System.out.println(getPmid());
        System.out.println(getProjectCode());
        System.out.println(getProjectName());
        System.out.println(getPlid());
        System.out.println(getStatus());
        System.out.println(getStartDate());
        System.out.println(getEndDate());
        
        try {
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("PMProjectsIterator");
            
            // Get the View Object from the iterator
            ViewObject projectVO = iterator.getViewObject();
            
            // Create a new row in the View Object
            Row newRow = projectVO.createRow();
                        
            // Set values for the new row (example values for each column)
            newRow.setAttribute("ProjectCode", getProjectCode()); // Set a unique project code
            newRow.setAttribute("ProjectName", getProjectName());
            newRow.setAttribute("StartDate", getStartDate()); // Today's date
            newRow.setAttribute("EndDate", getEndDate()); // Example end date
            newRow.setAttribute("PmId", 1); // Project manager id (make sure this exists)
            newRow.setAttribute("PlId", getPlid()); // Project leader id (ensure it exists in the PL table)
            newRow.setAttribute("Status", getStatus()); // Example status
            
            // Insert the new row into the View Object
            projectVO.insertRow(newRow);
            
            // Commit the changes to the database
            commitTransaction();
        }
        catch(Exception e) {
            System.out.println("Exception found in submit  button " + e);
        }
        return null;
    }
    
    // Method to commit the transaction
    public void commitTransaction() {
        // Get the Application Module and commit the transaction
        try {
            DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iterator = bindings.findIteratorBinding("PMProjectsIterator");
            ApplicationModule am = iterator.getViewObject().getApplicationModule();
            
            am.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("New project inserted and committed successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error committing transaction: " + e.getMessage(), null));
            System.out.println("Exception found in commit button " + e);
        }
    }
}

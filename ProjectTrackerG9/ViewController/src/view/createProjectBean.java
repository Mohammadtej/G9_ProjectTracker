package view;

import java.io.File;
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
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.ApplicationModule;

import java.util.UUID;

import javax.faces.event.ValueChangeEvent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.apache.myfaces.trinidad.model.UploadedFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class createProjectBean {
    private static BigDecimal projectCodeSeq = new BigDecimal("0");
    private BigDecimal pmid;
    private BigDecimal projectCode;
    private String projectName;
    private BigDecimal plid;
    private Date startDate;
    private Date endDate;
    private String status;
    private static UploadedFile uploadedFile;
   //private static String DIRECTORY_PATH=System.getProperty("user.dir") + File.separator + "uploads";
    private static String DIRECTORY_PATH="/Users/mtejabwa/Documents/projectFiles";
    private String filePath;
    
    @SuppressWarnings("oracle.jdeveloper.java.semantic-warning")
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }
    
    public createProjectBean() {
        String ret = filterProjectLeaders();
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
    
    public String getFilePath() {
        return filePath;
    }
    
    private static synchronized BigDecimal getNextProjectCode() {
        projectCodeSeq = projectCodeSeq.add(BigDecimal.ONE); // Increment by 1
        return projectCodeSeq;
    }
    
    public void handleFileUpload(ValueChangeEvent event) {
        System.out.println("In Handle Upload");
       UploadedFile file = (UploadedFile) event.getNewValue();
       if (file != null) {
           setUploadedFile(file);
           System.out.println("File uploaded: " + uploadedFile.getFilename());
           try{
               File uploadDir = new File(DIRECTORY_PATH);
               if (!uploadDir.exists()) {
                   uploadDir.mkdirs();
               }
               String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getFilename();
               File savedFile = new File(uploadDir, uniqueFilename);
               Files.copy(file.getInputStream(), savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
               String filePath = savedFile.getAbsolutePath();
               System.out.println(filePath);
               this.filePath=filePath;
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File uploaded successfully!"));
           }catch(IOException e){
               e.printStackTrace();
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error uploading file."));
           }
       } else {
           System.out.println("No file uploaded.");
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
        
        setProjectCode(getNextProjectCode());
        setPmid(getCurrentUser());
        
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
            newRow.setAttribute("PmId", getCurrentUser()); // Project manager id (make sure this exists)
            newRow.setAttribute("PlId", getPlid()); // Project leader id (ensure it exists in the PL table)
            newRow.setAttribute("Status", getStatus()); // Example status
            newRow.setAttribute("ProjectFilePath", getFilePath());
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
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }

    public String filterProjectLeaders() {
        System.out.println("Ooooh Nice");
        // Get the current Application Module's DataControl
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        
        // Find the View Object by its iterator binding name (replace with your actual VO iterator name)
        ViewObject viewObject = bindings.findIteratorBinding("ProjectLeadersForAParticularPMIterator").getViewObject();
        
        // Set the bind variable with the value from getCurrentUser
        viewObject.setNamedWhereClauseParam("pmIdForFilterProjectLeader", getCurrentUser());
        
        // Execute the query
        viewObject.executeQuery();
        return null;
    }
}

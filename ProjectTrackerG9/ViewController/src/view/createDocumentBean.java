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
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.ApplicationModule;


public class createDocumentBean {
    private static BigDecimal docIdSeq = new BigDecimal("0");
    private BigDecimal projectCode;
    private BigDecimal docId;
    private String docName;
    private String documentType;
    private Date startDate;
    private Date endDate;
    private BigDecimal plId;
    //private String status;

    public createDocumentBean() {
    }
    
    private static synchronized BigDecimal getNextdocId() {
        docIdSeq = docIdSeq.add(BigDecimal.ONE); // Increment by 1
        return docIdSeq;
    }

    

    public void setProjectCode(BigDecimal projectCode) {
        this.projectCode = projectCode;
    }

    public BigDecimal getProjectCode() {
        return projectCode;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public BigDecimal getDocId() {
        return docId;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
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
        return endDate;
    }

    public void setPlId(BigDecimal plId) {
        this.plId = plId;
    }

    public BigDecimal getPlId() {
        return plId;
    }

    //public void setStatus(String status) {
    //    this.status = status;
    //}

    //public String getStatus() {
    //    return status;
    //}
    
    public BigDecimal getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (BigDecimal) context.getExternalContext().getSessionMap().get("userId");
    }
    
    public String SubmitDocument() {
        try{
            BindingContext bctx = BindingContext.getCurrent();
            DCBindingContainer bindings = (DCBindingContainer) bctx.getCurrentBindingsEntry();
            DCDataControl dataControl = bindings.getDataControl();
            ApplicationModule am = (ApplicationModule) dataControl.getDataProvider();
            ViewObject usersVO = am.findViewObject("DocumentsVO1");
            Row userRow = usersVO.createRow();
            
            setDocId(getNextdocId());
            
            //int userId = getNewUserId();
            //add validation to check if userId exists, generate new if yes.
            userRow.setAttribute("PlId", getCurrentUser());
            userRow.setAttribute("DocId", docId);
            userRow.setAttribute("DocName", docName);
            userRow.setAttribute("DocumentType", documentType);
            userRow.setAttribute("EndDate", endDate);
            userRow.setAttribute("ProjectCode", projectCode);
            userRow.setAttribute("StartDate", startDate);
            userRow.setAttribute("Status", "In Progress");
            // why not? usersVO.insertRow(userRow);
            System.out.println("Before Insert");
            usersVO.insertRow(userRow);
            System.out.println("Before COmmit");
            am.getTransaction().commit();
            System.out.println("After Commit");
            
            //FacesContext context = FacesContext.getCurrentInstance();
            //FacesMessage message = new FacesMessage("Document Created Successfully");
            //context.addMessage(null, message);
            
            
            
            return "returnToDashboard";
        }
        catch(Exception e) {
            System.out.println("Error in submitting the form" + e);
        }
        return null;
    }
}

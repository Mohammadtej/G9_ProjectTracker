package view;

import java.math.BigDecimal;
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
import java.util.Date;

public class createModuleBean {
    private BigDecimal projectCode;
    private BigDecimal moduleId;
    private BigDecimal plId;
    private Date startDate;
    private Date endDate;
    private BigDecimal tlId;
    private String status;

    public createModuleBean() {
    }


    public void setProjectCode(BigDecimal projectCode) {
        this.projectCode = projectCode;
    }

    public BigDecimal getProjectCode() {
        return projectCode;
    }

    public void setModuleId(BigDecimal moduleId) {
        this.moduleId = moduleId;
    }

    public BigDecimal getModuleId() {
        return moduleId;
    }

    public void setPlId(BigDecimal plId) {
        this.plId = plId;
    }

    public BigDecimal getPlId() {
        return plId;
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

    public void setTlId(BigDecimal tlId) {
        this.tlId = tlId;
    }

    public BigDecimal getTlId() {
        return tlId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    
    public String submitModule() {
        try{
            BindingContext bctx = BindingContext.getCurrent();
            DCBindingContainer bindings = (DCBindingContainer) bctx.getCurrentBindingsEntry();
            DCDataControl dataControl = bindings.getDataControl();
            ApplicationModule am = (ApplicationModule) dataControl.getDataProvider();
            ViewObject usersVO = am.findViewObject("Modules1");
            Row userRow = usersVO.createRow();
            //int userId = getNewUserId();
            //add validation to check if userId exists, generate new if yes.
            userRow.setAttribute("EndDate", endDate);
            userRow.setAttribute("ModuleId", moduleId);
            userRow.setAttribute("PlId", plId);
            userRow.setAttribute("ProjectCode", projectCode);
            userRow.setAttribute("StartDate", startDate);
            userRow.setAttribute("Status", status);
            userRow.setAttribute("TlId", tlId);
            // why not? usersVO.insertRow(userRow);
            System.out.println("Before Insert");
            usersVO.insertRow(userRow);
            System.out.println("Before COmmit");
            am.getTransaction().commit();
            System.out.println("After COmmit");
            return "submit";
        }
        catch(Exception e) {
            System.out.println("Error in submitting the form" + e);
        }
        return null;
    }
}

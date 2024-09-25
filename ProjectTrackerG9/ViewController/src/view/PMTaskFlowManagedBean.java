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
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import javax.faces.application.FacesMessage;

import javax.faces.bean.ViewScoped;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.Row;

@ManagedBean
@ViewScoped
public class PMTaskFlowManagedBean {
    public PMTaskFlowManagedBean() {
    }
    
    @PostConstruct
    public void init() {
        // Code that runs when the page is loaded or the bean is instantiated
        System.out.println("PM Bean 2 initialized on page load");
    }
}

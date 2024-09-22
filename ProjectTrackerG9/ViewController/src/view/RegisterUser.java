package view;

import javax.faces.context.FacesContext;

import javax.naming.InitialContext;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import weblogic.security.providers.authentication.DefaultAuthenticatorMBean;
import weblogic.management.security.RealmMBean;
import weblogic.management.MBeanHome;
import javax.management.MBeanServerConnection;
import javax.naming.InitialContext;
import javax.management.ObjectName;


public class RegisterUser {
    private String userName;
    private String email;
    private String password;

    public RegisterUser() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    

    public String doRegister() {
        try {
            System.out.println(getUserName());
            System.out.println(getPassword());
            System.out.println(getEmail());

            // Step 1: Lookup the MBeanServerConnection
            InitialContext ctx = new InitialContext();
            MBeanServerConnection mBeanServerConnection = (MBeanServerConnection) ctx.lookup("java:comp/env/jmx/runtime");
            
            System.out.println(mBeanServerConnection);
            // Step 2: Use the ObjectName for the SecurityRealm to lookup RealmMBean
            ObjectName realmObjectName = new ObjectName("Security:Name=myrealm,Type=Realm");
            RealmMBean realmMBean = (RealmMBean) javax.management.MBeanServerInvocationHandler.newProxyInstance(
                mBeanServerConnection, realmObjectName, RealmMBean.class, false);

            // Step 3: Get the DefaultAuthenticatorMBean
            ObjectName authenticatorObjectName = (ObjectName)realmMBean.getAuthenticationProviders()[0];
            DefaultAuthenticatorMBean authenticatorMBean = (DefaultAuthenticatorMBean) javax.management.MBeanServerInvocationHandler.newProxyInstance(
                mBeanServerConnection, authenticatorObjectName, DefaultAuthenticatorMBean.class, false);

            // Step 4: Check if the user already exists
            if (authenticatorMBean.userExists(getUserName())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User already exists"));
                return "failure";
            }

            // Step 5: Add the user to WebLogic's LDAP (password and email are stored here)
            authenticatorMBean.createUser(getUserName(), getPassword(), getEmail());

            // Step 6: Assign the user to the role (application role defined in jazn-data.xml)
            authenticatorMBean.addMemberToGroup("Project Manager", getUserName());  // Group/role mapping

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User registered successfully"));
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error registering user"));
            return "failure";
        }
    }
}

package com.easytag.web.beans;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.web.utils.JSFHelper;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class UserBean implements Serializable {
    private Long userId;
    private User user;
    
    @ManagedProperty("#{currentUserBean}")
    private CurrentUserBean currentUserBean;
    
    @EJB
    private UserManagerLocal userMan;

    @PostConstruct
    private void init() {
        //setUserId(new JSFHelper().getRequest().getParameter("id"));
    }

    public User getCurrentUser() {
        return currentUserBean.getUser();
    }
    
    public Long getCurrentUserId() {
        return currentUserBean.getUserId();
    }
    
    public String getUserId() {
        return userId==null ? null : userId.toString();
    }

    public User getUser() {
        return user;
    }

    public UserProfile getUserProfile() {
        return userMan.getUserProfile(user);
    }
    
    public void setUserId(String userId) {
        System.out.println("setUserId = " + userId);
        if (userId == null || userId.isEmpty()) {
            this.userId = currentUserBean.getUserId();
            user = currentUserBean.getUser();
            System.out.println("... resolved to " + this.userId);
            return;
        }
        try {
            this.userId = Long.parseLong(userId);
            user = userMan.getUserById(this.userId);
        } catch (Exception ex) {
            this.userId = null;
        }
    }
    
    public boolean isCurrent() {
        if (currentUserBean.getUserId() == null)
            return false;
        if (this.userId == null)
            return true;
        return currentUserBean.getUserId().toString().equals(this.userId.toString());
    }

    public void setCurrentUserBean(CurrentUserBean currentUserBean) {
        this.currentUserBean = currentUserBean;
    }
    
    public String getDisabledClass(boolean disable){
        return disable ? "": "disabled";        
    }
    
    public String disableCreateButton(String userId){
        if (userId == "" || userId == null){
            return "";
        }
        Long id = Long.parseLong(userId);
        return (id == this.userId) ? "": "disabled";        
    }
    
    public User findById(String userId) {
        Long id = null;
        try {
            id = Long.parseLong(userId);
            System.out.println("userid: " + id);
        } catch (Exception ex) {
        }
        if (id == null) { // || id == this.userId
            return user;
        }
        return userMan.getUserById(id);
    }
    
    public String redirectIfNotAuthorized() {
        System.out.println("UserBean.redirectIfNotAuthorized()");
        JSFHelper helper = new JSFHelper();
        if (helper.getCurrentUserId() == null) {
            //helper.redirect("/index?faces-redirect=true");
            return "/index?faces-redirect=true";
        }
        return null;
    }
}

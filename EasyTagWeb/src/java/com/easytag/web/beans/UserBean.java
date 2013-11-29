package com.easytag.web.beans;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.web.utils.JSFHelper;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@SessionScoped
public class UserBean implements Serializable {

    @EJB
    UserManagerLocal userMan;
    
    private Long userId;
    private User user;

    @PostConstruct
    private void init() {
        this.userId = new JSFHelper().getCurrentUserId();
        this.user = userMan.getUserById(userId);
    }

    public Long getUserId() {
        return userId;
    }
    
    public User getUser() {
        return user;
    }
    
    public boolean isCurrent(String id) {
        if (userId == null)
            return false;
        if (id == null || id.equals(""))
            return true;
        return userId.toString().equals(id);
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
    
    public void redirectIfNotAuthorized() {
        System.out.println("UserBean.redirectIfNotAuthorized()");
        JSFHelper helper = new JSFHelper();
        if (helper.getCurrentUserId() == null) {
            helper.redirect("/index?faces-redirect=true");
        };
    }
}

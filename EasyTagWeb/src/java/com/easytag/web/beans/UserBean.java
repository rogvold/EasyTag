package com.easytag.web.beans;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.managers.UserManagerLocal;
import com.easytag.web.utils.SessionUtils;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@ManagedBean
@ViewScoped
public class UserBean {

    @EJB
    UserManagerLocal userMan;
    
    private Long userId;
    private User user;

    @PostConstruct
    private void init() {
        this.userId = SessionUtils.getUserId();
        this.user = userMan.getUserById(userId);
    }

    public Long getUserId() {
        return userId;
    }
}

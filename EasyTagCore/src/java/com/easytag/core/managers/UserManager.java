package com.easytag.core.managers;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.enums.UserType;
import com.easytag.exceptions.TagException;
import com.easytag.utils.StringUtils;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rogvold
 */
@Stateless
public class UserManager implements UserManagerLocal {

    @PersistenceContext(unitName = "EasyTagCorePU")
    EntityManager em;

    @Override
    public boolean userExists(String email) throws TagException {
        if (email == null) {
            throw new TagException("email is not passed");
        }
        if (!StringUtils.isValidEmail(email)) {
            throw new TagException("email is not valid");
        }

        try {
            User u = (User) em.createQuery("select u from User u where u.email = :email").setParameter("email", email).getSingleResult();
            if (u != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(User.class, id);
    }

    @Override
    public User login(String email, String password) throws TagException {
        checkAuthorisationData(email, password);
        return getUserByEmail(email);
    }

    @Override
    public User updateUserInfo(Long userId, String firstName, String lastName, String avatarSrc) {
        if (userId == null) {
            return null;
        }
        User u = getUserById(userId);
        if (u == null) {
            return null;
        }
        u.setAvatarSrc(avatarSrc);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        return em.merge(u);
    }

    @Override
    public User registerUser(String email, String password, UserType type) throws TagException {
        if (userExists(email)) {
            throw new TagException("user with email \"" + email + "\" already exists in the system");
        }
        if (type == null) {
            type = UserType.USER;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUserType(type);
        return em.merge(user);
    }

    @Override
    public User updateUser(User user) throws TagException {
        if (user == null) {
            throw new TagException("user is null");
        }
        if (user.getId() == null) {
            throw new TagException("user id is null");
        }
        return em.merge(user);
    }

    @Override
    public void checkAuthorisationData(String email, String password) throws TagException {
        if (email == null || email.equals("")) {
            throw new TagException("email is not specified");
        }
        if (!StringUtils.isValidEmail(email)) {
            throw new TagException("email is not valid");
        }
        if (password == null || password.equals("")) {
            throw new TagException("password is not specified");
        }

        User u = getUserByEmail(email);
        if (u == null) {
            throw new TagException(" user with email \"" + email + "\" is not registered in system");
        }
        if (!u.getPassword().equals(password)) {
            throw new TagException("password is incorrect");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        List<User> list = em.createQuery("select u from User u where u.email = :email").setParameter("email", email).getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}

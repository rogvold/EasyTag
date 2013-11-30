package com.easytag.core.managers;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
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
    public UserProfile getUserProfile(User user) {
        Long userId = user.getId();
        return em.find(UserProfile.class, userId);
    }
    
    @Override
    public User login(String email, String password) throws TagException {
        checkAuthorisationData(email, password);
        return getUserByEmail(email);
    }

    @Override
    public UserProfile updateUserProfile(Long userId, String firstName, String lastName, String avatarSrc, String email, String description) {
        if (userId == null) {
            return null;
        }
        User user = getUserById(userId);
        if (user == null) {
            return null;
        }
        UserProfile profile = getUserProfile(user);
        profile.setAvatarSrc(avatarSrc);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDescription(description);
//        if (email != null) {
//            setEmailToProfile(profile, user, email);
//        }
        return em.merge(profile);
    }
    
    private void setEmailToProfile(UserProfile profile, User user, String email) {
        System.out.println("0");
        profile.setEmail(email);
        System.out.println("1");
        if (user == null) {
            user = getUserById(profile.getId());
        }
        System.out.println("2");
        user.setEmail(email);
        System.out.println("3");
        em.merge(user);
        System.out.println("4");
    }
    
    @Override
    public UserProfile updateUserProfile(UserProfile profile) {
        return this.updateUserProfile(profile.getId(), profile.getFirstName(), profile.getLastName(), profile.getAvatarSrc(), profile.getEmail(), profile.getDescription());
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
        User createdUser = em.merge(user);
        UserProfile profile = new UserProfile(createdUser);
        em.merge(profile);
        return createdUser;
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

    @Override
    public List<User> getAllUsers() {
        List<User> list = em.createQuery("select u from User u where u.userType = :tp").setParameter("tp", UserType.USER).getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }
}

package com.easytag.core.managers;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
import com.easytag.core.enums.UserType;
import com.easytag.exceptions.TagException;
import com.easytag.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
        profile.recalculateFullName();
        return em.merge(profile);
    }

    private void setEmailToProfile(UserProfile profile, User user, String email) {
        profile.setEmail(email);
        if (user == null) {
            user = getUserById(profile.getId());
        }
        user.setEmail(email);
        em.merge(user);
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
    public List<UserProfile> getAllUserProfiles() {
        List<User> users = em.createQuery("select u from User u where u.userType = :tp").setParameter("tp", UserType.USER).getResultList();
        if (users == null) {
            return Collections.emptyList();
        }
        List<UserProfile> profiles = new ArrayList<UserProfile>(users.size());
        for (User user : users) {
            profiles.add(getUserProfile(user));
        }
        if (profiles.isEmpty()) {
            return Collections.emptyList();
        }
        return profiles;
    }

    @Override
    public User openIdAuthorization(String openIdKey) throws TagException {
        if (openIdKey == null) {
            throw new TagException("openId key is not specified");
        }
        Query q = em.createQuery("select u from User u where u.openId = :openId").setParameter("openId", openIdKey);
        List<User> users = q.getResultList();
        if (users == null || users.size() == 0) {
            User u = new User();
            u.setOpenId(openIdKey);
            u.setUserType(UserType.USER);

            User createdUser = em.merge(u);
            UserProfile profile = new UserProfile(createdUser);
            em.merge(profile);

            return createdUser;
        }

        User u = users.get(0);
        return u;

    }

    @Override
    public UserProfile findProfileByEmail(String email) {
        try {
            return em.createQuery("select p from UserProfile p where p.email = :email", UserProfile.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}

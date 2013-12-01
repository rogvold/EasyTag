package com.easytag.core.managers;

import com.easytag.core.entity.jpa.User;
import com.easytag.core.entity.jpa.UserProfile;
import com.easytag.core.enums.UserType;
import com.easytag.exceptions.TagException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface UserManagerLocal {

    public boolean userExists(String email) throws TagException;

    public User getUserById(Long id);
    
    public UserProfile getUserProfile(User user);

    public User login(String email, String password) throws TagException;

    public UserProfile updateUserProfile(Long userId, String firstName, String lastName, String avatarSrc, String email, String description);
    
    public UserProfile updateUserProfile(UserProfile profile);

    public User registerUser(String email, String password, UserType type) throws TagException;

    public User updateUser(User user) throws TagException;

    public void checkAuthorisationData(String email, String password) throws TagException;
    
    public User getUserByEmail(String email);
    
    public List<UserProfile> getAllUserProfiles();
    
    public User openIdAuthorization(String openIdKey) throws TagException;
    
}

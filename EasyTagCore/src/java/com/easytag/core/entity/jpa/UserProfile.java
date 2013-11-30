package com.easytag.core.entity.jpa;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Ovchinnikov Valeriy (email: kremsnx@gmail.com)
 */
@Entity
@Table(name="profiles")
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id // todo: make foreign key in order to delete profile when deleting user
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @Column(length = 2000)
    private String description;
    private String avatarSrc;
    @Transient
    private String fullName;

    public String getFullName() {
        if (fullName == null || fullName.isEmpty()) {
            fullName = calculateFulName();
        }
        return fullName;
    }

    @Deprecated
    public String getUserFullName() {
        return getFullName();
    }
    
    public UserProfile() {
    }

    public UserProfile(User user, String firstName, String lastName, String description, String avatarSrc) {
        this(user);
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.avatarSrc = avatarSrc;
        this.fullName = calculateFulName();
    }

    public UserProfile(User user) {
        this.email = user.getEmail();
        this.id = user.getId();
        this.fullName = calculateFulName();
    }
    
    private String calculateFulName() {
        if (firstName != null && !firstName.isEmpty()) {
            if (lastName != null && !lastName.isEmpty()) {
                return firstName + lastName;
            }
            return firstName;
        }
        if (lastName != null && !lastName.isEmpty()) {
                return lastName;
        }
        return email;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
        
    public String getAvatarSrc() {
        return avatarSrc;
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void updateFromProfile(UserProfile anotherProfile) {
        this.firstName = anotherProfile.firstName;
        this.lastName = anotherProfile.lastName;
        this.email = anotherProfile.email;
        this.description = anotherProfile.description;
        this.avatarSrc = anotherProfile.avatarSrc;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            return true;
        }
        if (object instanceof UserProfile) {
            UserProfile other = (UserProfile) object;
            if (this.id != null && this.id.equals(other.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "com.easytag.core.entity.jpa.UserProfile[id=" + id + "]";
    }
}

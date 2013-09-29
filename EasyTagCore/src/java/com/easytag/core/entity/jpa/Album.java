package com.easytag.core.entity.jpa;

import com.easytag.core.enums.AlbumStatus;
import com.easytag.core.enums.AlbumType;
import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@Entity
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long creatorId;
    @Column(length = 2000)
    private String name;
    @Column(length = 10000)
    private String description;
    @Column(length = 2000)
    private String tags;
    @Column(length = 2000)
    private String categories;
    private Long parentId;
    @Enumerated(EnumType.STRING)
    private AlbumStatus status;
    @Enumerated(EnumType.STRING)
    private AlbumType type;
    private String avatarSrc;

    public Album() {
    }

    public Album(Long creatorId, String name, String description, String tags, String categories, Long parentId, AlbumStatus status, AlbumType type, String avatarSrc) {
        this.creatorId = creatorId;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.categories = categories;
        this.parentId = parentId;
        this.status = status;
        this.type = type;
        this.avatarSrc = avatarSrc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public AlbumStatus getStatus() {
        return status;
    }

    public void setStatus(AlbumStatus status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public AlbumType getType() {
        return type;
    }

    public void setType(AlbumType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Album)) {
            return false;
        }
        Album other = (Album) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.easytag.core.entity.jpa.Album[ id=" + id + " ]";
    }
}

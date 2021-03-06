package com.easytag.core.entity.jpa;

import com.easytag.core.enums.PhotoStatus;
import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
@Entity
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2000)
    private String name;
    @Column(length = 10000)
    private String description;
    @Column(length = 2000)
    private String tags;
    @Enumerated(EnumType.STRING)
    private PhotoStatus status;
    private Integer number;
    private Long albumId;
    private Long fileId;
    private Long previewId;
    private Long height; 
    private Long width;   
    private Long original_id;    

    public Photo() {
    }

    public Photo(String name, String description, String tags, PhotoStatus status, Integer number, Long albumId, Long fileId, Long height, Long width) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.status = status;
        this.number = number;
        this.albumId = albumId;
        this.fileId = fileId;
        this.previewId = null;
        this.height = height;
        this.width = width;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public PhotoStatus getStatus() {
        return status;
    }

    public void setStatus(PhotoStatus status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Long getPreviewId() {
        return previewId;
    }

    public void setPreviewId(Long previewId) {
        this.previewId = previewId;
    }
    
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }   

    public Long getOriginal_id() {
        return original_id;
    }

    public void setOriginal_id(Long original_id) {
        this.original_id = original_id;
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
        if (!(object instanceof Photo)) {
            return false;
        }
        Photo other = (Photo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.easytag.core.entity.jpa.Photo[ id=" + id + " ]";
    }
}

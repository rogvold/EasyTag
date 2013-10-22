package com.easytag.core.entity.jpa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Entity class for file.
 * @author danshin
 */
@Entity @Table(name = "files")
public class EasyTagFile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    private Long userId;
    @Column(name="original_name", nullable = false)
    private String originalName;
    @Column(name="current_path", nullable = false)
    private String currentPath;
    @Column(name="md5")
    private String md5;
    @Column(name="file_size", nullable=false)
    private Long fileSize;
    @Column(name="content_type", nullable=false)
    private String contentType;

    public EasyTagFile() {
    }

    public EasyTagFile(Long userId, String originalName, String currentPath, String contentType) {
        this.userId = userId;
        this.originalName = originalName;
        this.currentPath = currentPath;
        this.contentType = contentType;
    }

    public EasyTagFile(Long id, Long userId, String originalName, String currentPath, String md5, Long fileSize, String contentType) {
        this.id = id;
        this.userId = userId;
        this.originalName = originalName;
        this.currentPath = currentPath;
        this.md5 = md5;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
        if (!(object instanceof EasyTagFile)) {
            return false;
        }
        EasyTagFile other = (EasyTagFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.easytag.core.entity.jpa.EasyTagFile[ id=" + id + " ]";
    }
    
}

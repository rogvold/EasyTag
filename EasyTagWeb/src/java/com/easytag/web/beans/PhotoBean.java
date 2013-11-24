package com.easytag.web.beans;

import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.managers.PhotoManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PhotoBean implements Serializable {
    
    @EJB
    private PhotoManagerLocal photoMan;
    
    private Long photoId;
    private Photo photo;
    
    
    public Photo getPhoto() {
        return photo;
    }
    
    public Photo findById(String photoId) {
        Long id = null;
        try {
            id = Long.parseLong(photoId);
        } catch (Exception ex) {
        }
        if (id == null || id == this.photoId) {
            return getPhoto();
        }
        return photoMan.getPhotoById(id);
    }
    

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
        this.photo = photoMan.getPhotoById(photoId);
    }
}

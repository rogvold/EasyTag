package com.easytag.web.beans;

import com.easytag.core.entity.jpa.Photo;
import com.easytag.core.managers.PhotoManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PhotoBean implements Serializable {
    
    @EJB
    private PhotoManagerLocal photoMan;
    
    private Long photoId;
    private Long prevId;
    private Long nextId;    
    private Photo photo;
    private List<Photo> albPh;
    private List<Photo> row;
    
    
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
    

    public String getPhotoId() {
        return photoId == null ? null : photoId.toString();
    }


    public void setPhotoId(String photoId) {
        System.out.println("setPhotoId: " + photoId);
        this.photoId = Long.parseLong(photoId);        
        this.photo = photoMan.getPhotoById(this.photoId);
        this.albPh = photoMan.getPhotosInAlbum(photo.getAlbumId());        
        
        int curr = albPh.indexOf(photo);  //System.out.println("Current phIndex: " + curr);                     
        int next = (curr + 1 == albPh.size())? 0: (curr + 1);        
        int prev = (curr == 0)? (albPh.size() - 1): (curr - 1);
        int afterNext = (next + 1 == albPh.size())? 0: (next + 1);
        this.nextId = albPh.get(next).getId(); 
        this.prevId = albPh.get(prev).getId();
        
        row = new ArrayList<Photo>(4);
        row.add(albPh.get(prev));
        row.add(albPh.get(curr));
        row.add(albPh.get(next));
        row.add(albPh.get(afterNext));        
    }

    public Long getPrevId() {
        return prevId;
    }

    public void setPrevId(Long prevId) {
        this.prevId = prevId;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }
    
    public Long getPreviewId(int ind) {
        return row.get(ind).getPreviewId();
    }   
    
    public Long getPhId(int ind) {
        return row.get(ind).getId();
    }   
}

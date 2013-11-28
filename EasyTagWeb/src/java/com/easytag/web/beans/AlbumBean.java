package com.easytag.web.beans;

import com.easytag.core.entity.jpa.Album;
import com.easytag.core.entity.jpa.User;
import com.easytag.core.managers.AlbumManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class AlbumBean implements Serializable {
    @EJB
    private AlbumManagerLocal albumMan;
    
    private Long albumId = null;

    public Album getAlbum() {
        return albumMan.getAlbumById(albumId);
    }
    
    public Album findById(String albumId) {
        System.out.println("findById: albumId = " + albumId);
        Long id = null;
        try {
            id = Long.parseLong(albumId);
        } catch (Exception ex) {
        }
        if (id == null || id == this.albumId) {
            return getAlbum();
        }
        return albumMan.getAlbumById(id);
    }
    
    public String getAlbumId() {
        return albumId == null ? null : albumId.toString();
    }

    public void setAlbumId(String albumId) {
        System.out.println("albumId="+albumId);
        this.albumId = Long.parseLong(albumId);
    }
    
}

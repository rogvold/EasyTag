package com.easytag.web.beans;

import com.easytag.core.entity.jpa.Album;
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
    
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
    
}

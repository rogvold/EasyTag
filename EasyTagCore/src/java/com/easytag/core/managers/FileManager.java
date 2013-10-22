package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTagFile;
import com.easytag.utils.FileUtils;
import com.easytag.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FileManager implements FileManagerLocal {

    @PersistenceContext
    EntityManager em;
    
    @Override
    public EasyTagFile addFile(Long userId, String originalName, String path, String contentType) {
        File f = new File(path);
        if (!f.canRead()) {
            throw new IllegalArgumentException("Specified file is not readable: " + path);
        }   
        try {
            EasyTagFile file = new EasyTagFile(userId, originalName, path, contentType);
            file.setFileSize(f.length());
            file.setMd5(StringUtils.toHexString(FileUtils.getMD5(f)));
            em.persist(file);
            return file;
        } catch (Exception ex) {
            throw new EJBException("File record wasn't created.", ex);
        }
    }

    @Override
    public EasyTagFile findFileById(Long userId, long fileId) {
        //TODO: Check user permissions!
        
        return em.find(EasyTagFile.class, fileId);
    }
    
}

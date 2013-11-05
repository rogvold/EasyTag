package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTagFile;
import com.easytag.utils.FileUtils;
import com.easytag.utils.StringUtils;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
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
    
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            //int a = sbi.get
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    } 
    
    public static void makePreview(String srcFile, String distFile) throws IOException {
        File imgF = new File(srcFile);        
        BufferedImage sbi = ImageIO.read(imgF);  
        int w = sbi.getWidth();
        int h = sbi.getHeight();
        BufferedImage scImg = scale(sbi, sbi.getType(),
                PREVIEW_WIDTH, PREVIEW_HEIGHT, (double)PREVIEW_WIDTH / w, (double)PREVIEW_HEIGHT/ h);
        File outputfile = new File(distFile);
        ImageIO.write(scImg, "jpg", outputfile);              
    }
    
    private final static int PREVIEW_WIDTH = 280;
    private final static int PREVIEW_HEIGHT = 180;
    
}

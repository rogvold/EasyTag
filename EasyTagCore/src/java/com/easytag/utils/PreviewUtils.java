/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytag.utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Vitaly
 */
public class PreviewUtils {
    private final static int PREVIEW_WIDTH = 280;
    private final static int PREVIEW_HEIGHT = 180;   
    
    public final static String  POSTFIX = "prev280x180"; 
     
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
}

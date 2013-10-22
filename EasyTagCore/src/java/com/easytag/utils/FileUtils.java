/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytag.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author danshin
 */
public abstract class FileUtils {
    
    public static byte[] getMD5(File f) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        InputStream is = null;
        DigestInputStream dis = null;
        try {
            is = new FileInputStream(f);
            dis = new DigestInputStream(is, md);
            byte[] buf = new byte[10 * 1024];
            while (is.read(buf) >= 0);
            return md.digest();
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }

}

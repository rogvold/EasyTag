package com.easytag.core.managers;

import com.easytag.core.entity.jpa.EasyTagFile;

public interface FileManagerLocal {

    EasyTagFile addFile(Long userId, String originalName, String path, String contentType);

    EasyTagFile findFileById(long fileId);
    
}

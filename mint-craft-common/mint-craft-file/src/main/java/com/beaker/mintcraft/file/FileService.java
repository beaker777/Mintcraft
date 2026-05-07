package com.beaker.mintcraft.file;

import java.io.InputStream;

/**
 * @Author beaker
 * @Date 2026/5/7 20:32
 * @Description 文件服务
 */
public interface FileService {

    /**
     * 文件上传
     * @param path
     * @param fileStream
     * @return
     */
    public boolean upload(String path, InputStream fileStream);
}

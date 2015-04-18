
package com.morncloud.publics.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (C) 2013 MORNCLOUD All Rights Reserved This program contains
 * proprietary information which is a trade secret of MORNCLOUD and/or its
 * affiliates and also is protected as an unpublished work under applicable
 * Copyright laws. Recipient is to retain this program in confidence and is not
 * permitted to use or make copies thereof other than as permitted in a written
 * agreement with MORNCLOUD, unless otherwise expressly allowed by applicable
 * laws
 */

public class FileUtil {
    public static void copyFile(InputStream source, String target)
            throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(target));
        byte[] buffers = new byte[1024 * 10];
        int len;
        while ((len = source.read(buffers)) != -1) {
            bos.write(buffers, 0, len);
        }
        bos.flush();
        bos.close();
        source.close();
    }

    public static void copyFile(String source, String target)
            throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(target));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                source));
        byte[] buffers = new byte[1024 * 10];
        int len;
        while ((len = bis.read(buffers)) != -1) {
            bos.write(buffers, 0, len);
        }
        bos.flush();
        bos.close();
        bis.close();
    }
}

package com.speechpro.onepass.core.utils;

import android.util.Log;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author volobuev
 * @since 01.12.2015
 */
public final class ZipUtil {

    private static final int BUFFER = 2048;


    public static void zip(final String[] files, final String zipFile) {
        try {
            BufferedInputStream origin;
            final FileOutputStream dest = new FileOutputStream(zipFile);
            final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte[] data = new byte[BUFFER];
            for (final String _file : files) {
                final FileInputStream fi = new FileInputStream(_file);
                origin = new BufferedInputStream(fi, BUFFER);
                final ZipEntry entry = new ZipEntry(_file.substring(_file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (final Exception e) {
            Log.v(ZipUtil.class.getSimpleName(), e.getMessage(), e);
        }
    }


    public static boolean verify(final InputStream is, final String destination) throws IOException {
        return verify(is, destination, false);
    }


    public static boolean verify(final InputStream is, final String destination, final boolean checkSize) throws IOException {
        final ZipInputStream zis = new ZipInputStream(is);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            final String filename = ze.getName();
            final File f = new File(destination + filename);
            if (!f.exists()) {
                return false;
            }
            if (checkSize) {
                if (f.isFile() && f.length() != ze.getSize()) {
                    return false;
                }
            }
            zis.closeEntry();
        }
        zis.close();
        is.close();
        return true;
    }


    public static boolean unzip(final InputStream is, final String destination) {
        final ZipInputStream zis;
        try {
            zis = new ZipInputStream(is);
            ZipEntry ze;
            final byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                final String filename = ze.getName();
                if (ze.isDirectory()) {
                    final File fmd = new File(destination + filename);
                    //noinspection ResultOfMethodCallIgnored
                    fmd.mkdirs();
                    continue;
                }
                final FileOutputStream out = new FileOutputStream(destination + filename);
                while ((count = zis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.close();
                zis.closeEntry();
            }
            zis.close();
            is.close();
        } catch (final IOException e) {
            Log.d(ZipUtil.class.getSimpleName(), e.getMessage(), e);
            return false;
        }
        return true;
    }

}

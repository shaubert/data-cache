package com.shaubert.cache.persistance;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class Files {

    public static final String TAG = Files.class.getSimpleName();

    public static boolean createNomedia(File where) {
        try {
            File nomedia = new File(where, ".nomedia");
            if (nomedia.exists() || nomedia.createNewFile()) {
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "failed to create .nomedia file", e);
        }
        return false;
    }

    public static boolean copy(File from, File to) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            //noinspection ResultOfMethodCallIgnored
            to.getParentFile().mkdirs();

            inputStream = new FileInputStream(from);
            outputStream = new FileOutputStream(to);
            byte[] buffer = new byte[1024 * 50];
            int len;
            while ((len = inputStream.read(buffer)) >= 0) {
                if (len > 0) {
                    outputStream.write(buffer, 0, len);
                }
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "failed to copy file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteDir(child)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return file.delete();
        }
    }

}
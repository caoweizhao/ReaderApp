package com.example.caoweizhao.readerapp.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class FileHelper {

    public static File getBooksDir() {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, "/ReaderApp/Books");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}

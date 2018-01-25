/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.example.caoweizhao.readerapp.util;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(FileHelper.getBooksDir(), "pic.jpg");
        Log.d("FileUtil",file.getAbsolutePath());
        return file;
    }
}

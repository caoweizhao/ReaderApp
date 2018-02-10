package com.example.caoweizhao.readerapp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class Constant {

    public static AtomicInteger TASKID = new AtomicInteger();
    public static final String BASE_URL = "http://192.168.191.1:8080/";
    //public static final String BASE_URL = "http://192.168.2.103:8080/";

    public static final String ACTION_UPDTATE = "UPDATE";
    public static final String ACTION_DONE = "DONE";

    public static final String TAMP_FILE_DIR = "/storage/emulated/0/TbsReaderTemp";
}

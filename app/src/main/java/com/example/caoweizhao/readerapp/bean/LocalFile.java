package com.example.caoweizhao.readerapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by caoweizhao on 2018-2-5.
 */

public class LocalFile implements Parcelable {

    private String name;

    private File referenceFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getReferenceFile() {
        return referenceFile;
    }

    public void setReferenceFile(File referenceFile) {
        this.referenceFile = referenceFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.referenceFile);
    }

    public LocalFile(String name, File file) {
        this.name = name;
        this.referenceFile = file;
    }

    protected LocalFile(Parcel in) {
        this.name = in.readString();
        this.referenceFile = (File) in.readSerializable();
    }

    public static final Creator<LocalFile> CREATOR = new Creator<LocalFile>() {
        @Override
        public LocalFile createFromParcel(Parcel source) {
            return new LocalFile(source);
        }

        @Override
        public LocalFile[] newArray(int size) {
            return new LocalFile[size];
        }
    };
}

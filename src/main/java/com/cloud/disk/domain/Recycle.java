package com.cloud.disk.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Entity
public class Recycle{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private long fileId;
    @Column(length = 100)
    private String filename;
    private long filesize;
    @CreationTimestamp
    private java.util.Date addtime;
    private long adduserid;
    private int filetype;

    public Recycle() {
    }

    public Recycle(long fileId, String filename,  long adduserid,int filetype) {
        this.fileId = fileId;
        this.filename = filename;
        this.adduserid = adduserid;
        this.filetype=filetype;
    }

    public Recycle(long fileId, String filename, long filesize, long adduserid,int filetype) {
        this.fileId = fileId;
        this.filetype=filetype;
        this.filename = filename;
        this.filesize = filesize;
        this.adduserid = adduserid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public java.util.Date getAddtime() {
        return addtime;
    }

    public void setAddtime(java.util.Date addtime) {
        this.addtime = addtime;
    }

    public long getAdduserid() {
        return adduserid;
    }

    public void setAdduserid(long adduserid) {
        this.adduserid = adduserid;
    }
    public long getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }
}

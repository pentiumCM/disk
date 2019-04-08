package com.cloud.disk.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
@Entity
public class Node {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    @Column(length = 100)
    private String md5Val;
    @Column(length = 200)
    private String fileName;
    private long fileSize;
    private long adduserid;
    @CreationTimestamp
    private Date addTime;
    @Column(length = 100)
    private String fileToken;
    @Column(length = 50)
    private String ext;

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    @Column(length = 500)
    private String landmark;
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    @Column(length = 200)
    private String contentType;

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    private int fileType;

    public Node(String md5Val, String fileName,String fileToken, long adduserid,int fileType) {
        this.md5Val = md5Val;
        this.fileName = fileName;
        this.fileToken=fileToken;
        this.adduserid = adduserid;
        this.fileType=fileType;
    }

    public Node() {

    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMd5Val() {
        return md5Val;
    }

    public void setMd5Val(String md5Val) {
        this.md5Val = md5Val;
    }

    public long getAdduserid() {
        return adduserid;
    }

    public void setAdduserid(long adduserid) {
        this.adduserid = adduserid;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileToken() {
        return fileToken;
    }

    public void setFileToken(String fileToken) {
        this.fileToken = fileToken;
    }
}
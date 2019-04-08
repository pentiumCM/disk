package com.cloud.disk.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "file")
@SQLDelete(sql = "update `file` set deleted = 1 where id = ?")
@Where(clause = "deleted = false")
public class FileSaveInfo  extends  BaseEntity
{

    public FileSaveInfo(String md5,String fileName, String ext) {
        super();
        this.md5Val=md5;
        this.fileName=fileName;
        this.ext=ext;
    }


    public FileSaveInfo() {

    }
    @Column(length = 200)
    private String fileName;
    private long fileSize;
    private int folderid;
    @Column(length = 50)
    private String md5Val;
    @Column(length = 50)
    private String fileToken;
    private int fileType;
    @Column(length = 50)
    private String ext;
    @Column(length = 200)
    private String contentType;

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    @Column(length = 500)
    private String landmark;

    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public long getFileSize() {
        return this.fileSize;
    }
    
    public void setFileSize(final long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFolderid() {
        return folderid;
    }

    public void setFolderid(int folderid) {
        this.folderid = folderid;
    }

    public String getMd5Val() {
        return md5Val;
    }

    public void setMd5Val(String md5Val) {
        this.md5Val = md5Val;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileToken() {
        return fileToken;
    }

    public void setFileToken(String fileToken) {
        this.fileToken = fileToken;
    }

    public String getEix() {
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

}

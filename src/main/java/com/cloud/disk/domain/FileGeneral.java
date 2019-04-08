package com.cloud.disk.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
@Entity(name = "file_general")
@SQLDelete(sql = "update `file_general` set deleted = 1 where id = ?")
@Where(clause = "deleted = false")
public class FileGeneral  extends BaseEntity{
    public FileGeneral() {
    }
    private long fileId;
    private String  root;
    private String keyword;
    private double score;

    public FileGeneral(double score,String root, String keyword) {
        this.root = root;
        this.keyword = keyword;
        this.score = score;
    }


    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

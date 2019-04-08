package com.cloud.disk.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name="folder")
@SQLDelete(sql = "update `folder` set deleted = 1 where id = ?")
@Where(clause = "deleted = false")
public class Folder extends BaseEntity
{
    @Column(length = 100)
    private String folderName;
    private long pid;

    public Folder(String folderName,long id) {
        this.folderName = folderName;
        super.setId(id);
    }

    public Folder() {
    }

    public String getFolderName() {
        return this.folderName;
    }
    
    public void setFolderName(final String folderName) {
        this.folderName = folderName;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}

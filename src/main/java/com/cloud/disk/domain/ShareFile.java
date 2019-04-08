package com.cloud.disk.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Date;

@Entity
@SQLDelete(sql = "update `share_file` set deleted = 1 where id = ?")
@Where(clause = "deleted = false")
public class ShareFile extends BaseEntity{
    private   long   nodeId;
    @Column(length = 100)
    private   String   shareFileName;
    @CreationTimestamp
    private Date shareTime;
    private   Date   overtime;
    private   int   viewCount;
    private   int   saveCount;
    private   int   downloadCount;
    @Column(length = 100)
    private   String   shareToken;
    private   boolean   IfSecert;
    @Column(length = 100)
    private   String   extractState;

    public int getOverType() {
        return overType;
    }

    public void setOverType(int overType) {
        this.overType = overType;
    }

    private int overType;

    public ShareFile(long nodeId) {
        this.nodeId = nodeId;
    }

    public ShareFile() {
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getShareFileName() {
        return shareFileName;
    }

    public void setShareFileName(String shareFileName) {
        this.shareFileName = shareFileName;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getOvertime() {
        return overtime;
    }

    public void setOvertime(Date overtime) {
        this.overtime = overtime;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    public boolean isIfSecert() {
        return IfSecert;
    }

    public void setIfSecert(boolean ifSecert) {
        IfSecert = ifSecert;
    }

    public String getExtractState() {
        return extractState;
    }

    public void setExtractState(String extractState) {
        this.extractState = extractState;
    }


}

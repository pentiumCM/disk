/*JPA实体操作数据库*/
package com.cloud.disk.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AdvancedGeneral {

    public AdvancedGeneral() {
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /*基于annotation的hibernate主键标识为@Id*/
    @Id
    private long id;
    private long nodeId;
    private String  root;
    private String keyword;
    private double score;

    public long getId() {
        return id;
    }

    public AdvancedGeneral(double score,String root, String keyword) {
        this.root = root;
        this.keyword = keyword;
        this.score = score;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
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

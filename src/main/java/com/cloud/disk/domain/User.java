package com.cloud.disk.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity(name = "user")
//软删除
@SQLDelete(sql = "update `user` set deleted = 1 where id = ?")
@Where(clause = "deleted = false")
public class User  extends BaseEntity{
    private static final long serialVersionUID = 1L;

    public  static final String Role_Admin="Admin";
    public  static final String Role_User="User";

    @Column(length = 50,nullable = false, unique = true)
    private String loginName;
    private Integer status;
    @Column(length = 50,nullable = false)
    private String role;
    private int sex;
    @Column(length = 50)
    private String pwd;
    @Column(length = 100)
    private String picture;
    @Column(length = 50)
    private String nickName;
    private Date lastLoginTime;

    public User() {
    }

    public User(long id, String loginName, String role) {
       super.setId(id);
        this.loginName=loginName;
        this.role=role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" +super.getId() +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", role='" + role + '\'' +
                ", sex=" + sex +
                ", pwd='" + pwd + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
package com.cloud.disk.dtos;

import javax.persistence.Column;
import java.io.Serializable;

public class EditUserDto  implements Serializable {

    private long id;
    private String loginName;
    private Integer status;
    @Column(length = 50,nullable = false)
    private String role;
    private int sex;
    @Column(length = 50)
    private String nickName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}

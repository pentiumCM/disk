package com.cloud.disk.dtos;

public class ResetPwdUserDto {
    private String pwd;
    private String pwdold;
    private String pwdrepeat;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwdrepeat() {
        return pwdrepeat;
    }

    public void setPwdrepeat(String pwdrepeat) {
        this.pwdrepeat = pwdrepeat;
    }


    public String getPwdold() {
        return pwdold;
    }

    public void setPwdold(String pwdold) {
        this.pwdold = pwdold;
    }
}


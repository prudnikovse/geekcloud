package com.geek.cloud.common;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private long UserId;
    private String UserName;
    private String LoweredUserName;
    private String Password;
    private String PasswordSalt;
    private String Email;
    private boolean IsLockedOut;
    private Date CreateDate;
    private Date LastLoginDate;

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLoweredUserName() {
        return LoweredUserName;
    }

    public void setLoweredUserName(String loweredUserName) {
        LoweredUserName = loweredUserName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPasswordSalt() {
        return PasswordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        PasswordSalt = passwordSalt;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isLockedOut() {
        return IsLockedOut;
    }

    public void setLockedOut(boolean lockedOut) {
        IsLockedOut = lockedOut;
    }

    public Date getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public Date getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        LastLoginDate = lastLoginDate;
    }
}

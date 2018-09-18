package com.geek.cloud.server.bo;

import java.util.Date;

public class User {
    private long UserId;
    private String UserName;
    private String LoweredUserName;
    private Date LastActivityDate;
    private String Password;
    private String PasswordSalt;
    private String Email;
    private boolean IsApproved;
    private boolean IsLockedOut;
    private Date CreateDate;
    private Date LastLoginDate;
    private Date LastLockoutDate;

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

    public Date getLastActivityDate() {
        return LastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        LastActivityDate = lastActivityDate;
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

    public boolean isApproved() {
        return IsApproved;
    }

    public void setApproved(boolean approved) {
        IsApproved = approved;
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

    public Date getLastLockoutDate() {
        return LastLockoutDate;
    }

    public void setLastLockoutDate(Date lastLockoutDate) {
        LastLockoutDate = lastLockoutDate;
    }
}

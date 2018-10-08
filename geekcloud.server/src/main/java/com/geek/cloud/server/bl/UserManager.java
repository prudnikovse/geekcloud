package com.geek.cloud.server.bl;

import java.util.*;

import com.geek.cloud.common.User;
import com.geek.cloud.server.dal.UserRepository;

import com.geek.cloud.common.*;
import com.geek.cloud.common.Helpers.HashHelper;

public class UserManager {
    private static Object _InstanceLock = new Object();
    private static volatile UserManager _Instance;

    private UserManager(){ }

    public static UserManager instance(){
        if(_Instance == null){
            synchronized (_InstanceLock){
                if(_Instance == null){
                    _Instance = new UserManager();
                }
            }
        }
        return _Instance;
    }

    public ResultItem<User> createUser(User source){
        ResultItem<User> res = new ResultItem();

        try(UserRepository db = new UserRepository()) {
            User user = db.getUser(source.getUserName());
            if(user != null)
                throw new Exception("Пользователь с таким именем уже сохранен в системе");

            source.setLoweredUserName(source.getUserName().toLowerCase());
            source.setLockedOut(false);
            source.setPasswordSalt(HashHelper.getHash(source.getPasswordSalt()));
            source.setPassword(HashHelper.createPasswordHash(source.getPassword(), source.getPasswordSalt()));

            res.setSuccess(db.createUser(source));
            if(res.isSuccess())
                res.setData(source);
            else
                res.setMessage("Не удалось создать нового пользователя!");
        }catch (Exception ex){
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    public ResultItem<User> getUser(String userName) {
        ResultItem<User> res = new ResultItem();

        try(UserRepository db = new UserRepository()) {
            User user = db.getUser(userName);
            if(user != null)
                res.setData(user);
            res.setSuccess(true);
        }catch (Exception ex){
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    public boolean validateUser(String userName, String password) {
        boolean isValid = false;
        ResultItem<User> res = getUser(userName);
        if (res.isSuccess() && res.getData() != null) {
            User user = res.getData();
            if (user.isLockedOut() == false && user.getPassword().equals(HashHelper.createPasswordHash(password, user.getPasswordSalt()))) {
                isValid = true;
                user.setLastLoginDate(new Date());

                //userRepository.Update(res);
            }
        }
        return isValid;
    }

    public boolean ChangePassword(String userName, String oldPassword, String newPassword) {
        ResultItem<User> res = getUser(userName);

        if (res.isSuccess() && res.getData() != null) {
            User user = res.getData();
            if (user.getPassword().equals(HashHelper.createPasswordHash(oldPassword, user.getPasswordSalt()))) {
                //user.setPasswordSalt(Crypto.GenerateSalt());
                user.setPassword(HashHelper.createPasswordHash(newPassword, user.getPasswordSalt()));
                return true;
            } else
                return false;
        }
        return false;
    }
}

package com.geek.cloud.server.bl;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.geek.cloud.common.User;
import com.geek.cloud.server.dal.UserRepository;
import  java.security.MessageDigest;

import com.geek.cloud.common.*;

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

    public static String getHash(String source){
        try{
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(source.getBytes());
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<messageDigest.length;i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        }catch(NoSuchAlgorithmException nsae){

        }
        return "";
    }

    public static String createPasswordHash(String pwd, String salt) {
        StringBuilder saltAndPwd = new StringBuilder(pwd + salt);
        String hashedPwd = "";
        for (int i = 0; i < 100; i++)
        {
            hashedPwd = getHash(saltAndPwd.toString());
            saltAndPwd.append(hashedPwd);
        }
        return hashedPwd;
    }

    public ResultItem<User> createUser(User source){
        ResultItem<User> res = new ResultItem();

        try(UserRepository db = new UserRepository()) {
            ResultItem<User> userRes = getUser(source.getUserName());
            if(userRes.isSuccess() && userRes.getData() != null)
                throw new Exception("Пользователь с таким именем уже сохранен в системе");

            source.setLoweredUserName(source.getUserName().toLowerCase());
            source.setLockedOut(false);

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
            res.setData(db.getUser(userName));
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
            if (user.isLockedOut() == false && user.getPassword().equals(createPasswordHash(password, user.getPasswordSalt()))) {
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
            if (user.getPassword().equals(createPasswordHash(oldPassword, user.getPasswordSalt()))) {
                //user.setPasswordSalt(Crypto.GenerateSalt());
                user.setPassword(createPasswordHash(newPassword, user.getPasswordSalt()));
                return true;
            } else
                return false;
        }
        return false;
    }
}

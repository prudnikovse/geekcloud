package com.geek.cloud.server.bl;

import java.util.*;

import com.geek.cloud.server.bo.User;
import com.geek.cloud.server.dal.UserRepository;

public class UserManager {
    private static Object _InstanceLock;
    private static volatile UserManager _Instance;

    private UserManager(){
    }

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

    private static String CreatePasswordHash(String pwd, String salt) {
        StringBuilder saltAndPwd = new StringBuilder(pwd + salt);
        String hashedPwd = "";
        for (int i = 0; i < 100; i++)
        {
            //hashedPwd = Crypto.SHA1(saltAndPwd);
            saltAndPwd.append(hashedPwd);
        }
        return hashedPwd;
    }

    public User CreateUser(String userName, String password, String email) throws Exception{
        User user = GetUser(userName);

        try(UserRepository users = new UserRepository()) {

            if(user != null)
                throw new Exception("Пользователь с таким именем уже сохранен в системе");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return user;
    }

    public User GetUser(String userName) {
        try(UserRepository users = new UserRepository()) {

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public boolean ValidateUser(String userName, String password) {
        boolean isValid = false;
        User res = GetUser(userName);
        if (res != null && res.isApproved()) {
            if (res.getPassword().equals(CreatePasswordHash(password, res.getPasswordSalt()))) {
                isValid = true;
                res.setLastLoginDate(new Date());

                //userRepository.Update(res);
            }
        }
        return isValid;
    }

    public boolean ChangePassword(String userName, String oldPassword, String newPassword) {
        User res = GetUser(userName);

        if (res != null && res.getPassword().equals(CreatePasswordHash(oldPassword, res.getPasswordSalt()))) {
            //res.setPasswordSalt(Crypto.GenerateSalt());
            res.setPassword(CreatePasswordHash(newPassword, res.getPasswordSalt()));
            return true;
        }
        else
            return false;
    }
}

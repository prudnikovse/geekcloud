package com.geek.cloud.common.Helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
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
}

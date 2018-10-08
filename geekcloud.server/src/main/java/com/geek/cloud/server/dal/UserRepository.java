package com.geek.cloud.server.dal;

import com.geek.cloud.common.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class UserRepository implements AutoCloseable {
    private static Connection cn;
    //private static Statement db;

    public UserRepository(){
            connect();
    }

    private void connect() {
        try {
            cn = getConnection();
            //db = cn.createStatement();
            System.out.println ("Database connection established");
        } catch (Exception ex){
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("./geekcloud.server/src/main/resources/database.properties"))) {
            props.load(in);

            String drivers = props.getProperty("jdbc.drivers");
            if (drivers != null) {
                System.setProperty("jdbc.drivers", drivers);
            }
            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String pwd = props.getProperty("jdbc.password");

            return DriverManager.getConnection(url, username, pwd);
        }
    }

    public boolean createUser(User source) throws SQLException{
        String sql = "INSERT INTO usermembership (UserName, LoweredUserName, Password, PasswordSalt, Email, " +
                "IsLockedOut, CreateDate, LastLoginDate) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stat = cn.prepareStatement(sql)){
            stat.setString(1, source.getUserName());
            stat.setString(2, source.getLoweredUserName());
            stat.setString(3, source.getPassword());
            stat.setString(4, source.getPasswordSalt());
            stat.setString(5, source.getEmail());
            stat.setBoolean(6, false);
            stat.setObject(7, new Date());
            stat.setObject(8, new Date());

            return stat.executeUpdate() != 0;
        }
    }

    public User getUser(String userName) throws SQLException{
        User res = null;
        String sql = "SELECT * FROM usermembership WHERE LoweredUserName=?";
        try (PreparedStatement stat = cn.prepareStatement(sql)){
            stat.setString(1, userName.toLowerCase());

            try(ResultSet rs = stat.executeQuery()){
                if(rs != null){
                    while (rs.next()){
                        res = new User();
                        res.setUserName(rs.getString(2));
                        res.setLoweredUserName(rs.getString(3));
                        res.setPassword(rs.getString(4));
                        res.setPasswordSalt(rs.getString(5));
                        res.setEmail(rs.getString(6));
                        res.setLockedOut(rs.getBoolean(7));
                        res.setCreateDate(rs.getDate(8));
                        res.setLastLoginDate(rs.getDate(9));
                    }
                }
            }
        }
        return res;
    }

    @Override
    public void close() throws SQLException {
        //db.close();
        cn.close();
    }
}

package com.geek.cloud.server.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UserRepository implements AutoCloseable {
    private static Connection cn;
    private static Statement db;

    public UserRepository(){
        try{
            Class.forName("com.mysql.JDBC.Driver");
            connect();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    private void connect() {
        String userName = "root";
        String password = "";
        String url = "jdbc:mysql://localhost/geekcloud";

        try {
            cn = DriverManager.getConnection (url, userName, password);
            db = cn.createStatement();
            System.out.println ("Database connection established");
        } catch (Exception ex){
            System.err.println ("Cannot connect to database server");
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        db.close();
        cn.close();
    }
}

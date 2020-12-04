package com.haulmont.testtask.util;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    private static Connection connection;
    private static final Properties property = new Properties();

    static {
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);
            String url = property.getProperty("db.url");
            String user = property.getProperty("db.user");
            String password = property.getProperty("db.password");

            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            connection = DriverManager.getConnection(url, user, password);
            if(connection != null){
                System.out.println("Connection created successfully");

                ScriptRunner sr = new ScriptRunner(connection);
                Reader reader = new BufferedReader(new FileReader("src/main/resources/scripts/create_tables.sql"));
                sr.runScript(reader);
                if (property.getProperty("data.initialized").equals("false")){
                    reader = new BufferedReader(new FileReader("src/main/resources/scripts/insert_data.sql"));
                    sr.runScript(reader);

                    fos = new FileOutputStream("src/main/resources/config.properties");
                    property.put("data.initialized", "true");
                    property.store(fos, "Properties");
                }
            } else {
                System.out.println("Problem with creating connection");
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static Connection getInstance(){
        return connection;
    }
}

package jsoupParser.cookies;

import com.sun.jna.platform.win32.Crypt32Util;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLiteConnector {

    private static Logger logger= Logger.getLogger(SQLiteConnector.class.getName());
    private static Connection connection;

    public static Connection getConnection() throws ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");

        String userHome = System.getProperty("user.home");
        String cookiesPath = userHome+"\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Cookies";
         // path to Google Chrome cookies file.

        try {

            connection = DriverManager.getConnection("jdbc:sqlite:"+cookiesPath);


        } catch (SQLException ex) {
            logger.log(Level.SEVERE,"Database file with Google Chrome cookies was not found",ex);
            throw new ClassNotFoundException("Database file with Google Chrome cookies was not found");
        }

        return connection;
    }


    public static Map<String,String> getCookies (String sqlQuery)throws SQLException,ClassNotFoundException{
        connection = SQLiteConnector.getConnection();
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(2);

        ResultSet cookiesSet =  statement.executeQuery(sqlQuery);

       // connection.close();
        return getCookiesMap(cookiesSet);

    }

    private static Map<String,String> getCookiesMap(ResultSet cookiesSet)throws SQLException{


        Map<String,String> cookies = Collections.synchronizedMap(new HashMap<>());

        String cookieName;
        String cookiesValue;

        while (cookiesSet.next()){
            cookieName = cookiesSet.getString("name");
            cookiesValue = decryptValue(cookiesSet.getBytes("encrypted_value"));
            cookies.put(cookieName,cookiesValue);
        }


        return cookies;
    }

    private static String decryptValue(byte[] encryptedCookieValue){

        byte[] decryptedCookieValue = Crypt32Util.cryptUnprotectData(encryptedCookieValue);

        return new String(decryptedCookieValue);
    }


}

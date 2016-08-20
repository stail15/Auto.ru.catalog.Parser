package jsoupParser.cookies;


import java.beans.Statement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class CookiesImpl implements Cookies{

    private static Logger logger = Logger.getLogger(CookiesImpl.class.getName());
    private static Map<String,String> cookies;
    private String hostName;
    /**
     * Milliseconds from 01 January 1601 till "Epoch";
     */
    private final static long googleTime = 11641806000000L;


    public CookiesImpl(String hostName){
        this.hostName = hostName;
    }


    @Override
    public Map<String,String > getCookies(){
        if(cookies==null && hostName!=null){
           String sqlQuery = "auto.ru";

           cookies = CookiesImpl.getCookiesFromDB(sqlQuery);
        }

        return cookies;
    }

    @Override
    public void setCookies(Map<String,String> cookiesToSet) {

        cookies = cookiesToSet.entrySet().stream()
                                         .collect(Collectors.toMap(
                                                 Map.Entry::getKey,
                                                 Map.Entry::getValue)
                                         );

    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    private static Map<String,String > getCookiesFromDB(String hostName) {

        Map<String,String>cookies = null;

        String sqlQuery = "SELECT * FROM cookies WHERE host_key LIKE \"%"+hostName+"%\"";

        try {
            cookies =SQLiteConnector.getCookies(sqlQuery);
        }
        catch (SQLException | ClassNotFoundException ex){
            logger.log(Level.WARNING,"Failed to fetch cookies from Google cookies store",ex);
        }

        return cookies;
    }
}

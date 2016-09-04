package jsoupParser.cookies;

import java.util.Map;


public interface Cookies {
    String getHostName();
    void setHostName(String hostName);
    Map<String,String > getCookies();
    void setCookies(Map<String, String> cookiesToSet);
    void addNewCookies(Map<String, String> cookiesToAdd);

}

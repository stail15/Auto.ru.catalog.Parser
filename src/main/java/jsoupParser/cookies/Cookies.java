package jsoupParser.cookies;

import java.util.Map;

/**
 * Created by stail on 19.08.2016.
 */
public interface Cookies {
    public String getHostName();
    public void setHostName(String hostName);
    public Map<String,String > getCookies();
    public void setCookies(Map<String,String> cookiesToSet);
    public void addNewCookies(Map<String,String> cookiesToAdd);

}

package jsoupParser.service;

import jsoupParser.cookies.Cookies;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by stail on 19.08.2016.
 */
public class ConnectionService {

    private static Logger logger = Logger.getLogger(ConnectionService.class.getName());
    private Document htmlPage;
    private transient Cookies cookies;

    public ConnectionService(Cookies cookies){
        this.cookies = cookies;
    }

    public Document getDocument(String url){

        Connection connection = Jsoup.connect(url);

        connection.cookies(cookies.getCookies())
                  .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                  .header("Accept", "text/html,application/xhtml+ xml, application/xml;q=0.9,image/webp,*/*;q=0.8")
                  .header("Accept-Encoding", "gzip, deflate, sdch, br")
                  .header("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
                  .header("Cache-Control","no-cache")
                  .header("Connection","keep-alive")
                  .header("DNT","1")
                  .header("Host", "auto.ru")
                  .header("Pragma","no-cache")
                  .header("Upgrade-Insecure-Requests","1")
                .followRedirects(true);

        Connection.Response response;
        try {

            response=connection.execute();

            cookies.setCookies(response.cookies());

            htmlPage = response.parse();

            Connection.Request request= connection.request();
            request.headers().forEach((k,v)->logger.info(k+" : "+v));

            //logger.info(htmlPage.toString());
        }
        catch (IOException ex){logger.warning("Error occurred while parsing "+url);}




        return htmlPage;
    }

}

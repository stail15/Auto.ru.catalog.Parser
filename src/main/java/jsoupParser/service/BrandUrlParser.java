package jsoupParser.service;

import jsoupParser.Main;
import jsoupParser.cookies.Cookies;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class BrandUrlParser implements Runnable {

    private static Logger logger = Logger.getLogger(BrandUrlParser.class.getName());
    private Element carBrandLink;
    private org.w3c.dom.Element brandElement;
    private int errorCount=0;
    private String modelCarSeparator;
    private org.w3c.dom.Document resultDocument;
    private Cookies cookies;


    public BrandUrlParser(Cookies cookies,org.w3c.dom.Element brandElement,String modelCarSeparator, org.w3c.dom.Document resultDocument){
        this.modelCarSeparator= modelCarSeparator;
        this.resultDocument = resultDocument;
        this.brandElement = brandElement;
        this.cookies = cookies;
    }

    public Element getElement() {
        return carBrandLink;
    }

    public void setElement(Element element) {
        this.carBrandLink = element;
    }


    @Override
    public void run() {
        List<String> allModelsUrl = new ArrayList<>();

        String brandUrl = brandElement.getAttribute("href");
        ConnectionService connectionService = new ConnectionService(cookies);
        Document carBrandDoc = null;

        while (carBrandDoc!=null && errorCount<10){
            carBrandDoc = connectionService.getDocument(brandUrl);
            errorCount++;
            if(errorCount==10){
                logger.warning("Failed to parse "+brandUrl);
            }
        }


        Elements modelHtml = carBrandDoc.select(modelCarSeparator);

        modelHtml.forEach(element -> getModelsURL(element));
//        for(Element el : modelHtml){
//            String modelUrl = el.select("a").first().attr("abs:href");
//            allModelsUrl.add(modelUrl);
//        }

        Main.modelsUrl.addAll(allModelsUrl);

    }

    private void getModelsURL(Element element){

    }
}

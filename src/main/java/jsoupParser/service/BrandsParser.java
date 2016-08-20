package jsoupParser.service;

import jsoupParser.cookies.Cookies;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.logging.Logger;

/**
 * Created by stail on 19.08.2016.
 */
public class BrandsParser {

    private static Logger logger = Logger.getLogger(BrandsParser.class.getName());
    private static Cookies cookies;
    private static org.w3c.dom.Document resultDocument;

    public BrandsParser(Cookies cookies, org.w3c.dom.Document resultDocument){
        this.cookies = cookies;
        this.resultDocument = resultDocument;
    }

    public void parseAllBrands(String url, String brandSeparator){
        logger.info("Parsing "+url+" ...");
        ConnectionService connectionService = new ConnectionService(cookies);
        Document document = connectionService.getDocument(url);
        Elements brandList = document.select(brandSeparator);

        if(brandList.size()>0){
            logger.info(url + "was parsed - " + brandList.size() + " car brands were found");
            // modelList.forEach(element -> System.out.println(element));
            brandList.forEach(element -> getBrand(element));
        }
        else {
            logger.warning(url+"was not parsed");
        }



    }

    private static void getBrand(Element brandElement){

        org.w3c.dom.Element rootElement = resultDocument.getDocumentElement();

        String catalogUrl = rootElement.getAttribute("href");
        String carBrandUrl = catalogUrl+brandElement.attr("href").split("/")[2]+"/";
        String carBrand = brandElement.ownText();

        org.w3c.dom.Element brand = resultDocument.createElement("brand");
        brand.setAttribute("name",carBrand);
        brand.setAttribute("href", carBrandUrl);
        rootElement.appendChild(brand);

    }
}

package jsoupParser.service;

import jsoupParser.cookies.Cookies;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stail on 19.08.2016.
 */
public class ModelsParser implements Runnable{

    private static Logger logger = Logger.getLogger(BrandsParser.class.getName());
    private Map<String,String> models = Collections.synchronizedMap(new HashMap<String, String>());
    private static Cookies cookies;
    private static org.w3c.dom.Document resultDocument;
    private static ArrayList<Thread> threadList;
    private static String modelSeparator;
    private Element brandElement;

    public ModelsParser(Element brandElement){
        this.brandElement = brandElement;
    }
    public ModelsParser(Cookies cookies, org.w3c.dom.Document resultDocument,String modelSeparator){
        this.cookies = cookies;
        this.resultDocument = resultDocument;
        this.modelSeparator = modelSeparator;
    }

    @Override
    public void run() {

        String brandUrl = brandElement.getAttribute("href");


        logger.info("Parsing "+brandUrl+" ...");

        ConnectionService connectionService = new ConnectionService(cookies);
        Document document = connectionService.getDocument(brandUrl);
        Elements modelList = document.select(modelSeparator);

        if(modelList.size()>0){

            modelList.forEach(element -> getModel(element));

            logger.info(brandUrl + " was parsed - " + models.size() + " car models were found");
        }
        else {
            logger.warning(brandUrl+" was not parsed");
        }

    }

    public void parseAllModels(){


        threadList = new ArrayList<>();
        NodeList brandList = resultDocument.getElementsByTagName("brand");

        int length = brandList.getLength();
        if(brandList!=null && length>0){
            for( int i = 0; i<length; i++){
                Element brandElement = (Element)brandList.item(i);
                Thread thread = new Thread(new ModelsParser(brandElement));
                threadList.add(thread);


            }
        }

        if(!threadList.isEmpty()){
           // threadList.forEach(thread -> thread.run());
           threadList.forEach(thread -> ModelsParser.startThread(thread));
           threadList.forEach(thread -> ModelsParser.joinThread(thread));
        }

    }

    private static void joinThread(Thread thread){
        try {
            thread.join();
        } catch (InterruptedException ex){
            logger.log(Level.SEVERE,"Exception while parsing.",ex);
        }
    }

    private static void startThread(Thread thread){
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex){
            logger.warning("Failed to delay Thread start");
        }
        thread.start();
    }

    private void getModel(org.jsoup.nodes.Element modelElement){

        String catalogUrl = resultDocument.getDocumentElement().getAttribute("href");
        String carModelUrl = catalogUrl+modelElement.attr("href").split("/")[2]+"/";
        String carModel = modelElement.ownText();

        if(!this.models.containsKey(carModel)){
            org.w3c.dom.Element brand = resultDocument.createElement("model");
            brand.setAttribute("name",carModel);
            brand.setAttribute("href",carModelUrl);
            this.brandElement.appendChild(brand);
            
            this.models.put(carModel,carModelUrl);
        }

    }

}

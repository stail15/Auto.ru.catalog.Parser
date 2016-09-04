package jsoupParser.dep;

import jsoupParser.cookies.Cookies;
import jsoupParser.service.ConnectionService;
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


public class GenerationParser implements Runnable {

    private static Logger logger = Logger.getLogger(BrandsParser.class.getName());
    private Map<String,String> generations = Collections.synchronizedMap(new HashMap<String, String>());
    private static volatile Cookies cookies;
    private static org.w3c.dom.Document resultDocument;
    private static ArrayList<Thread> threadList;
    private static String carSeparator;
    private Element generationElement;

    public GenerationParser(Element generationElement){
        this.generationElement = generationElement;
    }
    public GenerationParser(Cookies cookies, org.w3c.dom.Document resultDocument, String carSeparator){
        this.cookies = cookies;
        this.resultDocument = resultDocument;
        this.carSeparator = carSeparator;
    }


    @Override
    public void run() {

        String modelUrl = generationElement.getAttribute("href");


        logger.info("Parsing "+modelUrl+" ...");

        ConnectionService connectionService = new ConnectionService(cookies);
        Document document = connectionService.getDocument(modelUrl);
        Elements modelList = document.select(carSeparator);

        if(modelList.size()>0){

            modelList.forEach(element -> getGeneration(element));

            logger.info(modelUrl + " was parsed - " + generations.size() + " car generations were found");
        }
        else {
            logger.warning(modelUrl+" was not parsed");
        }

    }

    public void parseAllGenerations(){


        threadList = new ArrayList<>();
        NodeList modelList = resultDocument.getElementsByTagName("model");

        int length = modelList.getLength();
        if(modelList!=null && length>0){
            for( int i = 0; i<length; i++){

                Element modelElement = (Element)modelList.item(i);
                Thread thread = new Thread(new GenerationParser(modelElement));
                threadList.add(thread);


            }
        }

        if(!threadList.isEmpty()){
            // threadList.forEach(thread -> thread.run());
            threadList.forEach(thread -> GenerationParser.startThread(thread));
            threadList.forEach(thread -> GenerationParser.joinThread(thread));
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


    private void getGeneration(org.jsoup.nodes.Element modelElement){

        String rootUrl = resultDocument.getDocumentElement().getAttribute("href").replace("/catalog/","");
        String carGenerationUrl = rootUrl+modelElement.attr("href");
        String carGenYears = modelElement.select("div.search-form-v2-list__card-title").first().ownText();
        org.jsoup.nodes.Element element = modelElement.select("div.search-form-v2-list__card-text").first();

        String carGenName = null;
       if(element!=null){
            carGenName =element.ownText();
        }


        if(!this.generations.containsKey(carGenYears)){
            org.w3c.dom.Element generation = resultDocument.createElement("generation");
            generation.setAttribute("years", carGenYears);
            generation.setAttribute("href", carGenerationUrl);
            generation.setAttribute("name", carGenName);
            this.generationElement.appendChild(generation);

            this.generations.put(carGenYears, carGenerationUrl);
        }

    }

}

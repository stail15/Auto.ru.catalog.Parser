package jsoupParser.parsers;


import jsoupParser.service.ConnectionService;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Parser implements Runnable{

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private final Set<String> models = Collections.synchronizedSet(new HashSet<>());
    private String parentNodeName;

    private static String nodeName;
    private static String separator;
    private static String carYearsSeparator;
    private static String carGenerationNameSeparator;
    private static org.w3c.dom.Document resultDocument;
    private Element element;



    private Parser(Element element, String parentNodeName){
        this.element = element;
        this.parentNodeName = parentNodeName;
    }

    public Parser(String separator){
        Parser.separator = separator;
    }



    @Override
    public void run() {

        String url = element.getAttribute("href");

        logger.info("Parsing "+url+" ...");

        ConnectionService connectionService = new ConnectionService();
        Document document = connectionService.getDocument(url);
        Elements allElements = document.select(separator);

        if(allElements.size()>0){

            allElements.forEach(this::getSubElement);

            logger.info(url + " was parsed - " + models.size() + " elements were found");
        }
        else {
            logger.warning(url+" was not parsed");
        }

    }


    public void parse(String tagName){


        NodeList modelList = resultDocument.getElementsByTagName(tagName);

        int length = modelList.getLength();
        if(length > 0){

            ArrayList<Thread> threadList = new ArrayList<>();

            for( int i = 0; i<length; i++){

                Element element = (Element)modelList.item(i);
                Thread thread = new Thread(new Parser(element,tagName));
                threadList.add(thread);

            }

            if(!threadList.isEmpty()){
                threadList.forEach(Parser::startThread);
                threadList.forEach(Parser::joinThread);
            }
        }

    }


    private void getSubElement(org.jsoup.nodes.Element element){
        String rootUrl = resultDocument.getDocumentElement().getAttribute("href").replace("/catalog/","");
        String elementUrl = rootUrl+ element.attr("href");
        String elementName = this.getElementName(element);

        this.appendToResultDoc(element, elementUrl, elementName);

    }

    private  void appendToResultDoc(org.jsoup.nodes.Element element,String elementUrl, String elementName){

        if(!this.models.contains(elementName)){

            org.w3c.dom.Element parentElement = this.element;
            org.w3c.dom.Element childElement = resultDocument.createElement(nodeName);
            childElement.setAttribute("name", elementName);
            childElement.setAttribute("href", elementUrl);

            if(nodeName !=null && nodeName.equals("generation")){
                String carYears = element.select(carYearsSeparator).first().ownText();
                childElement.setAttribute("years", carYears);
            }

            parentElement.appendChild(childElement);
            this.models.add(elementName);
        }
    }


    private String getElementName(org.jsoup.nodes.Element element){

        String elementName = null;

        if(nodeName.equals("generation")) {

            org.jsoup.nodes.Element childElement = element.select(carGenerationNameSeparator).first();

            if (childElement != null) {
                elementName = childElement.ownText();
            }
        }else {
            elementName = element.ownText();
        }

        return elementName;
    }


    private static void joinThread(Thread thread){
        try {
            thread.join();
        } catch (InterruptedException ex){
            logger.log(Level.SEVERE, "Exception while parsing.", ex);
        }
    }

    private static void startThread(Thread thread){
        try {
            Thread.sleep(0);
        }
        catch (InterruptedException ex){
            logger.warning("Failed to delay Thread start");
        }
        thread.start();
    }


    public static void setResultDocument(org.w3c.dom.Document resultDocument) {
        Parser.resultDocument = resultDocument;
    }


    public static void setNodeName(String nodeName) {
        Parser.nodeName = nodeName;
    }

    public static void setCarGenerationNameSeparator(String carGenerationNameSeparator) {
        Parser.carGenerationNameSeparator = carGenerationNameSeparator;
    }

    public static void setCarYearsSeparator(String carYearsSeparator) {
        Parser.carYearsSeparator = carYearsSeparator;
    }
}

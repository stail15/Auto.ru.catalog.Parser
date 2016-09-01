package jsoupParser;

import jsoupParser.config.Context;
import jsoupParser.cookies.Cookies;
import jsoupParser.cookies.CookiesImpl;
import jsoupParser.pojo.Car;
import jsoupParser.service.*;
import jsoupParser.parsers.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Context.class.getName());
    public static List<String> modelsUrl;
    public static UrlService urlService;
    private static Set<Car> carList;
    private volatile static Cookies cookies;
    private static ArrayList<Thread> brandThreadsList;
    private static ArrayList<Thread> modelThreadsList;
    private static org.w3c.dom.Document resultDocument;


    public static void main(String[] args) throws Exception{

        Main.configLogger("/logging.properties");

        urlService = Main.getUrlService("/url-config.xml");

        cookies = new CookiesImpl(urlService.getCookiesHostName());
        modelsUrl = Collections.synchronizedList(new ArrayList<>());
        carList = Collections.synchronizedSet(new TreeSet<>((o1, o2) -> o1.toString().compareTo(o2.toString())));
        brandThreadsList = new ArrayList<>();
        modelThreadsList = new ArrayList<>();

        args = new String[]{"D:\\TestTask\\autoruParser\\resultXml.xml"};
        if(!(args!=null && args.length>0)) {
            resultDocument = Main.createResultDocument();


            String mainUrl = urlService.getMainUrl();
            String brandSeparator = urlService.getBrandClassSeparator();
            String modelSeparator = urlService.getModelClassSeparator();

            logger.info("Parsing car brands...");
            BrandsParser brandsParser = new BrandsParser(cookies, resultDocument);
            brandsParser.parseAllBrands(mainUrl, brandSeparator);
            logger.info("Car brands was parsed.");

            logger.info("Parsing car models...");
            ModelsParser modelsParser = new ModelsParser(cookies, resultDocument, modelSeparator);
            modelsParser.parseAllModels();
            logger.info("Car models was parsed.");
        }
        else {
            loadSourceFile(args[0]);

            String carsSeparator = urlService.getCarClassSeparator();
            GenerationParser generationParser = new GenerationParser(cookies,resultDocument,carsSeparator);
            generationParser.parseAllGenerations();




        }

//        NodeList nodeList = resultDocument.getElementsByTagName("model");
//        for(int i=0;i<nodeList.getLength();i++){
//           System.out.println(((org.w3c.dom.Element) nodeList.item(i)).getAttribute("href"));
//        }

        saveResultToFile("D:\\TestTask\\autoruParser\\fullResultXml.xml");


        System.exit(0);

        System.out.println("Final size is " + modelsUrl.size());


    }

    /**
     * Returns {@code true} if {@link java.util.logging.LogManager LogManager} was successfully configured; returns false otherwise.<br>
     * Configures LogManager object
     * in accordance with file "logging.properties" in classpath.
     *
     * @param propertiesFile the string representation of the relative path to the {@code *.properties} file
     *                                     to configure {@link java.util.logging.LogManager LogManager}.
     * @return boolean {@code true} if LogManager was successfully configured; returns {@code false} otherwise.
     */
    public static boolean configLogger(String propertiesFile){
        boolean configured = false;
        try {

            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream(propertiesFile));
            configured = true;

        } catch (NullPointerException ex) {
            System.err.println(": Failed to configure LogManager - file \"logging.properties\" does not exist:");
            ex.printStackTrace();
        } catch (IOException ex){
            System.err.println(": LogManager failed to read properties file \"logging.properties\":");
            ex.printStackTrace();
        } catch (SecurityException ex){
            System.err.println(": LogManager does not have LoggingPermission(\"control\"):");
            ex.printStackTrace();
        }

        logger.info("Logger was configured");
        return configured;
    }


    private static org.w3c.dom.Document createResultDocument(){
        org.w3c.dom.Document resultDocument = null;
        logger.info("Preparing result document:");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;


        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex){
            logger.log(Level.SEVERE, " - the result document wasn't created:");
            logger.log(Level.SEVERE, " - the application execution is stopped because of the ParserConfigurationException", ex);
            return null;
        }
        if(documentBuilder!=null){
            resultDocument = documentBuilder.newDocument();
            org.w3c.dom.Element rootElement = resultDocument.createElement("cars");
            rootElement.setAttribute("href","https://auto.ru/catalog/");
            resultDocument.appendChild(rootElement);
        }

        logger.info("- result document was prepared");

            return resultDocument;
    }


    private static UrlService getUrlService(String filepath) throws Exception{
        Context context = new Context();
        context.loadConfiguration(filepath);
        UrlService urlService = (UrlService)context.loadBean(UrlService.class);

        return urlService;
    }


    protected static boolean saveResultToFile(String fileDirectory){
        boolean saved = false;

        File resultXml = new File(fileDirectory);


        TransformerFactory factory = TransformerFactory.newInstance();
        DOMSource xmlSource = new DOMSource(resultDocument);
        Transformer xmlTransformer;

        try {
            xmlTransformer= factory.newTransformer();
            Result resultToFile = new StreamResult(resultXml);
            xmlTransformer.transform(xmlSource,resultToFile);
            saved = true;
        }
        catch (Exception ex){
            logger.log(Level.WARNING, "- the result document wasn't saved to file "+resultXml.getAbsolutePath()+" because of the "+ex.getClass().getName(),ex);
        }


        return saved;
    }

    public static void loadSourceFile(String fileName) throws SAXException, IOException,ParserConfigurationException{
        File sourceXML;
        try {
            sourceXML = new File(fileName);
    ;
        }
        catch (NullPointerException ex){
            String exceptionMsg = "Configuration file \"" + fileName + "\" was not found";
            logger.log(Level.SEVERE, exceptionMsg);
            throw new FileNotFoundException(exceptionMsg);
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        resultDocument= documentBuilder.parse(sourceXML);

    }

}

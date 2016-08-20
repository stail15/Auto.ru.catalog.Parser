package jsoupParser;

import jsoupParser.config.Context;
import jsoupParser.cookies.Cookies;
import jsoupParser.cookies.CookiesImpl;
import jsoupParser.pojo.Car;
import jsoupParser.service.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Context.class.getName());
    public static List<String> modelsUrl;
    public static UrlService urlService;
    private static Set<Car> carList;
    private transient static Cookies cookies;
    private static ArrayList<Thread> brandThreadsList;
    private static ArrayList<Thread> modelThreadsList;
    private static org.w3c.dom.Document resultDocument;


    public static void main(String[] args) throws Exception{

        Main.configLogger("/logging.properties");

        urlService = Main.getUrlService("/url-config.xml");
        resultDocument = Main.createResultDocument();
        cookies = new CookiesImpl(urlService.getCookiesHostName());
        modelsUrl = Collections.synchronizedList(new ArrayList<>());
        carList = Collections.synchronizedSet(new TreeSet<>((o1, o2) -> o1.toString().compareTo(o2.toString())));
        brandThreadsList = new ArrayList<>();
        modelThreadsList = new ArrayList<>();


        String mainUrl = urlService.getMainUrl();
        String brandSeparator = urlService.getBrandClassSeparator();
        String modelSeparator = urlService.getModelClassSeparator();

        logger.info("Parsing car brands...");
        BrandsParser brandsParser = new BrandsParser(cookies,resultDocument);
        brandsParser.parseAllBrands(mainUrl, brandSeparator);
        logger.info("Car brands was parsed.");

        logger.info("Parsing car models...");
        ModelsParser modelsParser = new ModelsParser(cookies,resultDocument,modelSeparator);
        modelsParser.parseAllModels();
        logger.info("Car models was parsed.");

        NodeList nodeList = resultDocument.getElementsByTagName("model");
        for(int i=0;i<nodeList.getLength();i++){
           System.out.println(((org.w3c.dom.Element) nodeList.item(i)).getAttribute("href"));
        }

        System.exit(0);

        System.out.println("Final size is " + modelsUrl.size());


        for(String link : modelsUrl){ // looking for car model information & creating a new Car
            ModelUrlParser modelUrlParser = new ModelUrlParser();
            modelUrlParser.setModelUrl(link);
            Thread modelThread = new Thread(modelUrlParser);
            modelThread.start();
            modelThreadsList.add(modelThread);

            try {
                Thread.currentThread().sleep(50);
            }
            catch (InterruptedException ex){ex.getMessage();}
        }

        for(Thread th :modelThreadsList){ // the main thread is waiting for other threads
            try {
                th.join();
            }
            catch (InterruptedException ex){ex.getMessage();}
        }
        System.out.println("Final size of carList is " + carList.size());
        writeToFile(carList);
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

    private static void writeToFile(Set<Car> carList){

        String fileDirectory = "D:\\Auto.ru_16.02";
        File path = new File(fileDirectory);
        File file = new File(fileDirectory+"\\CarModelsDate.txt");
        BufferedWriter fileWriter=null;

        //noinspection ResultOfMethodCallIgnored
        path.mkdirs();
        try {
            fileWriter = new BufferedWriter(new FileWriter(file));
        }
        catch (IOException ex){System.out.println("Unable to write file");}

        for(Car car :carList){
           try {
               fileWriter.write(car.toString());
           }
           catch (IOException ex){ex.getMessage();}

        }
        try {
            assert fileWriter != null;
            fileWriter.close();
        }
        catch (IOException ex){ex.getMessage();}

    }

}

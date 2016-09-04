package jsoupParser;

import jsoupParser.config.Context;
import jsoupParser.cookies.Cookies;
import jsoupParser.cookies.CookiesImpl;
import jsoupParser.service.*;
import jsoupParser.parsers.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main {

    private static Logger logger = Logger.getLogger(Context.class.getName());

    private static org.w3c.dom.Document resultDocument;


    public static void main(String[] args) throws Exception{

        Main.configLogger("/logging.properties");

        UrlService urlService = Main.getUrlService("/url-config.xml");

        String mainURL = urlService.getMainUrl();
        String urlForCookies = urlService.getCookiesHostName();
        String brandSeparator = urlService.getBrandSeparator();
        String modelSeparator = urlService.getModelSeparator();
        String generationSeparator = urlService.getGenerationSeparator();
        String carYearsSeparator = urlService.getCarYearsSeparator();
        String carGenerationNameSeparator = urlService.getCarGenerationNameSeparator();


        Cookies cookies = new CookiesImpl(urlForCookies);
        resultDocument = Main.createResultDocument(mainURL);

//        args = new String[]{"D:\\TestTask\\autoruParser\\fullResultXml.xml"};
//        if(!(args!=null && args.length>0)) {
//            resultDocument = Main.createResultDocument();
//
//
//            String mainUrl = urlService.getMainUrl();
//            String brandSeparator = urlService.getBrandSeparator();
//            String modelSeparator = urlService.getModelSeparator();
//            String carsSeparator = urlService.getGenerationSeparator();
//
//            logger.info("Parsing car brands...");
//            BrandsParser brandsParser = new BrandsParser(cookies, resultDocument);
//            brandsParser.parseAllBrands(mainUrl, brandSeparator);
//            logger.info("Car brands was parsed.");
//
//            logger.info("Parsing car models...");
//            ModelsParser modelsParser = new ModelsParser(cookies, resultDocument, modelSeparator);
//            modelsParser.parseAllModels();
//            logger.info("Car models was parsed.");
//
//            logger.info("Parsing car generations...");
//            GenerationParser generationParser = new GenerationParser(cookies,resultDocument,carsSeparator);
//            generationParser.parseAllGenerations();
//            logger.info("Parsing car generations...");
//        }
//        else {
//            loadSourceFile(args[0]);
//
//
//            ResTransformer resTransformer = new ResTransformer(resultDocument);
//            resTransformer.processCarsRestyling("brand");
//            resTransformer.saveResultToFile("D:\\TestTask\\autoruParser\\fullResultXml.xml");
//
//
//            File resultHTML = resTransformer.produceHTML();
//
//
//            try {
//                if (resultHTML!=null){
//                    Desktop.getDesktop().browse(resultHTML.toURI());
//                }
//            }
//            catch (UnsupportedOperationException | IOException ex){
//                logger.log(Level.WARNING,
//                        " - the result file can not be displayed in HTML because of the "
//                        + ex.getMessage()
//                        + ":", ex);
//            }
//
//            logger.info(" - the result XML file was successfully displayed in HTML.");
//
//        }

        ConnectionService.setCookies(cookies);
        Parser.setResultDocument(resultDocument);

        Parser brandParser = new Parser(brandSeparator);
        Parser.setNodeName("brand");
        brandParser.parse("cars");

        Parser modelParser = new Parser(modelSeparator);
        Parser.setNodeName("model");
        modelParser.parse("brand");

        Parser generationParser = new Parser(generationSeparator);
        Parser.setCarGenerationNameSeparator(carGenerationNameSeparator);
        Parser.setCarYearsSeparator(carYearsSeparator);
        Parser.setNodeName("generation");
        generationParser.parse("model");


        ResTransformer resTransformer = new ResTransformer(resultDocument);
        resTransformer.aggregateCarsRestyling();
        resTransformer.saveResultToFile("D:\\TestTask\\autoruParser\\fullResultXml.xml");

        File resultHTML = resTransformer.produceHTML();
        Main.showOnDisplay(resultHTML);




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
    private static boolean configLogger(String propertiesFile){
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


    private static org.w3c.dom.Document createResultDocument(String mainURL) throws ParserConfigurationException{
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
            throw ex;
        }

        if(documentBuilder!=null){
            resultDocument = documentBuilder.newDocument();
            org.w3c.dom.Element rootElement = resultDocument.createElement("cars");
            rootElement.setAttribute("href",mainURL);
            resultDocument.appendChild(rootElement);
        }

        logger.info("- result document was prepared");

            return resultDocument;
    }


    private static UrlService getUrlService(String filepath) throws Exception{
        Context context = new Context();
        context.loadConfiguration(filepath);

        return (UrlService)context.loadBean(UrlService.class);
    }




    public static void loadSourceFile(String fileName) throws SAXException, IOException,ParserConfigurationException{
        File sourceXML;
        try {
            sourceXML = new File(fileName);
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


    public static void showOnDisplay(File file){
        try {
            if (file!=null){
                Desktop.getDesktop().browse(file.toURI());
            }
        }
        catch (UnsupportedOperationException | IOException ex){
            logger.log(Level.WARNING,
                    " - the result file can not be displayed in HTML because of the " + ex.getMessage()
                            + ":", ex);
        }

        logger.info(" - the result XML file was successfully displayed in HTML.");
    }
}

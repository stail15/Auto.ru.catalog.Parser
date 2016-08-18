package jsoupParser.configReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads configuration from XML file and produces bean objects according to it.
 */
public class XMLReader {

    /**
     * {@code Logger} object that logs the program execution process.
     */
    private static final Logger logger = Logger.getLogger(XMLReader.class.getName());

    /**
     * {@code DocumentBuilder} for creating {@link Document} object;
     */
    private  static DocumentBuilder documentBuilder;

    /**
     * {@code Document} object for storing configuration file.
     */
    private Document document;

    /**
     * Initializes a newly created {@link XMLReader} object.
     * Initializes static{@code documentBuilderFactory} and {@code documentBuilder} fields of {@link XMLReader} class.
     *
     * @throws ParserConfigurationException in case of error occurred while creating {@link DocumentBuilder} object.
     */
    public XMLReader()throws ParserConfigurationException{

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    /**
     * Returns {@code true} if configuration file was loaded, {@code false} otherwise.
     * Loads configuration file from string {@code path} specified in input parameter.
     *
     * @param fileName {@code path} to configuration file.
     * @return {@code true} if configuration file was loaded, {@code false} otherwise.
     *
     * @throws SAXException in case of error while parsing configuration file.
     * @throws IOException in case of error while loading or reading configuration file.
     */
    public boolean loadConfiguration(String fileName) throws SAXException, IOException{
        File configXML;
        try {
           configXML = new File(XMLReader.class.getResource(fileName).getFile());
        }
        catch (NullPointerException ex){
            String exceptionMsg = "Configuration file " + fileName + " was not found";
            logger.log(Level.SEVERE,exceptionMsg);
            throw new FileNotFoundException(exceptionMsg);
        }

        document= documentBuilder.parse(configXML);
        return true;
    }

    /**
     * Returns initialized {@code object} of specified {@link Class} type.
     *
     * @param clazz {@code Class} type of {@code object} to initialize.
     *
     * @return initialised {@code object} of specified {@link Class} type.
     *
     * @throws ReflectiveOperationException in case of exception during accessing {@code filed}
     * and {@code method} of {@code object} by {@link java.lang.reflect reflection} API.
     *
     * @throws ParserConfigurationException in case of failing to find configuration for {@code object} in
     * configuration file.
     */
    public Object loadBean(Class clazz) throws ReflectiveOperationException, ParserConfigurationException{


        Object object = null;
        try {
            //noinspection NullArgumentToVariableArgMethod
            object = clazz.getConstructor(null).newInstance(null);
            object = this.initializeBean(clazz,object);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException ex){
            logger.log(Level.WARNING,"Exception during creation bean "+clazz.getName(),ex);
        }

        return object;
    }

    /**
     * Sets fields values to {@code object} specified in input parameter and returns this {@code object}.
     *
     * @param clazz {@link Class} type of {@code object} to set fields values.
     * @param object to set fields values
     *
     * @return returns {@code object} with initialized fields.
     *
     * @throws ReflectiveOperationException in case of exception during accessing {@code filed}
     * and {@code method} of {@code object} by {@link java.lang.reflect reflection} API.
     *
     * @throws ParserConfigurationException in case of failing to find configuration for {@code object} in
     * configuration file.
     */
    private Object initializeBean(Class clazz,Object object) throws ReflectiveOperationException,
                                                                    ParserConfigurationException{

        if(document==null){
            String exceptionMsg = "Object initialization failed because configuration file had not been loaded.";
            logger.log(Level.SEVERE,exceptionMsg);
            throw new NullPointerException(exceptionMsg);
        }

        Element beanConfiguration = this.getElementByClassName(clazz, document);

        if(beanConfiguration==null){
            ParserConfigurationException ex = new ParserConfigurationException("No bean configuration for "+clazz.getName());
            logger.log(Level.SEVERE,"There is no bean configuration for "+clazz.getName()+"in configuration file.");
            throw ex;
        }


        NodeList beanFields = beanConfiguration.getElementsByTagName("property");

        if(beanFields!=null && beanFields.getLength()>0) {
            for (int i = 0; i < beanFields.getLength(); i++) {
                Element objectField = (Element) beanFields.item(i);
                this.initializeFields(clazz, object, objectField);
            }
        }


        return object;
    }


    /**
     * Sets the value to the field by setter method in {@code object}.
     *
     * @param clazz {@link Class} type of object to set value of field.
     * @param object {@code object} to set field value.
     * @param objectField {@link Element} object which contains {@code name} and {@code value} attributes
     *                                   of filed in {@code object}to be set.
     *
     * @throws ReflectiveOperationException if any exception has occurred during accessing {@code filed}
     * and {@code method} of {@code object} by {@link java.lang.reflect reflection} API.
     */
    private void initializeFields(Class clazz, Object object, Element objectField) throws ReflectiveOperationException{

        String fieldName = objectField.getAttribute("name");
        String fieldValue = objectField.getAttribute("value");

        String setterMethodName = XMLReader.getSetterMethod(fieldName);

        try {
            Field field = clazz.getDeclaredField(fieldName);
            Class fieldType = field.getType();

            Method setter = clazz.getMethod(setterMethodName, fieldType);

            this.setFieldValue(object,fieldType,fieldValue,setter);
        }
        catch (NoSuchFieldException ex){
            logger.log(Level.SEVERE,"There is no field \""+fieldName+"\""+" in \""+clazz+"\".",ex);
            throw ex;
        }
        catch (NoSuchMethodException ex){
            logger.log(Level.SEVERE,"There is no public setter method \""+setterMethodName+"\""+" in \""+clazz+"\".",ex);
            throw ex;
        }
        catch (IllegalAccessException |InvocationTargetException ex){
            logger.log(Level.SEVERE,"Setter method \""+setterMethodName+"\""+" in \""+clazz+"\" has access level lower them public",ex);
            throw ex;
        }


    }

    /**
     * Uses {@code public} setter method of object to set fields' value.
     *
     * @param object {@link Object} for set field value.
     * @param fieldType {@link Class} type of field in object.
     * @param fieldValue value of field to set.
     * @param setter {@link Method} to set fields' value.
     *
     * @throws InvocationTargetException if there is no setter method for field specified in input parameters.
     * @throws IllegalAccessException if setter method has access level lower then {@code public}.
     */
    private void setFieldValue(Object object,Class fieldType,String fieldValue, Method setter)
                                throws InvocationTargetException,IllegalAccessException{

        //noinspection ConstantConditions
        switch(FieldType.getType(fieldType)){


            case DOUBLE:{

                Double dbl = Double.parseDouble(fieldValue);
                setter.invoke(object,dbl);
                break;
            }

            case INTEGER:{

                Integer intg = Integer.parseInt(fieldValue);
                setter.invoke(object,intg);
                break;
            }

            case LONG:{
                Long lng = Long.parseLong(fieldValue);
                setter.invoke(object,lng);
                break;
            }

            case STRING:{

                setter.invoke(object,fieldValue);
                break;
            }

            case BOOLEAN:{

                Boolean bln = Boolean.valueOf(fieldValue);
                setter.invoke(object,bln);
                break;
            }


        }


    }

    /**
     * Returns string name of setter method for field of object.
     *
     * @param fieldName {@link String} representation of the objects' field name
     * @return string name of setter method for field of object
     */
    private static String getSetterMethod(String fieldName){

        char[] chars = fieldName.trim().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        return "set" + chars;
    }

    /**
     * Returns {@link Element} object from config file
     * which consists attributes for initialization {@link java.lang.Object} of specified in input parameters Class.
     *
     * @param clazz the {@link Class} type of  object for which we want to load fields values from configuration file.
     * @param document {@link Document} object associated with configuration file.
     *
     * @return {@link Element} object matching with input parameter {@code clazz} or {@null} if there is no matching.
     */
    private Element getElementByClassName(Class clazz, Document document){

        String className = clazz.getName();

        Element rootElement = document.getDocumentElement();

        if("beans".equals(rootElement.getTagName())){

            NodeList beanList = rootElement.getElementsByTagName("bean");

            if(beanList!=null && beanList.getLength()>0){
                for(int i=0; i<beanList.getLength(); i++){
                    Element beanElement = (Element)beanList.item(i);


                    if("bean".equals(beanElement.getTagName()) &&
                            className.equals(beanElement.getAttribute("class"))){
                        return beanElement;

                    }

                }
            }

        }

        return null;
    }
}

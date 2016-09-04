package jsoupParser.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResTransformer {

    /**
     * {@code Logger} object that logs the program execution process.
     */
    private static final Logger logger = Logger.getLogger(ResTransformer.class.getName());

    /**
     * {@code Documet} object which represents the result of parsing and filtering.
     */
    private Document resultDocument;

    /**
     * Initializes a newly created {@link ResTransformer} object.
     *
     * @param resultDocument {@link org.w3c.dom.Document} object.
     */
    public ResTransformer(Document resultDocument){
        this.resultDocument = resultDocument;
    }

    public Document aggregateCarsRestyling(){
        Element rootElement = resultDocument.getDocumentElement();
        NodeList allBrands = rootElement.getElementsByTagName("brand");

        int nodeListLength = allBrands.getLength();

        if(nodeListLength>0){

            for(int i = 0; i<nodeListLength; i++){
                Element brand = (Element)allBrands.item(i);

                this.processModels(brand);
            }
        }else{

            logger.warning("There are no elements in result document");
        }


        return resultDocument;
    }

    private void processModels(Element brand){
        NodeList brandModels = brand.getElementsByTagName("model");

        int nodeListLength = brandModels.getLength();

        if(nodeListLength>0){
            for(int i = 0; i<nodeListLength; i++){
                Element model = (Element) brandModels.item(i);
                this.replaceRestyling(model);
            }
        }

    }

    private void replaceRestyling(Element model){
        NodeList allModelGenerations = model.getElementsByTagName("generation");


        int nodeListLength = allModelGenerations.getLength();

        if(nodeListLength>1){

            Set<Element> nodesToDelete = new HashSet<>();

            for(int i = 0; i<nodeListLength; i++){
                Element generation = (Element)allModelGenerations.item(i);
                String elementName1 = generation.getAttribute("name");

                if(StringUtil.containsRestyling(elementName1)){

                    String restylingYears =generation.getAttribute("years");
                    elementName1 = StringUtil.clearFromRestyling(elementName1);

                    for(int j =0; j<nodeListLength; j++){
                        Element generationToCompare = (Element)allModelGenerations.item(j);
                        String elementName2 = generationToCompare.getAttribute("name");

                        if(StringUtil.compareCarGenerations(elementName1,elementName2)){
                            String years = generationToCompare.getAttribute("years");
                            years += "/" +restylingYears;
                            years = StringUtil.sortYears(years);
                            nodesToDelete.add(generation);
                            generationToCompare.setAttribute("years",years);
                        }
                    }
                }

            }

            if(nodesToDelete.size()>0) {
                logger.info("Delete restyling node for " + model.getAttribute("name"));
                nodesToDelete.forEach(model::removeChild);
            }

        }

    }

    public File produceHTML(){
        TransformerFactory factory = TransformerFactory.newInstance();
        DOMSource xmlSource = new DOMSource(resultDocument);
        File resultHtml = null;
        try {

            logger.info(" - creating temporary HTML file...");
            resultHtml = File.createTempFile("result", ".html");
            Result resultToHtml = new StreamResult(resultHtml);

            logger.info(": - loading XSLT file from resources...");
            Source source = new StreamSource(ResTransformer.class.getResourceAsStream("/style.xsl"));



            Transformer xmlTransformer = factory.newTransformer(source);
            logger.info(": - applying XSLT to result XML file...");
            xmlTransformer.transform(xmlSource, resultToHtml);

        }

        catch (TransformerException | RuntimeException |IOException ex){
            logger.log(Level.WARNING,
                     " - the result file can not be displayed in HTML because of the "
                    + ex
                    + ":",ex);
        }

        return resultHtml;
    }

    public boolean saveResultToFile(String fileDirectory){
        boolean saved = false;

        File resultXml = new File(fileDirectory);


        TransformerFactory factory = TransformerFactory.newInstance();
        DOMSource xmlSource = new DOMSource(this.getResultDocument());
        Transformer xmlTransformer;

        try {
            xmlTransformer= factory.newTransformer();
            Result resultToFile = new StreamResult(resultXml);
            xmlTransformer.transform(xmlSource, resultToFile);
            saved = true;
        }
        catch (Exception ex){
            logger.log(Level.WARNING, "- the result document wasn't saved to file "+resultXml.getAbsolutePath()+" because of the "+ex.getClass().getName(),ex);
        }


        return saved;
    }

    private Document getResultDocument() {
        return resultDocument;
    }

    public void setResultDocument(Document resultDocument) {
        this.resultDocument = resultDocument;
    }
}

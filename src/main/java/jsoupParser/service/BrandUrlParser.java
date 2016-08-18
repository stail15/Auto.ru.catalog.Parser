package jsoupParser.service;

import jsoupParser.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BrandUrlParser implements Runnable {

    private Element carBrandLink;
    private int errorCount=0;

    public Element getElement() {
        return carBrandLink;
    }

    public void setElement(Element element) {
        this.carBrandLink = element;
    }


    @Override
    public void run() {
        List<String> allModelsUrl = new ArrayList<>();

        String carBrandUrl = "https:" + carBrandLink.attr("href").replace("all/", "");

        Document carBrandDoc = null;

        while (carBrandDoc==null) {
            try {
                carBrandDoc = Jsoup.connect(carBrandUrl).get();
                carBrandDoc.setBaseUri("https://auto.ru");
            } catch (IOException ex) {
                System.out.println("Error occurred while parsing " + carBrandUrl);
                errorCount++;
                if(errorCount>9){
                    return;
                }
            }
        }

        String separator = Main.urlService.getModelClassSeparator();
        Elements modelHtml = carBrandDoc.select(separator);
        for(Element el : modelHtml){
            String modelUrl = el.select("a").first().attr("abs:href");
            allModelsUrl.add(modelUrl);
        }

        Main.modelsUrl.addAll(allModelsUrl);

    }
}

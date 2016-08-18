package jsoupParser.service;

import jsoupParser.Main;
import jsoupParser.pojo.Car;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class ModelUrlParser implements Runnable {

    private final Car car= new Car();
    private String modelUrl;
    private int errorCount=0;

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    @Override
    public void run(){
        String brand;
        String model;
        Map<String,String> generations;


        Document carModelDoc = null;

        while (carModelDoc==null) {
            System.out.println("Connecting to " + modelUrl);
            try {
                carModelDoc = Jsoup.connect(modelUrl).get();
                carModelDoc.setBaseUri("https://auto.ru");
            } catch (IOException ex) {
                errorCount++;
               // System.out.println("Error occurred while parsing " + modelUrl+"  "+errorCount);
                if(errorCount>9){
                    return;}
                try {
                    Thread.currentThread().sleep(50);}
                catch (InterruptedException e){e.getStackTrace();}

            }
        }

        brand=getCarBrand(carModelDoc);
        model=getCarModel(carModelDoc);
        generations=getModelGenerations(carModelDoc);

       // getCarImage(carModelDoc);

        car.setModelURL(modelUrl);
        car.setBrand(brand);
        car.setModel(model);
        generations= analyzeGenerations(generations, car);
        car.setModelGenerations(generations);
        Main.carList.add(car);

    }

    private String getCarBrand(Document doc){
        String carBrandAnchor = Main.urlService.getCarBrendAnchor();
        Element carBrand = doc.select(carBrandAnchor).first();
        return carBrand.text();
    }

    private String getCarModel(Document doc){
        String carModelAnchor = Main.urlService.getCarModelAnchor();
        Element carModel = doc.select(carModelAnchor).first();
        return carModel.text();
    }

    private void getCarImage (Document doc){

    }

    private Map<String,String> getModelGenerations (Document doc){
        Map<String,String> modelGeneration= new HashMap<>();
        String modelGenerationAnchor = Main.urlService.getModelGenerationAnchor1();
        Elements generations = doc.select(modelGenerationAnchor);
        if(generations.size()>0){
            for(Element link : generations){
                String generation = link.text();
                String years = link.select("span").text();
                generation = generation.substring(0,generation.length()-years.length()-1);
                years =  trimText(years);
                modelGeneration.put(generation, years);
               // System.out.println(generation+" "+years);
            }
        }
        else {
            List<String> modelDate = new ArrayList<>();
            modelGenerationAnchor = Main.urlService.getModelGenerationAnchor2();
            generations = doc.select(modelGenerationAnchor);

            if(generations.size()>0){
                for(Element link : generations){
                    String date = link.text();
                    date = trimText(date);
                    String[] dateList = date.split("-");
                    Collections.addAll(modelDate,dateList);
                }
            }
            else {
                modelGenerationAnchor = Main.urlService.getModelGenerationAnchor3();
                Element link = doc.select(modelGenerationAnchor).first();
                if(!(link==null)){
                    String date = link.text();
                    date = trimText(date);
                    String[]dateList = date.split("-");
                    Collections.addAll(modelDate, dateList);

                }
            }

            String years = findModelDate(modelDate);
            modelGeneration.put("", years);

        }
        return modelGeneration;
    }

    private String findModelDate(List<String> dateList){
        String[]date = new String[2];
        int max = 0;
        int min = 2100;
        for(int i = 0; i<dateList.size();i++){

            try{
                int parseDate = Integer.parseInt(dateList.get(i));
                if(max<parseDate){max=parseDate;}
                if(min>parseDate){min=parseDate;}
            }catch (Exception ex){
                date[1]=dateList.get(i);
            }
        }

        date[0]=String.valueOf(min);
        if(date[1]==null) {
            date[1]=String.valueOf(max);
        }

        return date[0] + "-" + date[1];
    }
    private String trimText(String years){
        int length=years.length();
        return years.substring(0, 4) + "-" + years.substring(length - 4, length);
    }
    private Map<String,String> analyzeGenerations(Map<String, String> generations, Car car){

        Map<String,String> restyleMap = new HashMap<>();
        Map<String,String> modelInfoCopy = (HashMap)((HashMap)generations).clone() ;
        byte[] buff = new byte[]{-48,-27,-15,-14,-32}; // cleaning word "Реста" in Cp1251 charset for looking matches in models' generations
        String match = null;

       try {
            match =new String(buff,"Cp1251");;
        }
        catch (UnsupportedEncodingException ex){ex.getMessage();}

        for(Map.Entry<String,String> entry : generations.entrySet()){
            String key = entry.getKey();
            if(key.contains(match)){
                restyleMap.put(key,entry.getValue());
            }
        }

        if(!restyleMap.isEmpty()){
            for(Map.Entry<String,String> entry2 : restyleMap.entrySet()){
                String modelInformation = entry2.getKey();
                String modelUpdate = modelInformation.substring(0,modelInformation.indexOf(match)-1);
                int modelUpdLength = modelUpdate.length();

                for(Map.Entry<String,String> entry3 : generations.entrySet()){
                    String model1 = entry3.getKey();
                    if (model1.contains(modelUpdate) && !model1.contains(match)&&model1.length()==modelUpdLength) {
                        String modelDate = sortDate(entry3.getValue() + "/" + entry2.getValue());
                        modelInfoCopy.put(model1, modelDate);
                        entry3.setValue(modelDate);
                    } else {
                        if (model1.equals(modelInformation)) {
                            modelInfoCopy.remove(modelInformation);
                        }

                    }
                }
            }
        }

        return modelInfoCopy;
    }
    private String sortDate(String dates){

        String[] buff = dates.replace(" ","").split("/");
        for(int i=0;i<buff.length;i++){
            for(int j = 0;j<buff.length-i-1;j++){
                int first = Integer.parseInt(buff[j].substring(0, 4));
                int second = Integer.parseInt(buff[j+1].substring(0,4));
                if(first>second) {
                    String k = buff[j];
                    buff[j] = buff[i + 1];
                    buff[j + 1] = k;
                }
            }
        }

        StringBuilder stringBuffer = new StringBuilder();
        for(int i= 0; i<buff.length;i++){
            stringBuffer.append(buff[i]);
            if(i<(buff.length-1)){
                stringBuffer.append(" / ");
            }

        }
        return stringBuffer.toString();
    }

}

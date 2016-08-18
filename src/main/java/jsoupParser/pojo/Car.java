package jsoupParser.pojo;

import java.util.Map;


public class Car {

    private String brand;
    private String model;
    private String modelURL;
    private String img;
    private Map<String,String> modelGenerations;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelURL() {
        return modelURL;
    }

    public void setModelURL(String modelURL) {
        this.modelURL = modelURL;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private Map<String, String> getModelGenerations() {
        return modelGenerations;
    }

    public void setModelGenerations(Map<String, String> modelGenerations) {
        this.modelGenerations = modelGenerations;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        String info =brand+ ";"+ model+";";
        for(Map.Entry<String, String> entry : this.getModelGenerations().entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            if(key.length()>0){
                key = key.substring(brand.length()+model.length()+2,key.length())
                        +", "+value+";"+modelURL;
            }
            else {
                key = value;
            }
            strb.append(info);
            strb.append(key);
            strb.append("\r\n");

        }

        return strb.toString();
    }
}

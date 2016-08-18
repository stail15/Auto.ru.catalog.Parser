package jsoupParser.service;

import java.util.List;


public class UrlService {
    private String mainUrl;
    private String brandClassSeparator;
    private String modelClassSeparator;
    private String carBrendAnchor;
    private String carModelAnchor;
    private String modelGenerationAnchor1;
    private String modelGenerationAnchor2;
    private String modelGenerationAnchor3;
    private List<String> modelUrlList;

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public List<String> getModelUrlList() {
        return modelUrlList;
    }

    public void setModelUrlList(List<String> modelUrlList) {
        this.modelUrlList = modelUrlList;
    }

    public String getBrandClassSeparator() {
        return brandClassSeparator;
    }

    public void setBrandClassSeparator(String brandClassSeparator) {
        this.brandClassSeparator = brandClassSeparator;
    }

    public String getModelClassSeparator() {
        return modelClassSeparator;
    }

    public void setModelClassSeparator(String modelClassSeparator) {
        this.modelClassSeparator = modelClassSeparator;
    }

    public String getCarBrendAnchor() {
        return carBrendAnchor;
    }

    public void setCarBrendAnchor(String carBrendAnchor) {
        this.carBrendAnchor = carBrendAnchor;
    }

    public String getCarModelAnchor() {
        return carModelAnchor;
    }

    public void setCarModelAnchor(String carModelAnchor) {
        this.carModelAnchor = carModelAnchor;
    }

    public String getModelGenerationAnchor1() {
        return modelGenerationAnchor1;
    }

    public void setModelGenerationAnchor1(String modelGenerationAnchor1) {
        this.modelGenerationAnchor1 = modelGenerationAnchor1;
    }

    public String getModelGenerationAnchor2() {
        return modelGenerationAnchor2;
    }

    public void setModelGenerationAnchor2(String modelGenerationAnchor2) {
        this.modelGenerationAnchor2 = modelGenerationAnchor2;
    }

    public String getModelGenerationAnchor3() {
        return modelGenerationAnchor3;
    }

    public void setModelGenerationAnchor3(String modelGenerationAnchor3) {
        this.modelGenerationAnchor3 = modelGenerationAnchor3;
    }

    @Override
    public String toString() {
        return "UrlService{" +"\r\n"+
                "mainUrl='" + mainUrl + '\'' +"\r\n"+
                ", brandClassSeparator='" + brandClassSeparator + '\'' +"\r\n"+
                ", modelClassSeparator='" + modelClassSeparator + '\'' +"\r\n"+
                ", carBrendAnchor='" + carBrendAnchor + '\'' +"\r\n"+
                ", carModelAnchor='" + carModelAnchor + '\'' +"\r\n"+
                ", modelGenerationAnchor1='" + modelGenerationAnchor1 + '\'' +"\r\n"+
                ", modelGenerationAnchor2='" + modelGenerationAnchor2 + '\'' +"\r\n"+
                ", modelGenerationAnchor3='" + modelGenerationAnchor3 + '\'' +"\r\n"+
                ", modelUrlList=" + modelUrlList +
                '}';
    }
}

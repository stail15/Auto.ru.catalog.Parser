package jsoupParser.service;

import java.util.List;


public class UrlService {
    private String mainUrl;
    private String cookiesHostName;
    private String brandClassSeparator;
    private String modelClassSeparator;
    private String carClassSeparator;

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getCookiesHostName() {
        return cookiesHostName;
    }

    public void setCookiesHostName(String cookiesHostName) {
        this.cookiesHostName = cookiesHostName;
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

    public String getCarClassSeparator() {
        return carClassSeparator;
    }

    public void setCarClassSeparator(String carClassSeparator) {
        this.carClassSeparator = carClassSeparator;
    }
}

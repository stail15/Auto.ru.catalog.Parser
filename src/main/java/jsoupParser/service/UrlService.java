package jsoupParser.service;


public class UrlService {
    private String mainUrl;
    private String cookiesHostName;
    private String brandSeparator;
    private String modelSeparator;
    private String generationSeparator;
    private String carGenerationNameSeparator;
    private String carYearsSeparator;

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

    public String getBrandSeparator() {
        return brandSeparator;
    }

    public void setBrandSeparator(String brandSeparator) {
        this.brandSeparator = brandSeparator;
    }

    public String getModelSeparator() {
        return modelSeparator;
    }

    public void setModelSeparator(String modelSeparator) {
        this.modelSeparator = modelSeparator;
    }

    public String getGenerationSeparator() {
        return generationSeparator;
    }

    public void setGenerationSeparator(String generationSeparator) {
        this.generationSeparator = generationSeparator;
    }

    public String getCarYearsSeparator() {
        return carYearsSeparator;
    }

    public void setCarYearsSeparator(String carYearsSeparator) {
        this.carYearsSeparator = carYearsSeparator;
    }

    public String getCarGenerationNameSeparator() {
        return carGenerationNameSeparator;
    }

    public void setCarGenerationNameSeparator(String carGenerationNameSeparator) {
        this.carGenerationNameSeparator = carGenerationNameSeparator;
    }
}

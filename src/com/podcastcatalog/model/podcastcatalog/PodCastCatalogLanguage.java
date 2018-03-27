package com.podcastcatalog.model.podcastcatalog;

public enum PodCastCatalogLanguage {
    SE("SE"),
    CN("CN"), //China
    ES("ES"),//Spain
    NO("NO"),//Norway
    FR("FR"),//France
    DE("DE"),//Germany
    US("US");

    private final String lang;
    PodCastCatalogLanguage(String lang) {
        this.lang = lang;
    }

    private String getLang() {
        return lang;
    }

    public static PodCastCatalogLanguage fromString(String lang) {
        if(lang==null || lang.isEmpty()){
            return null;
        }

        if(lang.equalsIgnoreCase("SWE")) {
            return SE; //REMOVE After next iOS version
        }

        String anObject = lang.trim().toUpperCase();
        for (PodCastCatalogLanguage catalogLang : PodCastCatalogLanguage.values()) {
            if(catalogLang.getLang().equals(anObject)){
                return  catalogLang;
            }
        }

        return null;
    }
}


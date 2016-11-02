package com.podcastcatalog.api.response;

public enum PodCastCatalogLanguage {
    Sweden("SE");

    private String lang;
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

        String anObject = lang.trim().toUpperCase();
        for (PodCastCatalogLanguage catalogLang : PodCastCatalogLanguage.values()) {
            if(catalogLang.getLang().equals(anObject)){
                return  catalogLang;
            }
        }

        return null;
    }
}


package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderCN;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderDE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderES;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderFR;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderNO;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderSE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderUS;

public enum PodCastCatalogLanguage {
    SE("SE", PodCastCatalogBuilderSE.class),
    CN("CN", PodCastCatalogBuilderCN.class), //China
    ES("ES", PodCastCatalogBuilderES.class),//Spain
    NO("NO", PodCastCatalogBuilderNO.class),//Norway
    FR("FR", PodCastCatalogBuilderFR.class),//France
    DE("DE", PodCastCatalogBuilderDE.class),//Germany
    US("US", PodCastCatalogBuilderUS.class);

    private final String lang;
    private final Class catalogBuilder;
    PodCastCatalogLanguage(String lang, Class<? extends PodCastCatalogBuilder> catalogBuilder) {
        this.lang = lang;
        this.catalogBuilder = catalogBuilder;
    }

    private String getLang() {
        return lang;
    }

    public PodCastCatalogBuilder create() {
        try {
            return (PodCastCatalogBuilder) catalogBuilder.newInstance();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
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


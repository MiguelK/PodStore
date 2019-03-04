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
    SE("SE", PodCastCatalogBuilderSE.class, false, true, true),
    CN("CN", PodCastCatalogBuilderCN.class, false, true, true), //China
    ES("ES", PodCastCatalogBuilderES.class, false, true, true), //Spain
    NO("NO", PodCastCatalogBuilderNO.class, false, true, true), //Norway
    FR("FR", PodCastCatalogBuilderFR.class, false, true, true), //France
    DE("DE", PodCastCatalogBuilderDE.class, false, true, true), //Germany
    US("US", PodCastCatalogBuilderUS.class, false, true, true); //US

    private final String lang;
    private final Class catalogBuilder;
    private final boolean inMemory;
    private final boolean inMemoryIndex;
    private final  boolean inMemorySearchSuggestions;

    PodCastCatalogLanguage(String lang, Class<? extends PodCastCatalogBuilder> catalogBuilder,
                           boolean inMemory, boolean inMemoryIndex, boolean inMemorySearchSuggestions) {
        this.lang = lang;
        this.catalogBuilder = catalogBuilder;
        this.inMemory = inMemory;
        this.inMemoryIndex = inMemoryIndex;
        this.inMemorySearchSuggestions = inMemorySearchSuggestions;
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

    public boolean isInMemorySearchSuggestions() {
        return inMemorySearchSuggestions;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public boolean isInMemoryIndex() {
        return inMemoryIndex;
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


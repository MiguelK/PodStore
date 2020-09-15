package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderAU;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderBR;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderCA;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderCH;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderCN;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderDK;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderDE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderES;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderFI;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderFR;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderGB;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderGT;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderIL;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderIE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderIN;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderIT;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderJP;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderKR;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderNL;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderNO;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderPT;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderRU;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderSE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderTH;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderUS;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderVN;

//See http://www.mathguide.de/info/tools/languagecode.html
//https://www.apple.com/choose-country-region/
public enum PodCastCatalogLanguage {

    /*
China XXXX BUG
     */
    SE("SE", PodCastCatalogBuilderSE.class, false, true, true),
    CN("CN", PodCastCatalogBuilderCN.class, false, true, true), //China
    ES("ES", PodCastCatalogBuilderES.class, false, true, true), //Spain
    NO("NO", PodCastCatalogBuilderNO.class, false, true, true), //Norway
    FR("FR", PodCastCatalogBuilderFR.class, false, true, true), //France
    DE("DE", PodCastCatalogBuilderDE.class, false, true, true), //Germany
    US("US", PodCastCatalogBuilderUS.class, false, true, true), //US

    //NEW ones...
    DK("DK", PodCastCatalogBuilderDK.class, false, true, true), //Denmark
    AU("AU", PodCastCatalogBuilderAU.class, false, true, true), //Australia
    IN("IN", PodCastCatalogBuilderIN.class, false, true, true), //India
    PT("PT",PodCastCatalogBuilderPT.class, false, true, true), //Portugal
    IE("IE",PodCastCatalogBuilderIE.class, false, true, true), //Ireland
    FI("FI",PodCastCatalogBuilderFI.class, false, true, true), //Finland

    BR("BR",PodCastCatalogBuilderBR.class, false, true, true), //Brazil
    CA("CA",PodCastCatalogBuilderCA.class, false, true, true), //Canada (English)
    IT("IT",PodCastCatalogBuilderIT.class, false, true, true), //Italy
    VN("VN",PodCastCatalogBuilderVN.class, false, true, true), //Vietnamn
    NL("NL",PodCastCatalogBuilderNL.class, false, true, true), //Netherlands

    JP("JP",PodCastCatalogBuilderJP.class, false, true, true), //Japan
    RU("RU",PodCastCatalogBuilderRU.class, false, true, true), //Russia
    CH("CH",PodCastCatalogBuilderCH.class, false, true, true), //Switzerland
    KR("KR",PodCastCatalogBuilderKR.class, false, true, true), //South Korea
    GB("GB",PodCastCatalogBuilderGB.class, false, true, true), //United Kingdom
    TH("TH",PodCastCatalogBuilderTH.class, false, true, true), //Thailand

    IL("IL",PodCastCatalogBuilderIL.class, false, true, true), //HE in appHebrew (Israel) and  il backend
    GT("GT",PodCastCatalogBuilderGT.class, false, true, true); //Guatemala

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

        if(lang.trim().equalsIgnoreCase("SWE")) {
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


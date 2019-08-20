package com.podcastcatalog.service.appstatistic;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AppStatisticDataContainer implements Serializable {

    public Map<PodCastCatalogLanguage, AppStatisticData> appStatisticDataLang = new HashMap<>();

}

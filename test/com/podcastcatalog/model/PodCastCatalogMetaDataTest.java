package com.podcastcatalog.model;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.job.PodCastCatalogUpdater;
import com.podcastcatalog.service.subscription.FtpOneClient;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.testng.Assert.*;

public class PodCastCatalogMetaDataTest {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogMetaDataTest.class.getName());

    @Test(groups = TestUtil.SLOW_TEST)
    public void testToString() throws Exception {
        PodCastCatalogMetaData podCastCatalogMetaData = FtpOneClient.getInstance().load(PodCastCatalogLanguage.US);
        LOG.info("podCastCatalogMetaData=" + podCastCatalogMetaData);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void validateAllFromServer() throws Exception {

        List<String> failedLang = new ArrayList<>();
        PodCastCatalogMetaData podCastCatalogMetaData = null;
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
            try {
                podCastCatalogMetaData = FtpOneClient.getInstance().load(language);
                //System.out.printf("podCastCatalogMetaData=" + podCastCatalogMetaData);
                LOG.info("podCastCatalogMetaData=" + podCastCatalogMetaData);

                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("Failed: " + language + " " + e.getMessage());
                failedLang.add(language.name());
            }
        }
        if(!failedLang.isEmpty()) {
            Assert.fail("failedLang = " + failedLang);
        }

    }
}
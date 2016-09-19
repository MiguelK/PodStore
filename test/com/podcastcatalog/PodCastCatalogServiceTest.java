package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCatalogServiceTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void build_load_catalog() {

//        DiscStorage.setDataDirectory(); //Data dir in Open Shift?
        //0#
        PodCastCatalogService
                .getInstance().startAsync();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
        System.out.println(podCastCatalog);
        //podCastCatalog.toJSON()->

        TestUtil.assertToJSONNotNull(podCastCatalog);

        Assert.assertNotNull(podCastCatalog);
/*
        //2# search free text
        List<PodCast> search =
                PodCastCatalogService.getInstance().search("Some text");
        //toJSON()*/
    }


}
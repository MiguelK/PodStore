package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PodCastCategoryTypeTest {

    @Test
    public void allCategoriesExistsInPodCastCategoryType() {

        for (PodCastIdCollector.Category category : PodCastIdCollector.Category.values()) {
            PodCastCategoryType podCastCategoryType1 = PodCastCategoryType.valueOf(category.name());
            System.out.println(category.name() + " " + podCastCategoryType1);
            Assert.assertNotNull(podCastCategoryType1);
        }
    }

    @Test
    public void allPodCastCategoryTypesExistsInCategory() {

        for (PodCastCategoryType categoryType : PodCastCategoryType.values()) {
            PodCastIdCollector.Category category = PodCastIdCollector.Category.valueOf(categoryType.name());
            System.out.println(categoryType.name() + " " + category);
            Assert.assertNotNull(category);
        }
    }

    @Test
    public void allTypes() throws Exception {
        int length = PodCastIdCollector.Category.values().length;
        int length1 = PodCastCategoryType.values().length;
        Assert.assertTrue(length == length1);
    }

    @Test
    public void podCastCategoryTypes() throws Exception {

        String[] all = (String[]) Arrays.asList("TV & Film").toArray();

        List<PodCastCategoryType> podCastCategoryTypes = PodCastCategoryType.fromString(all);

        Assert.assertTrue(all.length == podCastCategoryTypes.size());

        System.out.println(podCastCategoryTypes);

    }
}
package com.podcastcatalog.util;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.service.datastore.LocatorProduction;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2018-07-08.
 */
public class DynamicLinkIndexTest {

    @Test
    public void testAddLink() throws IOException {

        DynamicLinkIndex linkIndex = new DynamicLinkIndex();

        LocatorProduction locatorProduction = new LocatorProduction();
        File file = new File(locatorProduction.getPodDataHomeDirectory(), "dynamicLinks-index.json");


        System.out.println("File " + file.getAbsolutePath());
        linkIndex.loadFrom(file);

        Path path = file.toPath();
//        Stream<String> lines = Files.lines(path, Charset.forName("UTF-8")); //ISO-8859-1

       /* List<String> replaced = lines.map(line ->

             //   this.processLine(line)

        line.replaceAll("template_podcast_title","gfgf")

                ).collect(Collectors.toList());*/

        //System.out.println("replaced " + replaced);

       // lines.forEach(s -> processLine(s));


        //,"1097108911###-33a6eb58-3b1ae22a":"https://qw7xh.app.goo.gl/kGRAANkGVwzoEMMS8",

        /*DynamicLinkIndex linkIndex = new DynamicLinkIndex();

        linkIndex.addLink("a1", "Test 1");
        linkIndex.addLink("a2", "Test 2");

        LocatorProduction locatorProduction = new LocatorProduction();

        linkIndex.saveTo(new File(locatorProduction.getPodDataHomeDirectory(), "links.json"));*/
    }

    protected void processLine(String aLine){
        //use a second Scanner to parse the content of each line
        System.out.println("LINE==== " + aLine);
        StringBuilder result = new StringBuilder();
        Set<String> keys = new HashSet<>();

        try(Scanner scanner = new Scanner(aLine)){
            scanner.useDelimiter(",");
            while (scanner.hasNext()){
                //assumes the line has a certain structure
                String name = scanner.next();
                if(!scanner.hasNext()){
                    return;
                }
                String value = scanner.next();
              //  value.
                String[] split = value.split(":");

                System.out.println("KEY===" + split[0] + " VALUE=" + split[1]);
                if(keys.contains(split[0])){
                    System.out.println("DUPLICATE " + split[0]);
                }

                keys.add(split[0]);
            }
        }
    }

}
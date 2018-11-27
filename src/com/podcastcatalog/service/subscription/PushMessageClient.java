package com.podcastcatalog.service.subscription;

import com.google.common.collect.Maps;
import us.raudi.pushraven.FcmResponse;
import us.raudi.pushraven.Message;
import us.raudi.pushraven.Notification;
import us.raudi.pushraven.Payload;
import us.raudi.pushraven.Pushraven;
import us.raudi.pushraven.configs.ApnsConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by miguelkrantz on 2018-11-23.
 */
public class PushMessageClient {

   private static final  PushMessageClient INSTANCE = new PushMessageClient();

    private final static Logger LOG = Logger.getLogger(PushMessageClient.class.getName());


    private File googleCredentialFile;

    public static PushMessageClient getInstance() {
        return INSTANCE;
    }

    public void configure(File file) {
        googleCredentialFile = file;
        Pushraven.setAccountFile(googleCredentialFile);
        Pushraven.setProjectId("pods-20bd0");
    }

    void pushMessageWithToken(String title, String body,
                              String description, String pid,
                              String eid, String token) {

        try {

        Notification notification = new Notification()
                .title(title)
                .body(body);

        ApnsConfig apnsConfig = new ApnsConfig();

        HashMap<String, String> values =  Maps.newHashMap();

        Payload aps = new Payload();
        aps.addAttributeMap("aps", values);

        Payload  alertPayload = new Payload();
        alertPayload.addAttribute("pid", pid);
        alertPayload.addAttribute("eid", eid);
        alertPayload.addAttribute("episodeDescription", description);
        aps.addAttributePayload("aps", alertPayload);

        apnsConfig.payload(aps);

      //  String test = "fcHTPu8jdTo:APA91bFOt8RCA3MEBe9FvbobKvzcEmNSNlsHkb7akDxZkMEoXagi-vtjEbcqySiYBx88TqAtYFIhtmiQyiPUhvnI03qN3kFKmess5Ego_B7_D-4Wi3m1f4Zl59Xc15aBxFPWnpVNTsF3";
        Message raven = new Message()
                .name("id")
                .notification(notification)
                .apns(apnsConfig)
             .token(token);

        FcmResponse fcmResponse = Pushraven.push(raven);
        System.out.println("Response=" + fcmResponse);

        }catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed send push message", e);
        }
    }

    void pushMessage(String title, String body, String topic) {

        Notification not = new Notification()
                .title(title)
                .body(body);

        ApnsConfig apnsConfig = new ApnsConfig();

        HashMap<String, String> values = new HashMap<>();

        Payload aps = new Payload();
        aps.addAttributeMap("aps", values);

        Payload  alertPayload = new Payload();
        alertPayload.addAttribute("articleId", "test1234");
        aps.addAttributePayload("aps", alertPayload);

        apnsConfig.payload(aps);

        String  token = "dR2k45Ca3OA:APA91bFcWGKrgA0cKF3psV1NW-VwtLiEw-GcsOBVUBCdNXTpAegkdYtvxzSxKBJmR9mKiz-U4tuFdlqn2U25-XwQM1gBdXtmeJCK7ZhFEuml3_wGnHBdaOgP5fgulaYJg70bki4VmfRo";

        Message raven = new Message()
                .name("id")
                 .notification(not)
             .apns(apnsConfig).topic(topic);
        // .token("UserPodCasts");
        //ClientID IOS
        //262461566107-r40ekuj1r8o4h57apj0719rpuuf8im89.apps.googleusercontent.com

       // System.out.println(raven.toJson().toJSONString());

        FcmResponse fcmResponse = Pushraven.push(raven);
        System.out.println("Response=" + fcmResponse);
    }

    void pushSilent(String key, String value, String topic) {

        Notification not = new Notification()
                .title("test ")
                .body("This");

        ApnsConfig apnsConfig = new ApnsConfig();

        HashMap<String, String> values = new HashMap<>();
        values.put("title", "test3");
        values.put("body", "test3....");

        Payload aps = new Payload();
        aps.addAttributeMap("aps", values);

        Payload  alertPayload = new Payload();
        alertPayload.addAttribute("articleId", "test1234");
        //   alertPayload.addAttributeMap("alert", values); //With message
        alertPayload.addAttribute("content-available", 1);
        aps.addAttributePayload("aps", alertPayload);
        aps.addAttribute(key, value);

        apnsConfig.payload(aps);

        String  token = "dR2k45Ca3OA:APA91bFcWGKrgA0cKF3psV1NW-VwtLiEw-GcsOBVUBCdNXTpAegkdYtvxzSxKBJmR9mKiz-U4tuFdlqn2U25-XwQM1gBdXtmeJCK7ZhFEuml3_wGnHBdaOgP5fgulaYJg70bki4VmfRo";

        Message raven = new Message()
                .name("id")
                // .notification(not)
                .apns(apnsConfig).topic(topic);
        // .token("UserPodCasts");
        //ClientID IOS
        //262461566107-r40ekuj1r8o4h57apj0719rpuuf8im89.apps.googleusercontent.com

        //System.out.println(raven.toJson().toJSONString());

        FcmResponse fcmResponse = Pushraven.push(raven);
        System.out.println("Response=" + fcmResponse);
    }
}

package com.podcastcatalog.service.subscription;

import com.google.common.collect.Maps;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushMessageClient {

   private static final  PushMessageClient INSTANCE = new PushMessageClient();

    private final static Logger LOG = Logger.getLogger(PushMessageClient.class.getName());


    public static PushMessageClient getInstance() {
        return INSTANCE;
    }

    void configure(File googleCredentialFile) {
        Pushraven.setAccountFile(googleCredentialFile);
        Pushraven.setProjectId("pods-20bd0");
    }

    void pushMessageWithToken(String title, String body,
                              String description, String podCastEpisodeInfo, String pid,
                              String eid, String token) {

        try {

            if(ServerInfo.isLocalDevMode()) {
                LOG.info("DevMode no push is sent: " + title + " deviceToken=" + token);
                return;
            }

            String trimmedDescription = description;
            int totalLength = title.length() + body.length() + description.length() +
                    podCastEpisodeInfo.length() + pid.length() +eid.length();
            if(totalLength >= 663) {
                trimmedDescription = StringUtils.substring(description, 0, 40); //Test
                    int newTotalLength = title.length() + body.length() + description.length() +
                            podCastEpisodeInfo.length() + pid.length() +eid.length();
                    LOG.info("PushMessageClient to long text, totalLength=" + totalLength + ", newTotalLength=" + newTotalLength
                    + ", title=" + title + ", pid=" + pid);
           }


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
        alertPayload.addAttribute("category", "Episode1");
        alertPayload.addAttribute("podCastEpisodeDescription", trimmedDescription);
        alertPayload.addAttribute("podCastEpisodeInfo", podCastEpisodeInfo);
        alertPayload.addAttribute("mutable-content", 1);
        aps.addAttributePayload("aps", alertPayload);

        apnsConfig.payload(aps);

        Message raven = new Message()
                .name("id")
                .notification(notification)
                .apns(apnsConfig)
             .token(token);

        FcmResponse fcmResponse = Pushraven.push(raven);
            if(fcmResponse.getResponseCode() != 200) {
                if(fcmResponse.getResponseCode() ==  404) {
                    LOG.info("UnSubscribe due to 404 from Firebase, token=" + token + ",pid=" +pid);
                    PodCastSubscriptionService.getInstance().unSubscribe(token, pid);
                } else {
                    LOG.warning("Pushraven.push() return status=" + fcmResponse.getResponseCode()
                            + ", errorMessage=" + fcmResponse.getErrorMessage() + ",token=" + token + ",title=" + title);

                }
            }
        }catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed send push message ", e);
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

package com.podcastcatalog.util;

public class PodCastURLParser {

    public static class Parameters {

        private final String podCastEpisodeId;

        private final String podCastId;

         Parameters(String podCastEpisodeId, String podCastId) {
            this.podCastEpisodeId = podCastEpisodeId;
            this.podCastId = podCastId;
        }

        public String getPodCastEpisodeId() {
            return podCastEpisodeId;
        }

        public String getPodCastId() {
            return podCastId;
        }
    }

    public static Parameters parsePodCastEpisodeId(String queryString)  {
        if(queryString== null || queryString.isEmpty()){
            return null;
        }

        //eid can include + that will be decoded to empty space
        String podCastEpisodeId = null;
        String podCastId = null;

        queryString = queryString.replace("%26","&").replace("%3D","=");

            String[] andSplitted = queryString.split("&"); //&

            if(andSplitted.length>0) { //Always > 2 pis and optional eid plus isi

                for (String s : andSplitted) {
                    String[] keyValue = s.split("=");

                    if(keyValue[0].equals("eid")) {
                        podCastEpisodeId = keyValue[1];
                    }
                    if(keyValue[0].equals("pid")) {
                        podCastId = keyValue[1];
                    }
                }

               /* String[] pidKeyValue= andSplitted[0].split("&");

                if(pidKeyValue.length > 0 && pidKeyValue[0].equals("pid")){
                    podCastId = pidKeyValue[1];
                }

                String[] eidKeyValue = andSplitted[1].split("="); // =

                if(eidKeyValue.length == 2 && eidKeyValue[0].equals("eid")){
                    podCastEpisodeId =  eidKeyValue[1];
                }*/

    }
        return new Parameters(podCastEpisodeId, podCastId);
    }

}

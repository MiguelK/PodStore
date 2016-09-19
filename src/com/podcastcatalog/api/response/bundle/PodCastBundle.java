package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCast;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCastBundle extends Bundle {

    private List<PodCast> podCasts;

    public PodCastBundle(String title, String description, String imageURL,List<PodCast> podCasts) {
        super(title, description, imageURL, BundleType.PodCast);
        this.podCasts = Collections.unmodifiableList(podCasts);
    }

    public List<PodCast> getPodCasts() {
        return podCasts;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder {
        private  String title;
        private  String description;
        private  String imageURL; //FIXME Many???
        private List<PodCast> podCasts = new ArrayList<>();

        public Builder title(String title) {
            this.title = StringUtils.trimToNull(title);
            return this;
        }

        public Builder description(String description) {
            this.description = StringUtils.trimToNull(description);
            return this;
        }

        public Builder imageURL(String imageURL) {
            this.imageURL = StringUtils.trimToNull(imageURL);
            return this;
        }
        public Builder podCast(PodCast podCast){
            this.podCasts.add(podCast);
            return this;
        }
        public Builder podCasts(List<PodCast> podCasts){
            this.podCasts.addAll(podCasts);
            return this;
        }

        public PodCastBundle build(){

            if (description == null) {
                throw new IllegalArgumentException("description is mandatory");
            }
            if (title == null) {
                throw new IllegalArgumentException("title is mandatory");
            }
            if (imageURL == null) {
                throw new IllegalArgumentException("imageURL is mandatory");
            }

            if (podCasts == null || podCasts.isEmpty()) {
                throw new IllegalArgumentException("No podCasts exists in this bundle");
            }

            podCasts.forEach(p->{
                if(p==null){
                    throw new IllegalArgumentException();
                } });


            return new PodCastBundle(title, description, imageURL,podCasts);
        }

    }
}

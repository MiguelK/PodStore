package com.podcastcatalog.api.response;

import com.podcastcatalog.api.response.bundle.BundleItem;
import com.podcastcatalog.api.response.bundle.BundleType;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCast extends BundleItem {
    //FIXME stars,relations, recentioner etc ???
    private final int id; //FIXME ? collectionId?
    private final String publisher; //Sveriges Radio
    private  String imag; //FIXME
    private final LocalDateTime createdDate;
    private final String feedURL; //Get all episodes from this url
    private final List<PodCastEpisode> podCastEpisodes;
    private final List<PodCastCategoryType> podCastCategories;

    private PodCast(int id, String title, String publisher, String description, LocalDateTime createdDate,
                    String feedURL, List<PodCastEpisode> podCastEpisodes, List<PodCastCategoryType> podCastCategories) {
        super(title, description, "Image 123",BundleType.PodCast);
        this.id = id;
        this.publisher = publisher;
        this.createdDate = createdDate;
        this.feedURL = feedURL;
        this.podCastEpisodes = Collections.unmodifiableList(podCastEpisodes);
        this.podCastCategories = Collections.unmodifiableList(podCastCategories);
    }

    public int getId() {
        return id;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getFeedURL() {
        return feedURL;
    }

    public List<PodCastEpisode> getPodCastEpisodes() {
        return podCastEpisodes;
    }

    public List<PodCastCategoryType> getPodCastCategories() {
        return podCastCategories;
    }

    @Override
    public String toString() {
        return "PodCast{" +
                ", publisher='" + publisher + '\'' +
                ", feedURL='" + feedURL + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", podCastEpisodes=" + podCastEpisodes.size() +
                ", podCastCategories=" + podCastCategories +
                '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {
        private int id;
        private String title;
        private String publisher;
        private String description;
        private LocalDateTime createdDate;
        private String feedURL;
        private List<PodCastEpisode> podCastEpisodes = new ArrayList<>();
        private List<PodCastCategoryType> podCastCategories = new ArrayList<>();

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = StringUtils.trimToNull(title);
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = StringUtils.trimToNull(publisher);
            return this;
        }

        public Builder description(String description) {
            this.description = StringUtils.trimToNull(description);
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder feedURL(String feedURL) {
            this.feedURL = StringUtils.trimToNull(feedURL);
            return this;
        }

        public Builder addPodCastEpisodes(List<PodCastEpisode> podCastEpisodes) {
            this.podCastEpisodes.addAll(podCastEpisodes);
            return this;
        }
        public Builder addPodCastEpisode(PodCastEpisode podCastEpisode) {
            this.podCastEpisodes.add(podCastEpisode);
            return this;
        }

        public Builder setPodCastCategories(List<PodCastCategoryType> podCastCategories) {
            this.podCastCategories = podCastCategories;
            return this;
        }

        public PodCast build() {
            if (publisher == null) {
                throw new IllegalArgumentException("publisher is mandatory");
            }
            if (createdDate == null) {
                throw new IllegalArgumentException("createdDate is mandatory");
            }
            if (description == null) {
                throw new IllegalArgumentException("description is mandatory");
            }
            if (title == null) {
                throw new IllegalArgumentException("title is mandatory");
            }
            if (feedURL == null) {
                throw new IllegalArgumentException("feedURL is mandatory");
            }
            if (id == 0 || id < 0) {
                throw new IllegalArgumentException("Invalid id " + id + " mus be > 0");
            }
            if (podCastEpisodes == null || podCastEpisodes.isEmpty()) {
                throw new IllegalArgumentException("Invalid addPodCastEpisodes");
            }
            if (podCastCategories == null || podCastCategories.isEmpty()) {
                throw new IllegalArgumentException("Invalid podCastCategories");
            }


            return new PodCast(id, title, publisher, description, createdDate, feedURL, podCastEpisodes, podCastCategories);
        }

        public boolean isValid() {

            try {
                build();
            }catch (Exception e){
                    return false;
            }

            return true;
        }
    }
}

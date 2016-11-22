package com.podcastcatalog.api.response;

import com.podcastcatalog.api.response.bundle.BundleItem;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCast extends BundleItem {
    //FIXME stars,relations, recentioner etc ???
    private final String collectionId;
    private final String publisher; //Sveriges Radio
    private final LocalDateTime createdDate;
    private final String feedURL; //Get all episodes from this url
    private final List<PodCastEpisode> podCastEpisodes;
    private final List<PodCastCategoryType> podCastCategories;

    private PodCast(String collectionId, String title, String publisher, String description, LocalDateTime createdDate,
                    String feedURL, List<PodCastEpisode> podCastEpisodes, List<PodCastCategoryType> podCastCategories,
                    String artworkUrl600) {
        super(title, description, artworkUrl600);
        this.collectionId = collectionId;
        this.publisher = publisher;
        this.createdDate = createdDate;
        this.feedURL = feedURL;
        this.podCastEpisodes = Collections.unmodifiableList(podCastEpisodes);
        this.podCastCategories = Collections.unmodifiableList(podCastCategories);
    }

    public String getCollectionId() {
        return collectionId;
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
        private String collectionId;
        private String title;
        private String publisher;
        private String description;
        private LocalDateTime createdDate;
        private String feedURL;
        private String artworkUrl600;
        private final List<PodCastEpisode> podCastEpisodes = new ArrayList<>();
        private List<PodCastCategoryType> podCastCategories = new ArrayList<>();

        public Builder collectionId(String collectionId) {
            this.collectionId = StringUtils.trimToNull(collectionId);
            return this;
        }

        public Builder setArtworkUrl600(String artworkUrl600) {
            this.artworkUrl600 = StringUtils.trimToNull(artworkUrl600);
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
            if (collectionId == null) {
                throw new IllegalArgumentException("Invalid collectionId null ");
            }
            if (podCastEpisodes.isEmpty()) {
                throw new IllegalArgumentException("Invalid addPodCastEpisodes");
            }
            if (podCastCategories == null || podCastCategories.isEmpty()) {
                throw new IllegalArgumentException("Invalid podCastCategories");
            }

            return new PodCast(collectionId, title, publisher, description, createdDate, feedURL, podCastEpisodes, podCastCategories, artworkUrl600);
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

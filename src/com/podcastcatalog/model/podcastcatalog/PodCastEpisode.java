package com.podcastcatalog.model.podcastcatalog;


import com.podcastcatalog.util.IdGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

public class PodCastEpisode extends BundleItem implements Serializable {
    private final String id;
    private final String targetURL;

    private final PodCastEpisodeFileSize fileSize; //Optional can be null
    private final PodCastEpisodeType podCastType;
    private final LocalDateTime createdDate;//publishedDateTime //FIXME Rename?
    private final PodCastEpisodeDuration duration; //Optional can be null
    //private final int stars; //0-5 0==now votes yet //FIXME

    public static class SortCreatedDateLatestFirst implements Comparator<PodCastEpisode> {

        @Override
        public int compare(PodCastEpisode object1, PodCastEpisode object2) {
            return object2.createdDate.compareTo(object1.createdDate);
        }
    }


    private final String podCastCollectionId; //PodCast that contains this episode, used when subscribing after direct play

    //FIXME Remove id,
    private PodCastEpisode(String id, String title, String description, String targetURL, PodCastEpisodeDuration duration,
                           PodCastEpisodeFileSize fileSize, PodCastEpisodeType podCastType, LocalDateTime createdDate,
                           String podCastCollectionId) {
        super(title, description, null/*Client is using image form parent PodCast */);
        this.id = IdGenerator.generate(title, podCastCollectionId);
        this.podCastCollectionId = podCastCollectionId;
        this.targetURL = targetURL;
        this.fileSize = fileSize;
        this.podCastType = podCastType;
        this.createdDate = createdDate;
        this.duration = duration;
    }

    public String getPodCastCollectionId() {
        return podCastCollectionId;
    }

    public PodCastEpisodeDuration getDuration() {
        return duration;
    }

    public PodCastEpisodeFileSize getFileSize() {
        return fileSize;
    }

    public String getId() {
        return IdGenerator.generate(super.getTitle(), podCastCollectionId); //id; //FIXME remove
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodCastEpisode that = (PodCastEpisode) o;

        if (!getTitle().equals(that.getTitle())) return false;
        return targetURL.equals(that.targetURL);

    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + targetURL.hashCode();
        return result;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public PodCastEpisodeType getPodCastType() {
        return podCastType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "PodCastEpisode{" +
                "id=" + id +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", targetURL='" + targetURL + '\'' +
                ", duration=" + duration +
                ", fileSize=" + fileSize +
                ", podCastCollectionId=" + podCastCollectionId +
                ", podCastType=" + podCastType +
                ", createdDate=" + createdDate +
                '}';
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String podCastCollectionId;
        private String title;
        private String description;
        private String targetURL;
        private PodCastEpisodeDuration duration; //Optional?
        private PodCastEpisodeFileSize fileSize; //Optional?
        private PodCastEpisodeType podCastType;
        private LocalDateTime createdDate;

        public Builder targetURL(String targetURL) {
            this.targetURL = StringUtils.trimToNull(targetURL);
            return this;
        }

        public Builder description(String description) {
            this.description = StringUtils.trimToNull(description);
            return this;
        }

        public Builder title(String title) {
            this.title = StringUtils.trimToNull(title);
            return this;
        }

        public Builder duration(PodCastEpisodeDuration duration) {
            this.duration = duration;
            return this;
        }

        public Builder fileSizeInMegaByte(PodCastEpisodeFileSize fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder podCastCollectionId(String podCastCollectionId) {
            this.podCastCollectionId = StringUtils.trimToNull(podCastCollectionId);
            return this;
        }

        public Builder podCastType(PodCastEpisodeType podCastType) {
            this.podCastType = podCastType;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public boolean isValid() {
            try {
                build();
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        public PodCastEpisode build() {
            if (podCastType == null) {
                throw new IllegalArgumentException("Invalid podCastType null");
            }
            if (description == null) {
                throw new IllegalArgumentException("description is mandatory");
            }
            if (title == null) {
                throw new IllegalArgumentException("title is mandatory");
            }
            if (targetURL == null) {
                throw new IllegalArgumentException("targetURL is mandatory");
            }

            if (createdDate == null) {
                throw new IllegalArgumentException("createdDate is mandatory");
            }

            if (podCastCollectionId == null) {
                throw new IllegalArgumentException("podCastCollectionId is null");
            }

            return new PodCastEpisode(null, title, description, targetURL, duration, fileSize, podCastType, createdDate, podCastCollectionId);
        }
    }
}

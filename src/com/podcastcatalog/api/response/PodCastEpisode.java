package com.podcastcatalog.api.response;

import com.podcastcatalog.api.response.bundle.BundleItem;
import com.podcastcatalog.api.response.bundle.BundleType;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PodCastEpisode extends BundleItem implements Serializable{
    private final int id; //FIXME ? episode-id?
    private final String targetURL;

    private final PodCastEpisodeFileSize fileSize; //Optional can be null
    private final PodCastEpisodeType podCastType;
    private final LocalDateTime createdDate;
    private final PodCastEpisodeDuration duration; //Optional can be null

    private final int podCastId; //PodCast that contains this episode, used when subscribing after direct play

    private PodCastEpisode(int id, String title, String description, String targetURL,PodCastEpisodeDuration duration,
                           PodCastEpisodeFileSize fileSize, PodCastEpisodeType podCastType, LocalDateTime createdDate,int podCastId) {

        super(title, description, "Image URL", BundleType.Episode);
        this.id = id;
        this.podCastId = podCastId;
        this.targetURL = targetURL;
        this.fileSize= fileSize;
        this.podCastType = podCastType;
        this.createdDate = createdDate;
        this.duration = duration;
    }

    public int getPodCastId() {
        return podCastId;
    }

    public PodCastEpisodeDuration getDuration() {
        return duration;
    }

    public PodCastEpisodeFileSize getFileSize() {
        return fileSize;
    }

    public int getId() {
        return id;
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
                ", podCastType=" + podCastType +
                ", createdDate=" + createdDate +
                '}';
    }



    public static Builder newBuilder(){
        return new Builder();
    }



    public static class Builder{
        private int id; //FIXME ? episode-id?
        private int podCastId;
        private String title;
        private String description;
        private String targetURL;
        private PodCastEpisodeDuration duration; //Optional?
        private PodCastEpisodeFileSize fileSize; //Optional?
        private PodCastEpisodeType podCastType;
        private LocalDateTime createdDate;

        public Builder targetURL(String targetURL){
            this.targetURL = StringUtils.trimToNull(targetURL);
            return this;
        }
        public Builder description(String description){
            this.description = StringUtils.trimToNull(description);
            return this;
        }

        public Builder id(int id) {
            this.id = id;
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

        public Builder podCastId(int podCastId) {
            this.podCastId = podCastId;
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
            try{
                build();
            }catch (Exception e){
                return  false;
            }

            return true;
        }

        public PodCastEpisode build(){
            if(podCastType==null){
                throw new IllegalArgumentException("Invalid podCastType null");
            }
            if(description==null){
                throw new IllegalArgumentException("description is mandatory");
            }
            if(title==null){
                throw new IllegalArgumentException("title is mandatory");
            }
            if(targetURL==null){
                throw new IllegalArgumentException("targetURL is mandatory");
            }
            if(createdDate==null){
                throw new IllegalArgumentException("createdDate is mandatory");
            }
            if(id==0 || id<0){
                throw  new IllegalArgumentException("Invalid id " + id + " mus be > 0");
            }

            if(podCastId<=0l){
                throw new IllegalArgumentException("podCastId not set for this Episode");
            }

            return new PodCastEpisode(id,title,description,targetURL,duration,fileSize,podCastType, createdDate,podCastId);
        }
    }
}

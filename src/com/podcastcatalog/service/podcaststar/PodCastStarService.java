package com.podcastcatalog.service.podcaststar;

import com.podcastcatalog.service.ServiceDataStorageDisk;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PodCastStarService {

    private final static Logger LOG = Logger.getLogger(PodCastStarService.class.getName());

    private static final PodCastStarService INSTANCE = new PodCastStarService();
    private static final int MAX_STARS = 5;
    private static final int MIN_STARS = 1;

    private Map<String, PodCastStar> starsByPodCastId = new HashMap<>();

    public static PodCastStarService getInstance() {
        return INSTANCE;
    }

    public void load(ServiceDataStorageDisk serviceDataStorageDisk) {
        //FIXME load subscriptionData from disk async
        LOG.info("FIXME Loading starred podCasts from disk...");
        starsByPodCastId = new HashMap<>(); //FIXME
    }

    public void starPodCast(String podCastId, String episodeId, int stars) {
        if (stars < MIN_STARS || stars> MAX_STARS) {
            throw new IllegalArgumentException("Invalid stars " + stars);
        }

        String podIdTrimmed = StringUtils.trimToNull(podCastId);
        if (podIdTrimmed == null) {
            throw new IllegalArgumentException("Invalid podCastId=" + podCastId);
        }

        String episodeIdTrimmed = StringUtils.trimToNull(episodeId);
        if (episodeIdTrimmed == null) {
            throw new IllegalArgumentException("Invalid episodeId=" + episodeId);
        }

        starsByPodCastId.putIfAbsent(podIdTrimmed,new PodCastStar());

        PodCastStar podCastStar = starsByPodCastId.get(podIdTrimmed);


//        PodCastStar podCastStar = starsByPodCastId.getOrDefault(podCastId, new PodCastStar());

        podCastStar.starEpisode(episodeIdTrimmed, stars);
    }

    public int getStarsForEpisodeId(String podCastId, String episodeId) {

        PodCastStar podCastStar = starsByPodCastId.get(StringUtils.trimToEmpty(podCastId));
        if (podCastStar == null) {
            return 0;
        }

        return podCastStar.getStarsForEpisode(StringUtils.trimToEmpty(episodeId));
    }

    private static class PodCastStar {
        private Map<String, PodCastEpisodeStar> podCastEpisodeStars = new HashMap<>();

        PodCastStar() {
        }

        void starEpisode(String episodeId, int stars) {

            podCastEpisodeStars.putIfAbsent(episodeId, new PodCastEpisodeStar());
            PodCastEpisodeStar castEpisodeStar = podCastEpisodeStars.get(episodeId);

            castEpisodeStar.increaseStars(stars);
        }

        public int getStarsForEpisode(String episodeId) {

            PodCastEpisodeStar podCastEpisodeStar = podCastEpisodeStars.get(episodeId);
            if (podCastEpisodeStar == null) {
                return 0;
            }

            return podCastEpisodeStar.getStars();
        }
    }

    private static class PodCastEpisodeStar {

        private int totalStars;
        private int totalStarCounts;
        private int stars;

        void increaseStars(int stars) {
            totalStars += stars;
            totalStarCounts = totalStarCounts + 1;
            if(totalStarCounts>=10){
                this.stars = totalStars / totalStarCounts;
            }
        }

        public int getStars() {
            return stars;
        }
    }
}

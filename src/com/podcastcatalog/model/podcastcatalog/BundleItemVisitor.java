package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;

import java.util.*;

public class BundleItemVisitor implements Visitor{

    private final Set<PodCastEpisode> podCastEpisodes = new HashSet<>();
    private final Set<PodCast> podCasts = new HashSet<>();

    @Override
    public void visit(PodCast podCast) {

        List<PodCastEpisode> podCastEpisodes = podCast.getPodCastEpisodesInternal();
        setArtworkURL(podCast.getArtworkUrl600(), podCastEpisodes);

        this.podCastEpisodes.addAll(podCastEpisodes);
        podCasts.add(podCast);
    }

    @Override
    public void visit(PodCastCategory podCastCategory) {

        for (PodCast podCast : podCastCategory.getPodCasts()) {
            List<PodCastEpisode> podCastEpisodes = podCast.getPodCastEpisodesInternal();
            setArtworkURL(podCast.getArtworkUrl600(), podCastEpisodes);

            this.podCastEpisodes.addAll(podCastEpisodes);
            podCasts.add(podCast);
        }
    }

    @Override
    public void visit(PodCastEpisode podCastEpisode) {

        Optional<PodCast> podCastById = PodCastCatalogService.getInstance().getPodCastById(podCastEpisode.getPodCastCollectionId());

        String artworkURL = "invalid url"; //Will trigger placeholder image in search result
        if(podCastById.isPresent()){
            PodCast podCast = podCastById.get();
            artworkURL = podCast.getArtworkUrl600();
        }

        podCastEpisode.setArtworkUrl600(artworkURL);

        podCastEpisodes.add(podCastEpisode);
    }

    private void setArtworkURL(String artworkURL, List<PodCastEpisode> podCastEpisodes){

        for (PodCastEpisode podCastEpisode : podCastEpisodes) {
            podCastEpisode.setArtworkUrl600(artworkURL);
        }

    }

    public List<PodCastEpisode> getPodCastEpisodes() {
        return new ArrayList<>(podCastEpisodes);
    }

    public List<PodCast> getPodCasts() {
        return new ArrayList<>(podCasts);
    }
}

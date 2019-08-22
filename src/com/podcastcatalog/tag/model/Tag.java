package com.podcastcatalog.tag.model;

import java.util.List;

/**
 * Created by miguelkrantz on 2019-08-21.
 */
public class Tag {
    String name;
    List<PodCastEpisodeID> ids;

    public Tag(String name, List<PodCastEpisodeID> ids) {
        this.name = name;
        this.ids = ids;
    }
}

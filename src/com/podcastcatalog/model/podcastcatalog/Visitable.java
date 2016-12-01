package com.podcastcatalog.model.podcastcatalog;

public interface Visitable {
    void accept(Visitor visitor);
}

package com.podcastcatalog.api.response;

public interface Visitable {
    void accept(Visitor visitor);
}

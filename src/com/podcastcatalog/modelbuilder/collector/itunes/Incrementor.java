package com.podcastcatalog.modelbuilder.collector.itunes;

import java.util.concurrent.RecursiveTask;


class Incrementor extends RecursiveTask<Integer> {
    int theNumber;

    Incrementor(int x) {
        theNumber = x;
    }

    public Integer compute() {
        return theNumber + 1;
    }
}

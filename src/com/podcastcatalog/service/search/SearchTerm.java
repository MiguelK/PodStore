package com.podcastcatalog.service.search;

/**
 * Created by miguelkrantz on 2018-05-11.
 */
public class SearchTerm implements Comparable<SearchTerm>  {

    private int counter;
    private String term;

    public SearchTerm(int counter, String term) {
        this.counter = counter;
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public void increaseCounter() {
        this.counter = counter + 1;
    }

    @Override
    public int compareTo(SearchTerm o) {
        if(this.counter==o.counter){
            return 0;
        }
        else if(this.counter < o.counter){
            return 1;
        }
        else{
            return -1;
        }
    }
}


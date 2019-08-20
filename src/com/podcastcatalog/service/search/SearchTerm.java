package com.podcastcatalog.service.search;

/**
 * Created by miguelkrantz on 2018-05-11.
 */
public class SearchTerm implements Comparable<SearchTerm>  {

    private int counter;
    private String term;
    private String pid;

    public SearchTerm(int counter, String term, String pid) {
        this.counter = counter;
        this.term = term;
        this.pid = pid;
    }

    public int getCounter() {
        return counter;
    }

    public String getPid() {
        return pid;
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


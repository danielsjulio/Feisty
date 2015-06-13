package com.feisty.model;

/**
 * Created by Gil on 08/06/15.
 */
public class RestContainer {

    public String kind;
    public PageInfo pageInfo;

    public class PageInfo {
        public int totalResults;

        public int resultsPerPage;
    }
}

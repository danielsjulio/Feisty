package com.feisty.model;

/**
 * Created by Gil on 08/06/15.
 */
public class RestContainer {

    String kind;

    PageInfo pageInfo;

    public String getKind() {
        return kind;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    class PageInfo {
        int totalResults;

        int resultsPerPage;

        public int getTotalResults() {
            return totalResults;
        }

        public int getResultsPerPage() {
            return resultsPerPage;
        }
    }
}

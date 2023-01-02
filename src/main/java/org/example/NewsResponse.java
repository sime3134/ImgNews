package org.example;

import java.util.ArrayList;

/**
 * This class represents a response from the News API.
 */
public class NewsResponse {
    private String status;
    private String totalResults;
    private ArrayList<OriginalArticle> articles;

    public OriginalArticle getArticle(int index){
        if(index >= articles.size()) throw new IllegalArgumentException("Index out of bounds");
        return articles.get(index);
    }

    @Override
    public String toString() {
        return "OriginalNewsResponse{" +
                "status='" + status + '\'' +
                ", totalResults='" + totalResults + '\'' +
                ", articles=" + articles +
                '}';
    }

    public int getNumberOfArticles() {
        return articles.size();
    }
}

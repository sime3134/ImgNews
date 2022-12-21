package org.example;

import java.util.ArrayList;

public class NewsResponse {
    private String status;
    private String totalResults;
    private ArrayList<OriginalArticle> articles;

    public OriginalArticle getFirstArticle(){
        return articles.get(0);
    }

    @Override
    public String toString() {
        return "OriginalNewsResponse{" +
                "status='" + status + '\'' +
                ", totalResults='" + totalResults + '\'' +
                ", articles=" + articles +
                '}';
    }
}

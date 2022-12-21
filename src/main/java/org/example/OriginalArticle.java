package org.example;

import java.util.Arrays;

public class OriginalArticle {
    private String title;
    private String name;
    private String author;
    private String description;
    private String content;

    private String category;
    private OriginalArticleSource source;
    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public OriginalArticle setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String[] getFusedDescAndContentAsArray(int limit){
        String fused = description + " " + content;
        String[] array = fused.split("[.]", limit);
        System.out.println(Arrays.toString(array));
        return array;
    }

    public String getFusedDescAndContent(int limit){
        return description + " " + content;
    }

    public String getContent() {
        return content;
    }

    public OriginalArticleSource getSource() {
        return source;
    }

    public OriginalArticle(){

    }

    @Override
    public String toString() {
        return "OriginalArticle{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", source=" + source +
                '}';
    }
}

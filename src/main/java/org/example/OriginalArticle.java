package org.example;

public class OriginalArticle {
    private String title;
    private String name;
    private String author;
    private String description;
    private String content;
    private OriginalArticleSource source;
    public String getTitle() {
        return title;
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

package org.example;

/**
 * This class represents the original article from the NEWS API.
 */
public class OriginalArticle {

    private transient int index;
    private String title;
    private String name;
    private String author;
    private String description;
    private String content;
    private String category;
    private OriginalArticleSource source;
    private int numberOfTries;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String[] getContentAsArray(int limit) {
        return content.split("[.,]", limit);
    }

    public OriginalArticleSource getSource() {
        return source;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public OriginalArticle setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
        return this;
    }

    @Override
    public String toString() {
        return "OriginalArticle{" +
                "title='" + title +
                '}';
    }
}

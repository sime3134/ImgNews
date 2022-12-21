package org.example;

import java.util.List;

public class ImgArticle {
    private static int nextId;
    private final int id;
    private String headingImgUrl;
    private String preambleImgUrl;
    private String city;
    private String country;
    private String category;

    private OriginalArticle originalArticle;

    public ImgArticle(OriginalArticle originalArticle, List<GeneratedImage> generatedImages){
        id = nextId;
        nextId++;
        this.originalArticle = originalArticle;
        if(generatedImages.size() < 2){
            throw new IllegalArgumentException("Generated images array must have two elements");
        }
        this.headingImgUrl = generatedImages.get(0).getUrl();
        this.preambleImgUrl = generatedImages.get(1).getUrl();
    }


    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", headingImg='" + headingImgUrl + '\'' +
                ", preambleImg='" + preambleImgUrl + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}

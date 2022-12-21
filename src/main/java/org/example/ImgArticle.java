package org.example;

import java.util.ArrayList;
import java.util.List;

public class ImgArticle {
    private static int nextId;
    private final int id;
    private List<String> imgUrls;
    private OriginalArticle originalArticle;

    public ImgArticle(OriginalArticle originalArticle, List<GeneratedImage> generatedImages){
        id = nextId;
        nextId++;
        this.originalArticle = originalArticle;
        imgUrls = new ArrayList<>();
        for(GeneratedImage img : generatedImages){
            imgUrls.add(img.getUrl());
        }
        if(imgUrls.isEmpty()) throw new IllegalArgumentException("Generated images list is empty");
    }


    @Override
    public String toString() {
        return "ImgArticle{" +
                "id=" + id +
                ", imgUrls=" + imgUrls +
                ", originalArticle=" + originalArticle +
                '}';
    }
}

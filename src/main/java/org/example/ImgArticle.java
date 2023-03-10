package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the finished article made out of images.
 */
public class ImgArticle {
    private static int nextId;
    private final int id;
    private OriginalArticle originalArticle;

    private List<String> imgData;

    public OriginalArticle getOriginalArticle() {
        return originalArticle;
    }

    public int getId() {
        return id;
    }

    public ImgArticle(OriginalArticle originalArticle, List<GeneratedImage> generatedImages){
        id = nextId;
        nextId++;
        this.originalArticle = originalArticle;
        imgData = new ArrayList<>();
        for(GeneratedImage img : generatedImages){
            imgData.add(img.getData());
        }
        if(imgData.isEmpty()) throw new IllegalArgumentException("Generated images list is empty");
    }


    @Override
    public String toString() {
        return "ImgArticle{" +
                "id=" + id +
                ", imgUrls=" + imgData +
                ", originalArticle=" + originalArticle +
                '}';
    }
}

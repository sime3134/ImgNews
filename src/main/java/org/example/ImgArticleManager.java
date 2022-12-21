package org.example;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImgArticleManager {
    private final Mapper mapper;
    private final HttpClient httpClient;
    private final List<ImgArticle> articles;

    private static final String imgSize = "512x512";

    public ImgArticleManager(){
        articles = new ArrayList<>();
        mapper = new Mapper();
        httpClient = new HttpClient();
    }

    public void prepareArticles() {
        OriginalArticle originalArticle = getNews();
        createImgArticles(originalArticle, getGeneratedImages(originalArticle));
    }

    private void createImgArticles(OriginalArticle originalArticle, List<GeneratedImage> generatedImages) {
        ImgArticle imgArticle = new ImgArticle(originalArticle, generatedImages);
        articles.add(imgArticle);
    }

    private List<GeneratedImage> getGeneratedImages(OriginalArticle originalArticle) {
        ArrayList<GeneratedImage> images = new ArrayList<>();
        images.add(getImageFromDalle(originalArticle.getTitle()));
        images.add(getImageFromDalle(originalArticle.getDescription()));

        for(GeneratedImage image : images){
            System.out.println(image.getUrl());
        }

        return images;
    }

    @NotNull
    private GeneratedImage getImageFromDalle(String searchPrompt) {
        String json = "";
        System.out.println(searchPrompt);
        DallePrompt dallePrompt = new DallePrompt(searchPrompt, 1, imgSize);
        String jsonRequest = mapper.toJsonString(dallePrompt, DallePrompt.class);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .addHeader("Authorization", "Bearer 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")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                .build();
        try{
            json = httpClient.post(request);
        }catch(Exception e){
            e.printStackTrace();
        }

        DalleResponse dalleResponse = mapper.fromJsonString(json, DalleResponse.class);

        return dalleResponse.getData()[0];
    }

    private OriginalArticle getNews() {
        String json = "";
        Request request = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=se")
                .addHeader("Accept", "application/json")
                .addHeader("X-Api-Key", "7c34b62c6d744276a8b8c83a2875fc9f")
                .build();
        try {
            json = httpClient.get(request);
        }catch(Exception e){
            e.printStackTrace();
        }

        NewsResponse responseObj = mapper.fromJsonString(json, NewsResponse.class);

        return responseObj.getFirstArticle();
    }

    public String getArticlesAsJsonString() {
        return mapper.toJsonString(articles, List.class);
    }
}

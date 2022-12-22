package org.example;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImgArticleManager {
    private final Mapper mapper;
    private final Blacklist blacklist;

    private final KeyHandler keyHandler;
    private final HttpClient httpClient;
    private final List<ImgArticle> articles;

    private static final String[] ARTICLES_TO_GET_BY_ARTICLE = { "general", "entertainment", "sports" };
    private static final int IMAGES_PER_ARTICLE = 5;

    private static final String TITLE_IMG_SIZE = "256x256";
    private static final String OTHER_IMGS_SIZE = "256x256";

    public ImgArticleManager(){
        articles = new ArrayList<>();
        mapper = new Mapper();
        keyHandler = new KeyHandler();
        httpClient = new HttpClient();
        blacklist = new Blacklist();
    }

    public void prepareArticles() {
        for(String category : ARTICLES_TO_GET_BY_ARTICLE){
            OriginalArticle originalArticle = getNews(category);
            articles.add(createImgArticles(originalArticle, getGeneratedImages(originalArticle)));
        }
    }

    private ImgArticle createImgArticles(OriginalArticle originalArticle, List<GeneratedImage> generatedImages) {
        return new ImgArticle(originalArticle, generatedImages);
    }

    private List<GeneratedImage> getGeneratedImages(OriginalArticle originalArticle) {
        System.out.println(originalArticle);
        ArrayList<GeneratedImage> images = new ArrayList<>();
        images.add(getImageFromDalle(TITLE_IMG_SIZE, originalArticle.getTitle()));
        for(int i = 0; i < IMAGES_PER_ARTICLE; i++){
            images.add(getImageFromDalle(OTHER_IMGS_SIZE,
                    originalArticle.getFusedDescAndContentAsArray(IMAGES_PER_ARTICLE)[i]));
        }

        for(GeneratedImage image : images){
            System.out.println(image.getUrl());
        }

        return images;
    }

    @NotNull
    private GeneratedImage getImageFromDalle(String imgSize, String searchPrompt) {
        String json = "";
        searchPrompt = searchPrompt.replaceAll("[:]", "");
        System.out.println(searchPrompt);
        DallePrompt dallePrompt = new DallePrompt(searchPrompt, 1, imgSize);
        String jsonRequest = mapper.toJsonString(dallePrompt, DallePrompt.class);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .addHeader("Authorization", keyHandler.get("openai"))
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

    private OriginalArticle getNews(String category) {
        int articleIndex = 0;
        OriginalArticle article = null;
        String json = "";

        Request request = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=se&category=" + category)
                .addHeader("Accept", "application/json")
                .addHeader("X-Api-Key", keyHandler.get("newsapi"))
                .build();
        try {
            json = httpClient.get(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NewsResponse responseObj = mapper.fromJsonString(json, NewsResponse.class);
        System.out.println(responseObj);
        boolean blacklisted = true;
        while(blacklisted){
            if(responseObj.getNumberOfArticles() > articleIndex) {
                article = responseObj.getArticle(articleIndex);
                blacklisted = blacklist.containsBlacklisted(article, IMAGES_PER_ARTICLE);
                articleIndex++;
            }else{
                getNews(category);
            }
        }

        return article.setCategory(category);
    }

    public String getArticlesAsJsonString() {
        return mapper.toJsonString(articles, List.class);
    }

    public int numberOfArticles() {
        return articles.size();
    }
}

package org.example;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImgArticleManager {
    private final Mapper mapper;
    private final KeyHandler keyHandler;
    private final HttpClient httpClient;
    private final List<ImgArticle> articles;

    //public static final String[] ARTICLES_TO_GET_BY_ARTICLE = { "general", "entertainment", "sports" };
    public static final String[] ARTICLES_TO_GET_BY_ARTICLE = { "general", "entertainment" };
    private static final int IMAGES_PER_ARTICLE = 1;

    private static final String TITLE_IMG_SIZE = "256x256";
    private static final String OTHER_IMGS_SIZE = "256x256";

    public ImgArticleManager(){
        articles = new ArrayList<>();
        mapper = new Mapper();
        keyHandler = new KeyHandler();
        httpClient = new HttpClient();
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
        ArrayList<GeneratedImage> images = new ArrayList<>();
        String[] contents = originalArticle.getContentAsArray(IMAGES_PER_ARTICLE);
        images.add(getImageFromDalle(TITLE_IMG_SIZE, originalArticle.getTitle()));
        for (String content : contents) {
            images.add(getImageFromDalle(OTHER_IMGS_SIZE, content));
        }

        return images;
    }

    @NotNull
    private GeneratedImage getImageFromDalle(String imgSize, String searchPrompt) {
        String json = "";
        System.out.println("        Generating image from prompt: " + searchPrompt);
        DalleImagePrompt dalleImagePrompt = new DalleImagePrompt(searchPrompt, 1, imgSize);
        String jsonRequest = mapper.toJsonString(dalleImagePrompt, DalleImagePrompt.class);

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

        DalleImageResponse dalleImageResponse = mapper.fromJsonString(json, DalleImageResponse.class);

        return dalleImageResponse.getData()[0];
    }

    private OriginalArticle getNews(String category) {
        int articleIndex = -1;
        OriginalArticle article = null;
        String json = "";

        Request request = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=us&category=" + category)
                .addHeader("Accept", "application/json")
                .addHeader("X-Api-Key", keyHandler.get("newsapi"))
                .build();
        try {
            json = httpClient.get(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NewsResponse responseObj = mapper.fromJsonString(json, NewsResponse.class);
        boolean blacklisted = true;
        while(blacklisted){
            articleIndex++;
            if(responseObj.getArticle(articleIndex).getContent() == null) continue;
            if(responseObj.getNumberOfArticles() > articleIndex) {
                article = responseObj.getArticle(articleIndex);
                System.out.println("Checking article: " + article.getTitle());
                blacklisted = checkArticleWithDalle(article);
            }else{
                getNews(category);
            }
        }

        return article.setCategory(category);
    }

    private boolean checkArticleWithDalle(OriginalArticle article) {
        String json = "";
        DalleCompletionPrompt dalleCompletionPrompt = new DalleCompletionPrompt(article);
        String jsonRequest = mapper.toJsonString(dalleCompletionPrompt, DalleCompletionPrompt.class);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", keyHandler.get("openai"))
                .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                .build();
        try {
            json = httpClient.post(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DalleCompletionResponse dalleCompletionResponse = mapper.fromJsonString(json, DalleCompletionResponse.class);
        System.out.println("Contains names of public figures or violent content? Dalle says:"
                + dalleCompletionResponse.getChoice().getText() + "\n");
        return !dalleCompletionResponse.getChoice().getText().contains("No, ");
    }



    public String getArticlesAsJsonString() {
        return mapper.toJsonString(articles, List.class);
    }

    public String getArticleByIdAsJsonString(int id) {
        Optional<ImgArticle> article = articles.stream().filter(art -> art.getId() == id).findAny();
        return article.map(imgArticle -> mapper.toJsonString(imgArticle, ImgArticle.class)).orElse(null);
    }

    public String getArticlesAsJsonString(String category) {
        List<ImgArticle> articlesCategory =
                articles.stream().filter(article -> article.getOriginalArticle().getCategory().equals(category)).toList();
        if(!articlesCategory.isEmpty()) {
            return mapper.toJsonString(articlesCategory, List.class);
        }else{
            return null;
        }
    }

    public int numberOfArticles() {
        return articles.size();
    }
}

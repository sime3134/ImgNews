package org.example;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Create and store all ImgArticles.
 * All API calls to other API:s are also handled here.
 */
public class ImgArticleManager {
    private final Mapper mapper;
    private final KeyHandler keyHandler;
    private final HttpClient httpClient;
    private final List<ImgArticle> articles;

    //public static final String[] articleToGetByCategory = { "general", "entertainment", "sports" };
    public final String[] articleToGetByCategory = { "general" };
    private final int imgsPerArticle = 1;
    private final String imgSize = "256x256";

    private int totalNumberofTries;

    public ImgArticleManager(){
        articles = new ArrayList<>();
        mapper = new Mapper();
        keyHandler = new KeyHandler();
        httpClient = new HttpClient();
        totalNumberofTries = 0;
    }

    public void prepareArticles() {
        for(String category : articleToGetByCategory){
            OriginalArticle originalArticle = getNews(category);
            articles.add(createImgArticles(originalArticle, getGeneratedImages(originalArticle)));
        }
    }

    private ImgArticle createImgArticles(OriginalArticle originalArticle, List<GeneratedImage> generatedImages) {
        return new ImgArticle(originalArticle, generatedImages);
    }

    private List<GeneratedImage> getGeneratedImages(OriginalArticle originalArticle) {
        ArrayList<GeneratedImage> images = new ArrayList<>();
        String[] contents = originalArticle.getContentAsArray(imgsPerArticle);
        images.add(getImageFromDalle(originalArticle.getTitle()));
        for (String content : contents) {
            images.add(getImageFromDalle(content));
        }

        return images;
    }

    /**
     * Requests 1 image from the OPENAI API.
     * @param searchPrompt The text prompt to generate the image from.
     * @return The generated image.
     */
    private GeneratedImage getImageFromDalle(String searchPrompt) {
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

    /**
     * Requests 1 article from the NEWS API. If a retrieved article contains anything
     * related to violence, celebrities or COVID-19 a new article will be retrieved.
     * See {@link #checkArticleWithDalle(OriginalArticle)} for details about this.
     * @param category The category of the article to retrieve.
     * @return The retrieved article.
     */
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
        int numberOfTriesThisArticle = 0;
        boolean blacklisted = true;
        while(blacklisted){
            articleIndex++;
            totalNumberofTries++;
            numberOfTriesThisArticle++;
            if(responseObj.getArticle(articleIndex).getContent() == null) continue;
            if(responseObj.getNumberOfArticles() > articleIndex) {
                article = responseObj.getArticle(articleIndex);
                System.out.println("Checking article: " + article.getTitle());
                blacklisted = checkArticleWithDalle(article);
            }else{
                getNews(category);
            }
        }

        return article.setCategory(category).setNumberOfTries(numberOfTriesThisArticle);
    }

    /**
     * Asks the OPENAI API if the article contains anything related to violence, celebrities or COVID-19.
     * @param article The article to check.
     * @return true if the article contains anything related to violence, celebrities or COVID-19, else false.
     */
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

    public int getTotalNumberofTries() {
        return totalNumberofTries;
    }

    public String getArticlesAsJsonString() {
        return mapper.toJsonString(articles, List.class);
    }

    public ImgArticle getArticleById(int id) {
        Optional<ImgArticle> article = articles.stream().filter(art -> art.getId() == id).findAny();
        return article.orElse(null);
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

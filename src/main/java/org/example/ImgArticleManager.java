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

    public static final String[] articleToGetByCategory = { "general", "sports", "entertainment"};
    private final int imgsPerArticle = 5;
    private final String imgSize = "256x256";

    private int totalNumberofTries;

    public ImgArticleManager(){
        articles = new ArrayList<>();
        mapper = new Mapper();
        keyHandler = new KeyHandler();
        httpClient = new HttpClient();
        totalNumberofTries = 0;
    }

    /**
     * Prepares 1 article per category.
     */
    public void prepareArticles() {
        for(String category : articleToGetByCategory){
            generateArticle(category, 0, 0);
        }
    }

    /**
     * Generates an article with the given category.
     * Will recursively call itself if the article is not valid.
     * @param category The category to query for.
     * @param startIndex The index to start at when going through the articles looking for a valid one.
     * @param startNumberOfTries The number of tries to start at when going through the articles looking for a
     *                           valid one. Only for analyzing.
     */
    private void generateArticle(String category, int startIndex, int startNumberOfTries){
        OriginalArticle originalArticle = getNews(category, startIndex, startNumberOfTries);
        List<GeneratedImage> images = getGeneratedImages(originalArticle);
        //If any of the images could not be generated, try again.
        if(images != null) {
            articles.add(createImgArticle(originalArticle, images));
        }else{
            generateArticle(category, originalArticle.getIndex()+1, originalArticle.getNumberOfTries());
        }
    }

    private ImgArticle createImgArticle(OriginalArticle originalArticle, List<GeneratedImage> generatedImages) {
        return new ImgArticle(originalArticle, generatedImages);
    }

    /**
     * Creates a list of generated images from the OpenAI API.
     * @param originalArticle The article to generate images from.
     * @return List of the generated images or null if any of the images could not be generated.
     */
    private List<GeneratedImage> getGeneratedImages(OriginalArticle originalArticle) {
        ArrayList<GeneratedImage> images = new ArrayList<>();
        String[] contents = originalArticle.getContentAsArray(imgsPerArticle);
        GeneratedImage titleImg = getImageFromDalle(originalArticle.getTitle());

        //If title image could not be generated, return null.
        if(titleImg == null) return null;
        else images.add(titleImg);

        //If any content images could not be generated, return null.
        for (String content : contents) {
            GeneratedImage img = getImageFromDalle(content);
            if(img != null)  images.add(img);
            else return null;
        }

        return images;
    }

    /**
     * Requests 1 image from the OPENAI API.
     * @param searchPrompt The text prompt to generate the image from.
     * @return The generated image or null if request fails.
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

        json = httpClient.post(request);

        if(json != null) {
            DalleImageResponse dalleImageResponse = mapper.fromJsonString(json, DalleImageResponse.class);

            return dalleImageResponse.getData()[0];
        }

        return null;
    }

    /**
     * Requests 1 article from the NEWS API. If a retrieved article contains anything
     * related to violence, celebrities or COVID-19 a new article will be retrieved.
     * See {@link #articleContainsBadContent(OriginalArticle)} for details about this.
     *
     * @param category           The category of the article to retrieve.
     * @param startIndex         The index to start grabbing articles from.
     * @param startNumberOfTries The number of tries to start at when going through the articles looking for a
     *                           valid one. Only for analyzing.
     * @return The retrieved article.
     */
    private OriginalArticle getNews(String category, int startIndex, int startNumberOfTries) {
        int articleIndex = startIndex - 1;
        OriginalArticle article = null;
        String json = "";

        Request request = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=us&category=" + category)
                .addHeader("Accept", "application/json")
                .addHeader("X-Api-Key", keyHandler.get("newsapi"))
                .build();

        json = httpClient.get(request);

        if(json == null){
            getNews(category, startIndex, startNumberOfTries);
        }

        NewsResponse responseObj = mapper.fromJsonString(json, NewsResponse.class);
        int numberOfTriesThisBatch = startNumberOfTries;
        boolean blacklisted = true;
        while(blacklisted){
            articleIndex++;
            totalNumberofTries++;
            numberOfTriesThisBatch++;
            if(exists(responseObj.getArticle(articleIndex).getTitle()) || responseObj.getArticle(articleIndex).getContent() == null) continue;
            if(responseObj.getNumberOfArticles() > articleIndex) {
                article = responseObj.getArticle(articleIndex);
                article.setIndex(articleIndex);
                System.out.println("Checking article: " + article.getTitle());
                blacklisted = articleContainsBadContent(article);
            }else{
                getNews(category, startIndex, numberOfTriesThisBatch);
            }
        }

        return article.setCategory(category).setNumberOfTries(numberOfTriesThisBatch);
    }

    /**
     * Asks the OPENAI API if the article contains anything related to violence, celebrities or COVID-19.
     * @param article The article to check.
     * @return true if the article contains anything related to violence, celebrities covid-19 or if the request
     * failed. Otherwise, false.
     */
    private boolean articleContainsBadContent(OriginalArticle article) {
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

        if(json != null) {
            DalleCompletionResponse dalleCompletionResponse = mapper.fromJsonString(json, DalleCompletionResponse.class);
            System.out.println("Contains names of public figures or violent content? Dalle says:"
                    + dalleCompletionResponse.getChoice().getText() + "\n");
            return !dalleCompletionResponse.getChoice().getText().contains("No, ");
        }else{
            return true;
        }
    }

    public boolean exists(String title){
        for(ImgArticle article : articles){
            if(article.getOriginalArticle().getTitle().equals(title)) return true;
        }
        return false;
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

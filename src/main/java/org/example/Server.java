package org.example;

import io.javalin.Javalin;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.HashMap;
import java.util.Map;

/**
 * Asks the ImgArticleManager to generate ImgArticles and serves them in JSON or HTML format.
 */
public class Server {

    private final ImgArticleManager articleManager;
    private final String apiPrefix = "/api/v1";

    public Server(){
        articleManager = new ImgArticleManager();
        articleManager.prepareArticles();
    }

    /**
     * Starts the server that both serves HTML templates with the thymeleaf library and
     * answers API replies with ImgArticles in the JSON format.
     */
    public void run() {
        Javalin app = Javalin.create(config -> {
            JavalinThymeleaf.init();
            config.plugins.enableCors(corsContainer -> {
                corsContainer.add(CorsPluginConfig::anyHost);
            });
            config.staticFiles.add("/public/static");
        });

        //API endpoints

        app.start(5000);

        //Serves all news or all news for a specific category if it is specified in a query.
        app.get(apiPrefix + "/news", ctx -> {
            String json;
            if(articleManager.numberOfArticles() > 0){
                if(ctx.queryParam("category") != null){
                    json = articleManager.getArticlesAsJsonString(ctx.queryParam("category"));
                    if(json == null){
                        throw new NotFoundResponse("Couldn't find the requested category");
                    }
                }else {
                    json = articleManager.getArticlesAsJsonString();
                }
            } else {
                throw new InternalServerErrorResponse();
            }
            ctx.header("Content-type", "application/json").json(json);
        });

        //Serves an article specified with an ID or a NotFoundResponse if not article was found.
        app.get(apiPrefix + "/news/{id}", ctx -> {
            if(articleManager.numberOfArticles() > 0){
                Integer id;
                try {
                    id = Integer.parseInt(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    throw new InternalServerErrorResponse("Couldn't parse the article ID");
                }

                String json = articleManager.getArticleByIdAsJsonString(id);
                if (json != null) {
                    ctx.header("Content-type", "application/json").json(json);
                } else {
                    throw new NotFoundResponse("Couldn't find the requested article");
                }
            } else {
                throw new InternalServerErrorResponse();
            }
        });

        //Templates

        app.get("/news/{id}", ctx -> {
            if(articleManager.numberOfArticles() > 0){
                Integer id = null;
                try {
                    id = Integer.parseInt(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                if(id != null) {
                    Map<String, String> model = new HashMap<>();
                    model.put("json", articleManager.getArticleByIdAsJsonString(id));
                    model.put("numberOfTries", String.valueOf(articleManager.getArticleById(id).getOriginalArticle().getNumberOfTries()));
                    ctx.render("/templates/news.html", model);
                }else{
                    throw new InternalServerErrorResponse();
                }
            } else {
                throw new InternalServerErrorResponse();
            }
        });

        app.get("/news", ctx -> {
            String json;
            String category = "empty";
            if(articleManager.numberOfArticles() > 0){
                if(ctx.queryParam("category") != null){
                    category = ctx.queryParam("category");
                    json = articleManager.getArticlesAsJsonString(ctx.queryParam("category"));
                    if(json == null){
                        throw new NotFoundResponse("Couldn't find the requested category");
                    }
                }else {
                    json = articleManager.getArticlesAsJsonString();
                }
            } else {
                throw new InternalServerErrorResponse();
            }
            Map<String, String> model = new HashMap<>();
            model.put("json", json);
            model.put("numberOfTries", String.valueOf(articleManager.getTotalNumberofTries()));
            model.put("generatedArticles", String.valueOf(articleManager.numberOfArticles()));
            model.put("category", category);
            ctx.render("/templates/index.html", model);
        });
    }
}

package org.example;

import io.javalin.Javalin;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinThymeleaf;

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
                    if(json != null) {
                        ctx.header("Content-type", "application/json").json(json);
                    }else{
                        throw new NotFoundResponse("Couldn't find the requested category");
                    }
                }else {
                    json = articleManager.getArticlesAsJsonString();
                    ctx.header("Content-type", "application/json").json(json);
                }
            } else {
                throw new InternalServerErrorResponse();
            }
        });

        //Serves an article specified with an ID or a NotFoundResponse if not article was found.
        app.get(apiPrefix + "/news/{id}", ctx -> {
            if(articleManager.numberOfArticles() > 0){
                Integer id = null;
                try {
                    id = Integer.parseInt(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                if(id != null) {
                    String json = articleManager.getArticleByIdAsJsonString(id);
                    if (json != null) {
                        ctx.header("Content-type", "application/json").json(json);
                    } else {
                        throw new NotFoundResponse("Couldn't find the requested article");
                    }
                }else{
                    throw new InternalServerErrorResponse();
                }
            } else {
                throw new InternalServerErrorResponse();
            }
        });

        //Templates

        app.get("/news", ctx -> {
            ctx.render("/templates/index.html");
        });

        app.get("/news/{id}", ctx -> {
            Map<String, String> model = Map.of("json",
                    articleManager.getArticleByIdAsJsonString(Integer.parseInt(ctx.pathParam("id"))));
            ctx.render("/templates/news.html", model);
        });

    }
}

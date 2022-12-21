package org.example;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Server {

    private final ImgArticleManager articleManager;
    private final String apiPrefix = "/api/v1";

    public Server(){
        articleManager = new ImgArticleManager();
        articleManager.prepareArticles();
    }

    public void run() {
        Javalin app = Javalin.create(config -> {
            //JavalinThymeleaf.init();
            config.plugins.enableCors(corsContainer -> {
                corsContainer.add(CorsPluginConfig::anyHost);
            });
            //config.staticFiles.add("/public/static");
        });

        //API endpoints

        app.start(5000);

        app.get(apiPrefix + "/news", ctx -> {
            String json = articleManager.getArticlesAsJsonString();
            ctx.header("Content-type", "application/json").json(json);
        });

        app.get(apiPrefix + "/news?category={category}", ctx -> {

        });

        app.get(apiPrefix + "/news/{id}", ctx -> {

        });
    }
}

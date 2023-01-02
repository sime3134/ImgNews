package org.example;

import io.javalin.Javalin;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Server {

    private final ImgArticleManager articleManager;
    private final String apiPrefix = "/api/v1";

    public Server(){
        articleManager = new ImgArticleManager();
        articleManager.prepareArticles();
    }

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
            ctx.render("/templates/news.html");
        });

    }
}

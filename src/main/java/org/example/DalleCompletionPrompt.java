package org.example;

public class DalleCompletionPrompt {

    private String model = "text-davinci-003";
    private String prompt;
    public DalleCompletionPrompt(OriginalArticle article) {
        this.prompt =
                "does the following text contain anything related to any famous humans, COVID-19 or violent " +
                        "content?" +
                        " \n\n" + article.getTitle() + ". " +
                article.getContent();
    }
}

package org.example;

public class DalleImagePrompt {
    private String prompt;
    private int n;
    private String size;

    private final String response_format = "b64_json";

    public DalleImagePrompt(String prompt, int n, String size){
        this.prompt = prompt;
        this.n = n;
        this.size = size;
    }

    @Override
    public String toString() {
        return "GenerateImageRequest{" +
                "prompt='" + prompt + '\'' +
                ", n='" + n + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}

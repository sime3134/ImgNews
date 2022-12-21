package org.example;

public class DallePrompt {
    private String prompt;
    private int n;
    private String size;

    public DallePrompt(String prompt, int n, String size){
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

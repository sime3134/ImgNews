package org.example;

import okhttp3.*;

import java.io.IOException;

public class HttpClient {
    private final OkHttpClient client;

    public HttpClient() {
        client = new OkHttpClient();
    }

    public String get(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }

    public String post(Request request){
        System.out.println(request);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

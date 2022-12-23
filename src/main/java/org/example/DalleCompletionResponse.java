package org.example;

import java.util.Arrays;

public class DalleCompletionResponse {
    private DalleCompletionChoice[] choices;

    @Override
    public String toString() {
        return "DalleCompletionResponse{" +
                "choices=" + Arrays.toString(choices) +
                '}';
    }

    public DalleCompletionChoice getChoice() {
        return choices[0];
    }
}

package org.example;

import org.eclipse.jetty.util.log.Logger;

public class DalleCompletionChoice {
    private String text;

    @Override
    public String toString() {
        return "DalleCompletionChoices{" +
                "text='" + text + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }
}

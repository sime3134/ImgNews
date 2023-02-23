package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * This class is used to handle the keybindings for the program.
 */
public class KeyHandler {
    private final HashMap<String, String> keys;

    public KeyHandler(){
        keys = new HashMap<>();
        readKeys();
    }

    public String get(String key){
        return keys.get(key);
    }

    private void readKeys()  {
        try(BufferedReader r =
                    new BufferedReader(new InputStreamReader(new FileInputStream("api_keys.txt")))) {
            while (true) {
                String word = r.readLine();
                if (word == null || word.isEmpty()) {
                    break;
                }
                String[] split = word.split(Pattern.quote("="));
                keys.put(split[0], split[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Keys read successfully");
    }
}

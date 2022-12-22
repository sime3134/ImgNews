package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class KeyHandler {
    private final HashMap<String, String> keys;

    public KeyHandler(){
        keys = new HashMap<>();
        readKeys();
        System.out.println(keys);
    }

    public String get(String key){
        return keys.get(key);
    }

    private void readKeys()  {
        try(BufferedReader r =
                    new BufferedReader(new InputStreamReader(new FileInputStream("api_keys.txt")))) {
            while (true) {
                String word = r.readLine();
                if (word == null) {
                    break;
                }
                String[] split = word.split("=");
                keys.put(split[0], split[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

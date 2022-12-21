package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Blacklist {
    List<String> list = new ArrayList<>();

    public Blacklist(){
        readBlacklistFromFile();
    }

    public boolean containsBlacklisted(OriginalArticle article, int limit){
        for(String s : list){
            if(article.getTitle().contains(s) || article.getFusedDescAndContent(limit).contains(s)) return true;
        }
        return false;
    }

    private void readBlacklistFromFile()  {
        try(BufferedReader r =
                new BufferedReader(new InputStreamReader(new FileInputStream("blacklist.txt")))) {
            while (true) {
                String word = r.readLine();
                if (word == null) {
                    break;
                }
                list.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PreProcess {
    AnagramDictionary dic;
    PreProcess(){
        File wordList = new File("../assets/words.txt");
        try(Reader rdr = new FileReader(wordList)) {
            dic = new AnagramDictionary(rdr, null, null);
        } catch(IOException ex){
            System.out.print("shit");
        }
    }
    public static void main(String[] args) throws IOException {
        PreProcess p = new PreProcess();
        for (int i = 4; i < 9; i++) {
            CopyOnWriteArrayList<String> l2 = new CopyOnWriteArrayList<>(p.dic.getsizeToWords().get(i));
            for (String s : l2) {
                p.dic.getAnagrams(s);
            }
        }

        Map<String, List<String>> data = p.dic.getLettersToWord();

        File outputFileKey = new File("../assets/keyset.obj");
        File outputFileValue = new File("../assets/valueset.obj");

        try(ObjectOutputStream osK = new ObjectOutputStream(new FileOutputStream(outputFileKey));
            ObjectOutputStream osV = new ObjectOutputStream(new FileOutputStream(outputFileValue))){
            for(Map.Entry<String, List<String>> entry : data.entrySet()){
                osK.writeObject(entry.getKey());
                osV.writeObject(entry.getValue());
            }
        } catch(IOException ex){}

    }
}

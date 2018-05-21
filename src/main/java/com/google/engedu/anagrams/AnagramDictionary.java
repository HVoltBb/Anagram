/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private int wordLength = DEFAULT_WORD_LENGTH;
    private Random random = new Random();
    private Set<String> wordList1 = new HashSet<>();
    private Map<Integer, List<String>> sizeToWords = new HashMap<>();
    private Map<Integer, List<String>> pool = new HashMap<>();
    private Map<String, Set<String>> lettersToWord = new HashMap<>();
    private int[] index = new int[MAX_WORD_LENGTH];
    private ExecutorService es = Executors.newCachedThreadPool();
    private static final Object lock = new Object();
    private List<String> cache = new ArrayList<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        for (int i = 1; i < MAX_WORD_LENGTH + 2; ++i) {
            sizeToWords.put(i, new ArrayList<String>());
        }

        while ((line = in.readLine()) != null) {
            String word = line.trim();
            wordList1.add(word);
        }
        for (String word : wordList1) {
            if (word.length() <= MAX_WORD_LENGTH + 1)
                sizeToWords.get(word.length()).add(word);
            }
            pool.putAll(sizeToWords);
    }

    public boolean isGoodWord(String word, String base) {
        return wordList1.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        String targetWords = sortLetters(targetWord);
        if(lettersToWord.get(targetWords)==null) {
            String compareTowd;
            int tlen = targetWords.length();
            Set<String> tmp = new HashSet<>();
            Iterator<String> it = sizeToWords.get(tlen).iterator();
            while(it.hasNext()){
                String word = it.next();
                compareTowd = sortLetters(word);
                if(targetWords.equals(compareTowd)){
                    tmp.add(word);
                    it.remove();
                }
            }
            lettersToWord.put(targetWords, tmp);
        }
        return new ArrayList<>(lettersToWord.get(targetWords));
    }

    private static String sortLetters(String x){
        char[] tmp = x.toCharArray();
        Arrays.sort(tmp);
    	return new String(tmp);
    }


    public List<String> getAnagramsWithOneMoreLetter(String word) {
        if(cache.isEmpty()) {
            List<Future<Integer>> f1 = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                f1.add(es.submit(new tasks(i, word)));
            }
            try {
                for (int i = 0; i < 26; i++) {
                    f1.get(i).get();
                }
            } catch (Exception es) {
            }
        }
        return cache;
    }

    class tasks implements Callable<Integer>{
        private int i;
        private String word;
        tasks(int i, String word){
            this.i = i;
            this.word = word;
        }
        @Override
        public Integer call() {
            List<String> tmp = getAnagrams(sortLetters(word + (char) (97 + i)));
            if (!tmp.isEmpty())
                synchronized (lock) {
                    cache.addAll(tmp);
                }
            return null;
        }
    }

    public String pickGoodStarterWord() {
        String ans;
        boolean flag=false;
        do {
            ans = pickOne();
            cache.clear();
            List<String> tmp = getAnagramsWithOneMoreLetter(ans);
            if(tmp.size()<MIN_NUM_ANAGRAMS){
                pool.get(wordLength).remove(ans);
                flag = true;
            } else
                flag = false;
        } while(flag);
        wordLength = (wordLength<MAX_WORD_LENGTH) ? (wordLength+1):MAX_WORD_LENGTH;
        return ans;
    }

    private String pickOne(){
        int max = pool.get(wordLength).size();
        int thisOne = index[wordLength-1]%max;
        index[wordLength-1] = index[wordLength-1]+1;
        return pool.get(wordLength).get(thisOne);
    }

    @Override
    protected void finalize() throws Throwable {
        es.shutdown();
    }
}

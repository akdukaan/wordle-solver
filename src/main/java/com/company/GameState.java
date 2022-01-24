package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class GameState {
    GameState parent;
    HashSet<String> possibleWords = new HashSet<>();
    HashMap<Character, Integer> lettersMin;
    HashMap<Character, Integer> lettersMax;
    char[] greenLetters;
    ArrayList<Set<Character>> misplacedLetters;

    public GameState() {
        this.parent = null;
        this.greenLetters = new char[5];
        this.lettersMin = new HashMap<>();
        this.lettersMax = new HashMap<>();
        for (Character ch = 'a'; ch <= 'z'; ch++) {
            this.lettersMin.put(ch, 0);
            this.lettersMax.put(ch, 5);
        }
        this.misplacedLetters = new ArrayList<>(Arrays.asList(
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()));
        File file = new File("src/main/java/com/company/possibleWords.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st = br.readLine();
            st = st.replace("\"", "");
            String[] allWords = st.split(", ");
            possibleWords = new HashSet<>(Arrays.asList(allWords));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameState(GameState parent, String guess, String actual) {
        this.parent = parent;
        this.lettersMin = (HashMap) parent.lettersMin.clone();
        this.lettersMax = (HashMap) parent.lettersMax.clone();
        this.greenLetters = parent.greenLetters.clone();
        this.misplacedLetters = new ArrayList<>(Arrays.asList(
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()));
        for (int i = 0; i < 5; i++) {
            for (char c : parent.misplacedLetters.get(i)) {
                misplacedLetters.get(i).add(c);
            }
        }
        this.possibleWords = (HashSet) parent.possibleWords.clone();

        for (char c = 'a'; c <= 'z'; c++) {
            if (getOccurances(c, guess) > getOccurances(c, actual)) {
                this.lettersMin.put(c, getOccurances(c, actual));
                this.lettersMax.put(c, getOccurances(c, actual));
            } else {
                this.lettersMin.put(c, getOccurances(c, guess));
            }
        }
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == actual.charAt(i)) {
                this.greenLetters[i] = guess.charAt(i);
            } else {
                this.misplacedLetters.get(i).add(guess.charAt(i));
            }
        }
        recalculatePossibleWords();
    }

    public void recalculatePossibleWords() {
        narrowUsingGreenLetters();
        narrowUsingYellowLetters();
        narrowUsingLetterMinMax();
    }


    public void narrowUsingGreenLetters() {
        possibleWords.removeIf(word -> (greenLetters[0] != word.charAt(0) && greenLetters[0] != 0) ||
                (greenLetters[1] != word.charAt(1) && greenLetters[1] != 0) ||
                (greenLetters[2] != word.charAt(2) && greenLetters[2] != 0) ||
                (greenLetters[3] != word.charAt(3) && greenLetters[3] != 0) ||
                (greenLetters[4] != word.charAt(4) && greenLetters[4] != 0));
    }

    public void narrowUsingYellowLetters() {
        HashSet<String> newWords = (HashSet) possibleWords.clone();
        for (String word : newWords) {
            boolean keep = true;
            for (int slot = 0; slot < 5; slot++) {
                for (char ch : misplacedLetters.get(slot)) {
                    if (word.charAt(slot) == ch) {
                        keep = false;
                    }
                }
            }
            if (keep) {
                newWords.add(word);
            }
        }
        possibleWords = newWords;
    }

    public void narrowUsingLetterMinMax() {
        HashSet<String> newWords = new HashSet<>();
        for (String word : possibleWords) {
            boolean shouldAddWord = true;
            for (char ch = 'a'; ch <= 'z'; ch++) {
                int occurances = getOccurances(ch, word);
                if (occurances < lettersMin.get(ch) || occurances > lettersMax.get(ch)) {
                    shouldAddWord = false;
                }
            }
            if (shouldAddWord) {
                newWords.add(word);
            }
        }
        possibleWords = newWords;
    }


    public int getOccurances(char letter, String word) {
        int count = 0;
        for(int i=0; i < word.length(); i++) {
            if(word.charAt(i) == letter)
                count++;
        }
        return count;
    }
}

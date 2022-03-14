package com.company;

import java.io.*;
import java.util.*;

public class GameState {
    GameState parent;
    HashSet<String> possibleWords;
    HashMap<Character, Integer> lettersMin;
    HashMap<Character, Integer> lettersMax;
    char[] greenLetters;
    ArrayList<Set<Character>> misplacedLetters;

    public GameState() {
        this.parent = null;
        this.greenLetters = new char[5];
        this.lettersMin = new HashMap<>();
        this.lettersMax = new HashMap<>();
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
            String[] allWords = st.split(",");
            possibleWords = new HashSet<>(Arrays.asList(allWords));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameState(GameState parent, String guess, String output, int round) {
        this.parent = parent;
        this.lettersMin = (HashMap<Character, Integer>) parent.lettersMin.clone();
        this.lettersMax = (HashMap<Character, Integer>) parent.lettersMax.clone();
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
        this.possibleWords = (HashSet<String>) parent.possibleWords.clone();

        for (int i = 0; i < 5; i++) {
            if (output.charAt(i) == 'g') {
                greenLetters[i] = guess.charAt(i);
            } else {
                misplacedLetters.get(i).add(guess.charAt(i));
                int count = 0;
                if (output.charAt(i) == 'b') {
                    for (int j = 0; j < 5; j++) {
                        if (guess.charAt(j) == guess.charAt(i)) {
                            if (output.charAt(j) == 'g' || output.charAt(j) == 'y') {
                                count++;
                            }
                        }
                    }
                    lettersMax.put(guess.charAt(i), count);
                } else {
                    // yellow
                    // count the times it came up as yellow+green and add this to lettersMin
                    for (int j = 0; j < 5; j++) {
                        if (guess.charAt(j) == guess.charAt(i)) {
                            if (output.charAt(j) == 'g' || output.charAt(j) == 'y') {
                                count++;
                            }
                        }
                    }
                    lettersMin.put(guess.charAt(i), count);
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    public GameState(GameState parent, String guess, String actual) {
        this.parent = parent;
        this.lettersMin = (HashMap<Character, Integer>) parent.lettersMin.clone();
        this.lettersMax = (HashMap<Character, Integer>) parent.lettersMax.clone();
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
        this.possibleWords = (HashSet<String>) parent.possibleWords.clone();

        for (char c : guess.toCharArray()) {
            int charOccurrencesGuess = getOccurrences(c, guess);
            int charOccurrencesAnswer = getOccurrences(c, actual);
            if (charOccurrencesGuess > charOccurrencesAnswer) {
                this.lettersMin.put(c, getOccurrences(c, actual));
                this.lettersMax.put(c, getOccurrences(c, actual));
            } else {
                this.lettersMin.put(c, getOccurrences(c, guess));
            }
        }

        for (int i = 0; i < 5; i++) {
            char letter = guess.charAt(i);
            if (letter == actual.charAt(i)) {
                this.greenLetters[i] = letter;
            } else {
                this.misplacedLetters.get(i).add(letter);
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
        possibleWords.removeIf(word ->
                (greenLetters[0] != word.charAt(0) && greenLetters[0] != 0) ||
                (greenLetters[1] != word.charAt(1) && greenLetters[1] != 0) ||
                (greenLetters[2] != word.charAt(2) && greenLetters[2] != 0) ||
                (greenLetters[3] != word.charAt(3) && greenLetters[3] != 0) ||
                (greenLetters[4] != word.charAt(4) && greenLetters[4] != 0));
    }

    public boolean hasMisplacedLetters(String word) {
        for (int slot = 0; slot < 5; slot++) {
            for (char ch : misplacedLetters.get(slot)) {
                if (word.charAt(slot) == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    public void narrowUsingYellowLetters() {
        HashSet<String> newWords = new HashSet<>();
        for (String word : possibleWords) {
            if (!hasMisplacedLetters(word)) {
                newWords.add(word);
            }
        }
        possibleWords = newWords;
    }

    public boolean isWordWithinBounds(String word) {
        for (char letter : lettersMin.keySet()) {
            int occurrences = getOccurrences(letter, word);
            if (occurrences < lettersMin.get(letter)) {
                return false;
            }
        }
        for (char letter : lettersMax.keySet()) {
            int occurrences = getOccurrences(letter, word);
            if (occurrences > lettersMax.get(letter)) {
                return false;
            }
        }
        return true;
    }

    public void narrowUsingLetterMinMax() {
        HashSet<String> newWords = new HashSet<>();
        for (String word : possibleWords) {
            if (isWordWithinBounds(word)) {
                newWords.add(word);
            }
        }
        possibleWords = newWords;
    }


    public int getOccurrences(char letter, String word) {
        int count = 0;
        for(int i=0; i < word.length(); i++) {
            if(word.charAt(i) == letter)
                count++;
        }
        return count;
    }
}

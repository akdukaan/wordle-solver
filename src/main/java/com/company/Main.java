package com.company;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;


public class Main {
    static HashSet<String> validWords = new HashSet<>();

    public static void main(String[] args) throws Exception {
        /*
        File file = new File("src/main/java/com/company/validWords.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();
        st = st.replace("\"", "");
        String[] allWords = st.split(", ");
        validWords.addAll(Arrays.asList(allWords));

         */

        GameState ogState = new GameState();

        // this is how to tell the code that a certain letter is in a certain spot
        // this means that the 2nd letter is an i (subtract 1 from the place you mean to put it)
        // ogState.greenLetters[1] = 'i';

        // this is how to tell the code that there are a maximum of a certain letter in the word
        // this means that there are no r's in the word.
        // ogState.lettersMax.put('r', 0);

        // this is how to tell the code that there is a minimum of a certain letter in the word
        // this means that there are at least 1 t's in the word.
        // ogState.lettersMin.put('t', 1);

        // this is how to tell the code that you know that a certain letter doesn't belong in a certain spot
        // this means that f does not belong in the 4th spot.
        // ogState.misplacedLetters.get(3).add('f');

        // REMEMBER: You want to pick the output with the lowest value.
        // The number is proportional to the number of words you can expect to have left after guessing that word.

        ogState.recalculatePossibleWords();

        Map<String, Integer> totalPossibilities;
        Yaml yaml;
        if (ogState.possibleWords.size() == 2315) {
            InputStream inputStream = new FileInputStream("src/main/java/com/company/output.yml");
            yaml = new Yaml();
            totalPossibilities = yaml.load(inputStream);
        } else {
            totalPossibilities = new HashMap<>();
        }

        for (String guess : ogState.possibleWords) {
            if (!totalPossibilities.containsKey(guess)) {
                ArrayList<GameState> wordChildren = new ArrayList<>();
                for (String solution : ogState.possibleWords) {
                    GameState child = new GameState(ogState, guess, solution);
                    wordChildren.add(child);
                }
                int totalPoss = 0;
                for (GameState state : wordChildren) {
                    totalPoss += state.possibleWords.size();
                }
                totalPossibilities.put(guess, totalPoss);
                System.out.println(totalPossibilities.size() + " " + guess + " " + totalPoss / ogState.possibleWords.size());

                if (ogState.possibleWords.size() == 2315) {
                    yaml = new Yaml();
                    FileWriter writer = new FileWriter("src/main/java/com/company/output.yml");
                    yaml.dump(totalPossibilities, writer);
                }
            }
        }

        // Print the sorted list
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(totalPossibilities.entrySet());
        list.sort(Map.Entry.comparingByValue());
        System.out.println(list);
    }

}


package com.company;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Main {
    static HashSet<String> validWords = new HashSet<>();

    public static void main(String[] args) throws Exception {

        File file = new File("src/main/java/com/company/validWords.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();
        st = st.replace("\"", "");
        String[] allWords = st.split(", ");
        validWords.addAll(Arrays.asList(allWords));

        GameState ogState = new GameState();

        // this is how to tell the code that a certain letter is in a certain spot
        // replace the 0s with the letter surrounded by apostrophes, so like 'a'
        ogState.greenLetters = new char[]{'a',0,0,0,0};

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

        Map<String, Double> totalPossibilities;
        Yaml yaml = new Yaml();
        if (ogState.possibleWords.size() == 2315 && false) {
            InputStream inputStream = new FileInputStream("src/main/java/com/company/output.yml");
            totalPossibilities = yaml.load(inputStream);
        } else {
            totalPossibilities = new HashMap<>();
        }

        Stream<String> stream1 = StreamSupport.stream(ogState.possibleWords.spliterator(), true);
        stream1.forEach(guess -> {
            double totalPoss = 0.0;
            for (String solution : ogState.possibleWords) {
                GameState child = new GameState(ogState, guess, solution);
                totalPoss += child.getScore();
            }
            totalPossibilities.put(guess, totalPoss);
            System.out.println(totalPossibilities.size() + " " + guess + " " + totalPoss / ogState.possibleWords.size());
        });


        for (String guess : ogState.possibleWords) {
            if (!totalPossibilities.containsKey(guess)) {
                int counter = 0;
                double totalPoss = 0;
                for (String solution : ogState.possibleWords) {
                    counter++;

                    GameState child = new GameState(ogState, guess, solution);
                    System.out.println(counter + ". created child " + solution + " for " + guess + " - " + child.getScore());
                    totalPoss += child.getScore();

                }

                totalPossibilities.put(guess, totalPoss);
                System.out.println(totalPossibilities.size() + " " + guess + " " + totalPoss / ogState.possibleWords.size());

            }
        }

        // Print the sorted list
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(totalPossibilities.entrySet());
        list.sort(Map.Entry.comparingByValue());
        DecimalFormat df = new DecimalFormat("0.0000");
        int i = 1;
        for (Map.Entry<String, Double> entry : list) {
            String star = "";
            if (ogState.possibleWords.contains(entry.getKey())) {
                star = "*";
            }
            System.out.println(i++ + ". " + entry.getKey() + " = " + df.format(entry.getValue()) + star);
        }
        if (ogState.possibleWords.size() < 2315) {
            System.out.println(ogState.possibleWords);
        }
    }

}


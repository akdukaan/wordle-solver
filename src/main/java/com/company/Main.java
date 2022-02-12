package com.company;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Main {
    static HashSet<String> validWords = new HashSet<>();
    static Yaml yaml = new Yaml();

    public static void main(String[] args) throws Exception {

        File file = new File("src/main/java/com/company/validWords.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();
        st = st.replace("\"", "");
        String[] allWords = st.split(", ");
        validWords.addAll(Arrays.asList(allWords));

        GameState startState = new GameState();

        /* This is how to tell it what you've already guessed.
           b = black
           y = yellow
           g = green
         */
        //startState = new GameState(startState, "salet", "byyby", 0);
        //startState = new GameState(startState, "trial", "yybyy", 0);
        //startState = new GameState(startState, "prime", "bgbby", 0);
        //startState = new GameState(startState, "users", "bbgyg", 0);
        //startState = new GameState(startState, "greys", "yggbg", 0);
        //startState = new GameState(startState, "dying", "gbggb", 0);

        // REMEMBER: You want to pick the output with the lowest value.
        // The number is proportional to the number of words you can expect to have left after guessing that word.

        startState.recalculatePossibleWords();

        Map<String, Integer> totalPossibilities;
        if (startState.possibleWords.size() == 2315) {
            InputStream inputStream = new FileInputStream("src/main/java/com/company/output.yml");
            totalPossibilities = yaml.load(inputStream);
        } else {
            totalPossibilities = new HashMap<>();
        }

        Stream<String> stream1 = startState.possibleWords.parallelStream();
        stream1.forEach(guess -> {
            if (!totalPossibilities.containsKey(guess)) {
                int totalPoss = 0;
                for (String solution : startState.possibleWords) {
                    GameState child = new GameState(startState, guess, solution);
                    totalPoss += child.possibleWords.size();
                }

                totalPossibilities.put(guess, totalPoss);

                if (startState.possibleWords.size() == 2315) {
                    dumpToYaml(totalPossibilities);
                }
            }
        });

        // Print the sorted list
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(totalPossibilities.entrySet());
        list.sort(Map.Entry.comparingByValue());
        DecimalFormat df = new DecimalFormat("0.0000");
        int i = 1;
        for (Map.Entry<String, Integer> entry : list) {
            String star = "";
            if (startState.possibleWords.contains(entry.getKey())) {
                star = "*";
            }
            System.out.println(i++ + ". " + entry.getKey() + " = " + df.format((0.0 + entry.getValue())/startState.possibleWords.size()) + star);
        }
        if (startState.possibleWords.size() < 2315) {
            System.out.println(startState.possibleWords);
        }
    }

    public static void dumpToYaml(Map<String, Integer> map) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/java/com/company/output.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        yaml.dump(map, writer);
    }

}


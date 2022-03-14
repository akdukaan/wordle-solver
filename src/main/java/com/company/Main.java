package com.company;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.SQLOutput;
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
        String[] allWords = st.split(",");
        validWords.addAll(Arrays.asList(allWords));

        GameState startState = new GameState();

        /* This is how to tell it what you've already guessed.
           b = black
           y = yellow
           g = green
         */


//        startState = new GameState(startState, "salet", "ybbbb", 0);

        // REMEMBER: You want to pick the output with the lowest value.
        // The number is proportional to the number of words you can expect to have left after guessing that word.

        startState.recalculatePossibleWords();

        Map<String, Integer> totalPossibilities;
        if (startState.possibleWords.size() == 2309) {
            InputStream inputStream = new FileInputStream("src/main/java/com/company/output.yml");
            totalPossibilities = yaml.load(inputStream);
        } else {
            totalPossibilities = new HashMap<>();
        }

        Stream<String> stream1 = validWords.parallelStream();
        GameState finalStartState = startState;
        stream1.forEach(guess -> {
            System.out.println(guess + " - ");
            if (!totalPossibilities.containsKey(guess)) {
                int totalPoss = 0;
                for (String solution : finalStartState.possibleWords) {
                    GameState child = new GameState(finalStartState, guess, solution);
                    totalPoss += child.possibleWords.size();
                }

                totalPossibilities.put(guess, totalPoss);
                dumpToYaml(totalPossibilities);
            }
        });
        if (finalStartState.possibleWords.size() == 2309) {
            dumpToYaml(totalPossibilities);
        }

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
        if (startState.possibleWords.size() < 2309) {
            System.out.println(startState.possibleWords);
        }
    }

    // Probably better to make this not async but i'll do that later.
    public static void dumpToYaml(Map<String, Integer> map) {
        try {
            FileWriter writer = null;
            try {
                writer = new FileWriter("src/main/java/com/company/output.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            yaml.dump(map, writer);
        } catch (ConcurrentModificationException e) {
            System.out.println("ERROR IGNORED CONCURRENT");
        }
    }

}


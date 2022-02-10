package com.company;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;


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
        ogState.greenLetters = new char[]{0, 0, 0, 0, 0};

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

        String firstGuess = "salet";
        // salet = 3.542980561555076
        ArrayList<Integer> depths = new ArrayList<>();
        Stream<String> stream1 = ogState.possibleWords.parallelStream();
        stream1.forEach(solution -> {
            int depth = 1;
            StringBuilder line = new StringBuilder(solution + ": " + firstGuess);
            if (!firstGuess.equals(solution)) {
                GameState state = new GameState(ogState, firstGuess, solution);
                String bestGuess = state.getBestGuess();
                while (!bestGuess.equals(solution)) {
                    line.append(" -> ").append(bestGuess);
                    depth++;
                    state = new GameState(state, state.getBestGuess(), solution);
                    bestGuess = state.getBestGuess();
                }
                depth++;
            }


            line.append(" -> ").append(solution).append(" | ").append(depth);
            System.out.println(line);
            depths.add(depth);
        });
        System.out.println(depths);
        OptionalDouble average = depths
                .stream()
                .mapToDouble(a -> a)
                .average();
        System.out.println("THE AVERAGE FOR THE WORD IS ");
        System.out.print(average.isPresent() ? average.getAsDouble() : 0);

        Map<String, Integer> totalPossibilities;
        Yaml yaml = new Yaml();
        if (ogState.possibleWords.size() == 2315) {
            InputStream inputStream = new FileInputStream("src/main/java/com/company/output.yml");
            totalPossibilities = yaml.load(inputStream);
        } else {
            totalPossibilities = new HashMap<>();
        }
    }
}


# Wordle Solver
The goal of this program is to find the best words to guess in a wordle game using any previous hints.
Best word is currently determined by the number of words that it would eliminate on average.
I wish to modify this soon so that it also analyzes the similarity of the remaining words to give slightly more accurate results.

You can view the best starting words by looking in output.yml, although it is not yet complete because it's taking a while to run. Look for the words with the lowest value. Truly seems to be a pretty good choice at the moment.

After you input the first word and get some feedback, you can input that feedback in Main.java to see what the next best guesses are.

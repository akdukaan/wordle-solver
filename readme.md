If you don't want to read all this, just [click here to go straight to the results](https://github.com/drbot7/wordle-solver/blob/main/results.md)

# Introduction & Background

We all know the popular word game Wordle, but for those of you who don’t, it’s like a mixture of mastermind and hangman. The goal of the game is to guess the wordle in 6 tries or fewer. The solution and each guess must be a valid 5 letter word. After each guess, the color of the tiles will change to show how close your guess was to the word. Green indicates that the character was placed in the right spot, yellow indicates that the character is in the word, but not in the right spot, and gray indicates that the character is not in the word. In your first guess, you would want to eliminate as many words as possible. Some words are better than others for this task. I’ll be attempting to find which word is best at eliminating the most words.

Others have made attempts at this same topic. Tyler Glaiel made a [program](https://medium.com/@tglaiel/the-mathematically-optimal-first-guess-in-wordle-cbcb03c19b0a) that found the best words to be “roate” and “raise”. Glancing at Glaiel’s code, the way that he finds yellow characters seems to be inconsistent with the actual website when a character repeats in the guess but not the solution. I could be wrong as his code was written in C++ and that is not a language I am very experienced in. Nonetheless, my program can be used to provide supporting evidence for or contradictory evidence against his findings.

# Methodology

Looking at the source code, you can find a [javascript file](https://www.powerlanguage.co.uk/wordle/main.e65ce0a5.js), and in this file, there are two variables La and Ta corresponding to the words that the solution is picked from and the words that you are allowed to guess, respectively. We can see that there are 2315 words from which a solution is chosen and nearly 13000 words from which you can guess.

Using those variables, I create a program. The program calculates the number of words remaining in the game for each of the guess and solution combinations. So for example, for the guess “hello”, we would calculate 2315 different states, one for each of the possible solutions. For each of those states, I calculate how it would color the word and what words I can eliminate from those hints. I then use those hints to narrow down the list of possible words in that state. I take the average of the size of the list of possible words from each solution combination for a guess, and I can then map the guess to the average number of words remaining after guessing that word.

At that point, we have a list of words that are mapped to the average number of possible words we have remaining after that guess. We can sort this list and print its results.

# Results & Conclusion

Running my program all night, I found that the best two words were “roate” and “raise” which limit the remaining words down to an average of 60.4 and 61.0 words, respectively. The [full results can be found on this page](https://github.com/drbot7/wordle-solver/blob/main/results.md).

While my top two words were consistent with Glaiel’s, he did not post his results as the average number of words any word narrows it down to. He did, however, post the average number of guesses that it takes for his algorithm to find a solution. I would like to add to my algorithm to calculate that same number so that our outputs match.

My algorithm does not consider how similar words in any of the potential states are. For example, if 4 of the 5 letters are the same in all of the remaining possible solutions, then the power of each of our future guesses is low because any guess can only eliminate one possible solution. Future work could be to use a recursive model to thoroughly examine each state to try to avoid situations where we are left with this problem.

EDIT: It seems that 3Blue3Brown has since created a recursive model which finds that the best starting word is actually [SALET](https://youtu.be/fRed0Xmc2Wg?t=603). Interestingly, when he looks only 1 step in, his best word is found to be SOARE which contradicts what I find. I feel confident in my code though, so i'm unsure if this is a mistake on my part or on his.

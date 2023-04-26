package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeSet;


public class EvilHangman {
    public static void main(String[] args) throws IOException {

        File dictionaryFile = new File(args[0]);
        String dictionaryFileName = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int maxGuesses = Integer.parseInt(args[2]);
        int guesses = maxGuesses;

        if (!(wordLength >= 2 )) {
            System.out.println("The length of the word must be at least 2");
            System.exit(69);
        }
        if (!(maxGuesses >= 1)) {
            System.out.println("The minimum number of guesses is 1");
            System.exit(69);
        }

        EvilHangmanGame game = new EvilHangmanGame();

        try {
            game.startGame(dictionaryFile, wordLength);
        }
        catch (IOException error) {
            error.printStackTrace();
        }
        catch (EmptyDictionaryException error) {
            System.out.println(error.getMessage());
        }

        String startMessage = String.format("java EvilHangman %s %d %d", dictionaryFileName, wordLength, maxGuesses);
        System.out.println(startMessage);

        String oldDashReturn = "";
        StringBuilder sb = new StringBuilder();
        while (guesses > 0) {
            int guessesUsed = maxGuesses - guesses;
            String numGuesses = String.format("\nYou have %d guesses left", guesses);
            System.out.println(numGuesses);
            System.out.print("Used Letters: ");

            for (Character c : game.getGuessedLetters()) {
                System.out.printf("%c ", c);
            }
            if (guesses == maxGuesses) {        //first iteration
                System.out.print("\nWord: ");

                for (int i = 0; i < wordLength; i++) {
                    sb.append('-');
                }
                oldDashReturn = String.valueOf(sb);
                System.out.print(sb);
            }
            else {
                System.out.printf("\b\nWord: %s", game.getDashReturn()); //the backslash gets rid of the last space
                oldDashReturn = game.getDashReturn();
            }
            System.out.println("\nEnter guess: ");



            try {
                //checks input for all impurities
                char guess = getChar();
                TreeSet<String> words = new TreeSet<>();
                words = (TreeSet<String>) game.makeGuess(guess);


                //System.out.println(game.makeGuess(guess)); //testing... prints the possible answers
                //if it successfully guesses...
                if (Objects.equals(game.getDashReturn(), oldDashReturn)) {
                    guesses--;
                }


                String dashAlpha = game.dashReturn;
                if (guesses != 0) {
                    if (!game.getDashReturn().contains(Character.toString(guess))) {
                        System.out.printf("Sorry, there are no %c's\n", guess);
                    }
                    else {
                        int countChar = 0;
                        String isOrAre = "are";
                        String aposAndS = "'s";
                        for (char c : game.getDashReturn().toCharArray()) {
                            if (c == guess) {
                                countChar++;
                            }


                        }
                        if (countChar == 1) {
                            aposAndS = "";
                            isOrAre = "is";
                        }
                        System.out.printf("Yes, there %s %d %c%s\n\n", isOrAre, countChar,guess, aposAndS);
                    }
                }
                else {
                    int countChar = 0;
                    String isOrAre = "are";
                    String aposAndS = "'s";
                    for (char c : game.getDashReturn().toCharArray()) {
                        if (c == guess) {
                            countChar++;
                        }
                    }
                    if (countChar == 1) {
                        aposAndS = "";
                        isOrAre = "is";
                    }
                    System.out.printf("Yes, there %s %d %c%s\n\n", isOrAre, countChar,guess, aposAndS);
                }

                //here so that when the returnDash is all alphabetic, then that is the answer, so we want to finish the game
                int dashAlphaCount = 0;
                for (Character c : dashAlpha.toCharArray()) {
                    if (Character.isAlphabetic(c)) {
                        dashAlphaCount++;
                    }
                }
                if (dashAlphaCount == wordLength) {
                    System.out.print("You win! You guessed the word: ");
                    for (String s : game.dictionaryWords) { //forloop should only run once
                        System.out.print(s);
                        System.exit(0);
                    }

                }
            } catch (GuessAlreadyMadeException error) {
                System.out.println("You've already guessed this letter!");
            }
        }
        if (game.dictionaryWords.size() == 1) {
            System.out.printf("You win! You guessed the word: %s", game.dictionaryWords);
        }
        else {
            System.out.println("You lose!");
            System.out.print("The word was: ");
            for (String s : game.dictionaryWords) { //forloop should only run once
                System.out.print(s);
                System.exit(0);
            }
        }
    }

    public static char getChar() {
        char userChar = ' ';

        //continue looping while the next char is alphabetic
        while (!Character.isAlphabetic(userChar) || Character.isSpaceChar(userChar)) {
            String userInput = new Scanner(System.in).nextLine(); //gets input from command line
            if (userInput.isBlank() || userInput.isEmpty()) {
                userChar = ' ';
            }
            else {
                userChar = userInput.charAt(0);
            }
            //if the next char is alphabetic, get a different input
            if (!Character.isAlphabetic(userChar) || Character.isSpaceChar(userChar)) {
                System.out.println("Invalid input! ");
            }

        }

        return Character.toLowerCase(userChar); //ensures the return is lowercase
    }

}

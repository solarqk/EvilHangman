package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
//use map of sets
    Set<String> dictionaryWords = new TreeSet<>();
    TreeSet<Character> guessedChars = new TreeSet<>();
    TreeSet<String> wordsOfLengthX = new TreeSet<>();
    String dashes, dashReturn;
    private int lengthOfWord = 0, numOfChars;


    public EvilHangmanGame() {}

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        boolean throwError = true;
        dictionaryWords.clear();
        wordsOfLengthX.clear();
        guessedChars.clear();

        if (dictionary.length() == 0) { //checks if the file is empty
            throw new EmptyDictionaryException();
        }

        setLengthOfWord(wordLength); //sets the length of the word for the whole file

        //creates a string of length wordLength and replaces the null characters with dashes
        dashes = new String(new char[wordLength]).replace('\0', '-');

        Scanner scanner = new Scanner(dictionary);
        while(scanner.hasNext()) {
            //if (scanner.next().length() == wordLength) {
                dictionaryWords.add(scanner.next().toLowerCase()); //puts all the words from the file and puts into the set
            //}
        }
        String blank = "aFgHJdkeP";
        blank.toLowerCase();

        for (String s : dictionaryWords) {
            if (s.length() == wordLength) {
                wordsOfLengthX.add(s);
                throwError = false;
            }
        }
        if (throwError) {       //if none of the words in the dictionary match the length of the guess, throw an error
            throw new EmptyDictionaryException();
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        String guessString = Character.toString(guess);
        char guessLC = guessString.toLowerCase().charAt(0);

        if (!guessedChars.isEmpty()) {
            if(guessedChars.contains(guessLC)) {
                throw new GuessAlreadyMadeException();
            }
        }
        guessedChars.add(guessLC);
        TreeMap<String, TreeSet<String>> wordMap = new TreeMap<>();

        //puts all words into sets based off of how long the word is
        for (String s : wordsOfLengthX) {
            char [] stringCharArray = s.toCharArray();

            String dashPattern = "";

                for (Character sC : stringCharArray) {
                    dashPattern = Dashes(s, guess);
                }

            //if the length of the dashPattern equals length of word...     //if not, words of other sizes get added
            if ( (dashPattern.length() == getLengthOfWord())) {
                //puts all the words into sets based off of where //THE EVIL PART
                if (!wordMap.containsKey(dashPattern))  {
                    TreeSet<String> toBeAdded = new TreeSet<>();
                    wordMap.put(dashPattern, toBeAdded);
                    wordMap.get(dashPattern).add(s);
                }
                else {
                    wordMap.get(dashPattern).add(s);
                }
            }
        }
        String key = "";
        int biggestVal = 0; //the size of the biggest set in wordMap
        for (Map.Entry<String, TreeSet<String>> entry : wordMap.entrySet()) {
            if (entry.getValue().size() > biggestVal) {

                wordsOfLengthX = entry.getValue();
                biggestVal = entry.getValue().size();
                key = entry.getKey();
                String str = entry.getKey();
                char[] strCharArray = str.toCharArray();
                int counter = 0;
                for (Character c : strCharArray) {
                    if (c == guessLC) {
                        counter++;
                    }
                }
                setNumOfChars(counter);
                dashes = entry.getKey();
            }
        }
        setDashReturn(key); //does dash return with only the biggest value
        String currKey = "";
        //for each entry in wordMap...
        for (Map.Entry<String, TreeSet<String>> entry : wordMap.entrySet()) {

            //update wordsOfLengthX to the biggest set in wordMap
            if (wordsOfLengthX == null || entry.getValue().size() > wordsOfLengthX.size()) {
                wordsOfLengthX = entry.getValue();                //updates sizeSet
                currKey = entry.getKey();
            }

            if (entry.getValue().size() == wordsOfLengthX.size()) {
                if(!entry.getKey().contains(Character.toString(guess))) { //if the value of the entry doesn't have the letter in it
                    wordsOfLengthX = entry.getValue();
                    currKey = entry.getKey();
                }

                //if every word contains the character
                else if (entry.getKey().contains(Character.toString(guessLC)) && currKey.contains(Character.toString(guessLC))) {

                    //filters the set to only be what is equal to the count of guessLC
                    long countEntry = 0;
                    long countWordOfLengthX = 0;

                    //gets the # of occurrences of guessLC in the set wordsOfLengthX
                    char[] sCharArray = entry.getKey().toCharArray();
                    for (Character ch : sCharArray) {
                        if (ch == guessLC) {
                            countEntry++;
                        }
                    }

                    //gets the # of occurrences of guessLC of each word in the wordMap
                    char[] tCharArray = currKey.toCharArray();
                    for (Character ch : tCharArray) {
                        if (ch == guessLC) {
                            countWordOfLengthX++;
                        }
                    }

                    if (countEntry < countWordOfLengthX) {
                        wordsOfLengthX = entry.getValue();
                        currKey = entry.getKey();
                    }

                    //if they appear the same amount of times   //checks for rightmost letter
                    else if (countEntry == countWordOfLengthX) {
                        if (entry.getKey().compareTo(currKey) < 0) {
                            wordsOfLengthX = entry.getValue();
                            currKey = entry.getKey();
                        }
                    }
                }
            }
        }

        assert wordsOfLengthX != null;
        dictionaryWords = wordsOfLengthX;
        dashes = wordsOfLengthX.first();

        return dictionaryWords; //return a set of strings that is the best fit off the specifications in the set
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {

        return this.guessedChars;
    }


    private String Dashes(String word, char guess) {
        char[] arrayOfChars = word.toCharArray();   //converts the word to an array of characters
        String toChar = "";
        StringBuilder result = new StringBuilder();
        StringBuilder toCharSB = new StringBuilder();


        for (char c : arrayOfChars) {   //for each char in the array, if the char doesn't match the guess,
                if (c != guess) {           //convert to a dash
                    c = '-';
                    result.append(c);
                }
                else {                      //if it does match, don't change
                    result.append(c);
                }
        }
        return result.toString();       //returns the character/dash result
    }

    public int getLengthOfWord() {
        return lengthOfWord;
    }

    public void setLengthOfWord(int lengthOfWord) {
        this.lengthOfWord = lengthOfWord;
    }

    public int getNumOfChars() {
        return numOfChars;
    }

    public void setNumOfChars(int numOfChars) {
        this.numOfChars = numOfChars;
    }

    public String getDashes() {
        return dashes;
    }

    public void setDashes(String dashes) {
        this.dashes = dashes;
    }

    public void setDashReturn(String s) {
        char[] sArrayOfChars = s.toCharArray();
        char[] dashReturnArrayOfChars = s.toCharArray();
        if (!(guessedChars.size() == 1)) {
            dashReturnArrayOfChars = dashReturn.toCharArray();
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sArrayOfChars.length; i++) {
                if (s.charAt(i) != '-') {
                    sb.append(sArrayOfChars[i]);
                }
                else {
                    sb.append(dashReturnArrayOfChars[i]);
                }
        }
        dashReturn = sb.toString();
    }

    public String getDashReturn() {
        return dashReturn;
    }
}

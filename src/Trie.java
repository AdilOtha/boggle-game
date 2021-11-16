import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***************************************************************************************
 *    This method is adapted and modified from the following source:
 *    Title: "Java implementation of Trie Data Structure", Techie Delight
 *    Author: Techie Delight
 *    Date: 11-Nov-2021
 *    Availability: https://www.techiedelight.com/implement-trie-data-structure-java/
 *
 ***************************************************************************************/

/**
 * Defines the data and methods for generation of a Trie data structure.
 * It is used for the generation of a Trie dictionary from the provided list of words.
 */
public class Trie {
    // A HashMap that stores a character of a word as a key and a Trie of its subsequent letters as the value.
    Map<Character, Trie> children = new HashMap<>();

    // Checks if the string formed by going from the root of Trie to the current letter is a word.
    boolean isAWord = false;

    // Return value of search() method when we cannot find the specified word in the Trie.
    public static final int RETURN_IS_LEAF = 0;
    // Return value of search() method when we are able to find the specified word at the leaf node of the Trie.
    public static final int RETURN_IS_WORD = 1;
    // Return value of search() method if the specified word is a substring in the Trie.
    public static final int RETURN_IS_SUBSTRING = 2;
    // Return value of search() method when we are able to find a the specified word which is also a substring of another word.
    public static final int RETURN_IS_WORD_SUBSTRING = 3;

    /**
     * Iterate over the provided list of words and adds them to the Trie.
     * @param wordList List of words for dictionary
     */
    public void generateDictionary(List<String> wordList){
        for(String word: wordList){
            addWord(word);
        }
    }

    /**
     * Adds the provided word to the Trie.
     * @param word
     */
    public void addWord(String word){
        Trie current = this;

        for(char letter: word.toCharArray()){
            if(current.children.get(letter)==null){
                current.children.put(letter, new Trie());
            }

            current = current.children.get(letter);

        }

        current.isAWord = true;
    }

    /**
     * Searches the Trie dictionary for the supplied word.
     * @param word
     * @return Returns any of the constants mentioned above accordingly.
     */
    public int search(String word) {
        Trie current = this;

        // keep searching the children till we don't find a particular character or reach leaf node
        for(char letter: word.toCharArray()){
            current = current.children.get(letter);

            if(current==null){
                return RETURN_IS_LEAF;
            }
        }

        if(current.isAWord){
            // if string is word and reached leaf node of Trie
            if(current.children.size()==0){
                return RETURN_IS_WORD;
            } else { // if string is word but not reached leaf node of Trie, i.e., it is a substring of another word
                return RETURN_IS_WORD_SUBSTRING;
            }
        }

        // if string is not a word but there are branches to explore
        if(current.children.size()!=0){
            return RETURN_IS_SUBSTRING;
        } else { // if string is not a word and there are no branches to explore
            return RETURN_IS_LEAF;
        }

    }

}

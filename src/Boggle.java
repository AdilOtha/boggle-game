import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Class that defines the data and methods to solve a Boggle puzzle
 */
public class Boggle {
    // List of words to be stored in the dictionary
    List<String> wordList = new ArrayList<>();

    // Matrix of characters that stores the puzzle grid
    List<List<Character>> puzzleGrid = new ArrayList<>();

    // A reference to the Trie object that will store the dictionary
    Trie trie;

    // variable to check the status of the puzzle
    boolean isPuzzleReady = false;

    // Constants that define the direction of search in the puzzle grid
    public static final char UP = 'U';
    public static final char DOWN = 'D';
    public static final char LEFT = 'L';
    public static final char RIGHT = 'R';
    public static final char NORTH = 'N';
    public static final char SOUTH = 'S';
    public static final char EAST = 'E';
    public static final char WEST = 'W';
    public static final char SOURCE = 'X';

    /**
     * Method that reads lines from a BufferedReader stream and stores the words to be used in the dictionary
     * @param stream The BufferedReader instance used for reading the words line by line
     * @return true if all words are read for dictionary, else false
     */
    boolean getDictionary(BufferedReader stream){
        // if BufferedReader instance is null, then return false
        if(stream==null){
            return false;
        }
        String word;
        try{
            // Read the input stream line-by-line till end-of-file
            while((word=stream.readLine())!=null){
                // if empty line is encountered then consider it as end of input
                if(word.isEmpty()){
                    break;
                }
                // Set Dictionary as invalid if 1-character word is encountered
                if(word.length() == 1){
                    return false;
                }
                // add word to list of words
                wordList.add(word);
            }
        } catch (IOException ioException){ // catch all I/O exceptions
            return false;
        }

        // return true if all words are read
        return true;
    }

    /**
     * Method that reads lines from a BufferedReader stream and creates a puzzle grid
     * @param stream The BufferedReader instance used for reading the puzzle grid rows line-by-line
     * @return true if the puzzle grid is ready for solving, else false
     */
    boolean getPuzzle(BufferedReader stream){
        // if BufferedReader instance is null, then return false
        if(stream==null){
            return false;
        }
        String line;
        try{
            // Read the input stream line-by-line till end-of-file
            while((line=stream.readLine())!=null){
                // if empty line is encountered then consider it as end of input
                if(line.isEmpty()){
                    break;
                }
                // block that compares the length of 2 successive rows
                if(puzzleGrid.size()>0){
                    // get previous row in the grid
                    List<Character> previousRow = puzzleGrid.get(puzzleGrid.size()-1);
                    // return false if any unequal length rows are encountered
                    if(previousRow.size() != line.length()){
                        return false;
                    }
                }

                // generate the puzzle grid
                List<Character> characterList = new ArrayList<>();
                for(char character: line.toCharArray()){
                    characterList.add(character);
                }
                puzzleGrid.add(characterList);
            }
        } catch (IOException ioException){
            return false;
        }

        // store status as true and return true as puzzle is ready
        isPuzzleReady=true;
        return true;
    }

    /**
     * Uses the generated puzzle grid and dictionary to find words and their path information.
     * @return A list of strings each containing the found word, its start coordinates and the directions for finding the word from those coordinates.
     */
    List<String> solve(){
        // condition that checks if puzzle is ready
        if(!isPuzzleReady){
            return null;
        }
        int height = puzzleGrid.size();

        int width;
        if(height==0){
            width=0;
        } else {
            width = puzzleGrid.get(0).size();
        }

        // Stack that stores the coordinates of letters for DFS search
        Stack<Coordinates> stack = new Stack<>();
        // array that keeps track of visited letters in the grid
        boolean[][] visitedNodes = new boolean[width+1][height+1];
        // variable that stores the current substring or word found in dictionary
        StringBuilder searchString = new StringBuilder();
        // The list containing words found from puzzle, their start coordinates and directions
        List<String> result = new ArrayList<>();
        // Map that stores the words found as key and the string containing its start coordinates and directions, as value
        Map<String, String> resultMap = new TreeMap<>();
        // creating a new Trie instance for dictionary generation
        trie = new Trie();

        // generate the Trie dictionary
        trie.generateDictionary(wordList);

        // iterate over the entire grid to search for the words using Depth-First Search approach
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){

                // push the coordinates of starting letter in the stack and set it as source letter
                stack.push(new Coordinates(i,j,SOURCE));
                // store the starting coordinated for future use
                Coordinates firstLetter = stack.peek();
                // variable that stores the directions to the found word
                StringBuilder pathResult = new StringBuilder();

                // search depth wise through the neighbouring letters till stack is not empty
                while(!stack.isEmpty()){
                    Coordinates pair = stack.pop();
                    int x = pair.getX();
                    int y = pair.getY();
                    char direction = pair.getDirection();

                    // if letter goes out of the grid or is visited
                    if(x<=0 || y<=0 || x>width || y>height || visitedNodes[x][y]){
                        // if the letter is the last one from the neighbours then backtrack
                        if((direction==NORTH)){
                            // set last visited node as unvisited
                            visitedNodes[++x][--y]=false;
                            // update the search string
                            if(searchString.length()>0) {
                                searchString.deleteCharAt(searchString.length() - 1);
                            }
                            char lastLetterPath = 0;
                            // update the current path
                            if(pathResult.length()>0){
                                lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                // get the direction of the removed letter
                                pathResult.deleteCharAt(pathResult.length()-1);
                            }
                            x=x+1;
                            y=y-1;
                            // backtrack if the current letter has reached end of neighbour list
                            while(!(x<=0 || y<=0 || x>width || y>height) && visitedNodes[x][y] && pathResult.length()>0 && lastLetterPath==NORTH){
                                if(searchString.length()>0){
                                    searchString.deleteCharAt(searchString.length()-1);
                                }
                                lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                pathResult.deleteCharAt(pathResult.length()-1);
                                visitedNodes[x][y]=false;
                                x=x+1;
                                y=y-1;
                            }
                        }
                        continue;
                    }
                    // get actual indices of the grid matrix
                    int row = (height-y);
                    int column = (x-1);

                    // add letter to search string
                    searchString.append(puzzleGrid.get(row).get(column));

                    // set letter as visited
                    visitedNodes[x][y]=true;

                    if(searchString.length()<=1){ // for 1-character string, check its neighbours
                        pushNeighboursToStack(stack,x,y);
                    } else {
                        // search the string in the Trie
                        int searchResult = trie.search(searchString.toString());

                        switch (searchResult){
                            // if word is found at leaf of Trie
                            case Trie.RETURN_IS_WORD:
                                pathResult.append(direction);

                                // add string to map only if it is not already found
                                if(!resultMap.containsKey(searchString.toString())){
                                    resultMap.put(searchString.toString(), "\t" + firstLetter.getX()+"\t"+firstLetter.getY()+"\t"+pathResult);
                                }

                                // update search and path string
                                searchString.deleteCharAt(searchString.length()-1);
                                if(pathResult.length()>0){
                                    pathResult.deleteCharAt(pathResult.length()-1);
                                }
                                visitedNodes[x][y]=false;

                                // If the current letter does not have unvisited neighbours,
                                // then pop elements from stack till we reach a letter that has unvisited neighbours.
                                // Also, delete letters from the path and search string accordingly and jump to next iteration of stack.
                                if(direction==NORTH){
                                    if(searchString.length()>0){
                                        searchString.deleteCharAt(searchString.length()-1);
                                    }
                                    visitedNodes[++x][--y]=false;
                                    char lastLetterPath = 0;
                                    if(pathResult.length()>0){
                                        lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                        pathResult.deleteCharAt(pathResult.length()-1);
                                    }
                                    x=x+1;
                                    y=y-1;
                                    while(!(x<=0 || y<=0 || x>width || y>height) && visitedNodes[x][y] && pathResult.length()>0 && lastLetterPath==NORTH){
                                        if(searchString.length()>0){
                                            searchString.deleteCharAt(searchString.length()-1);
                                        }
                                        lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                        pathResult.deleteCharAt(pathResult.length()-1);
                                        visitedNodes[x][y]=false;
                                        x=x+1;
                                        y=y-1;
                                    }
                                }
                                continue;

                            // if string is substring of a word
                            case Trie.RETURN_IS_SUBSTRING:
                                pathResult.append(direction);
                                pushNeighboursToStack(stack,x,y);
                                continue;

                            // if string is a word as well as substring of another word
                            case Trie.RETURN_IS_WORD_SUBSTRING:
                                pathResult.append(direction);

                                if(!resultMap.containsKey(searchString.toString())){
                                    resultMap.put(searchString.toString(), "\t" + firstLetter.getX()+"\t"+firstLetter.getY()+"\t"+pathResult);
                                }
                                pushNeighboursToStack(stack,x,y);
                                continue;

                            // if string is not found in the Trie
                            default:
                                searchString.deleteCharAt(searchString.length()-1);
                                visitedNodes[x][y]=false;

                                // If this is the last neighbour,
                                // then pop elements from stack till we reach a letter that has unvisited neighbours.
                                // Also, delete letters from the path and search string accordingly and jump to next iteration of stack.
                                if(direction==NORTH){
                                    if(searchString.length()>0){
                                        searchString.deleteCharAt(searchString.length()-1);
                                    }
                                    visitedNodes[++x][--y]=false;
                                    char lastLetterPath = 0;
                                    if(pathResult.length()>0){
                                        lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                        pathResult.deleteCharAt(pathResult.length()-1);
                                    }
                                    x=x+1;
                                    y=y-1;
                                    while(!(x<=0 || y<=0 || x>width || y>height) && visitedNodes[x][y] && pathResult.length()>0 && lastLetterPath==NORTH){
                                        if(searchString.length()>0){
                                            searchString.deleteCharAt(searchString.length()-1);
                                        }
                                        lastLetterPath=pathResult.charAt(pathResult.length()-1);
                                        pathResult.deleteCharAt(pathResult.length()-1);
                                        visitedNodes[x][y]=false;
                                        x=x+1;
                                        y=y-1;
                                    }
                                }
                                break;
                        }
                    }
                }

                // reset search and path strings
                searchString.setLength(0);
                pathResult.setLength(0);

                // set all letters as unvisited for next iteration
                visitedNodes = new boolean[width+1][height+1];
            }
        }

        // form a list of words with path information from the sorted TreeMap
        for(Map.Entry<String, String> pair: resultMap.entrySet()){
            result.add(pair.getKey() + pair.getValue());
        }

        return result;
    }

    /**
     * For a letter located at the given x and y coordinates,
     * pushes its neighbouring letters in grid in all directions to the given stack.
     * @param stack Provided stack to store the neighbours
     * @param x X coordinate of the letter
     * @param y Y coordinate of the letter
     */
    public void pushNeighboursToStack(Stack<Coordinates> stack, int x, int y){
        stack.push(new Coordinates(x-1,y+1,NORTH)); // N
        stack.push(new Coordinates(x-1, y,LEFT)); // L
        stack.push(new Coordinates(x-1, y-1,WEST)); // W
        stack.push(new Coordinates(x, y-1,DOWN)); // D
        stack.push(new Coordinates(x+1, y-1,SOUTH)); // S
        stack.push(new Coordinates(x+1,y,RIGHT)); // R
        stack.push(new Coordinates(x+1,y+1,EAST)); // E
        stack.push(new Coordinates(x,y+1,UP)); // U
    }

    /**
     * Print generated puzzle grid as a string
     * @return string containing the puzzle grid
     */
    String print(){
        if(!isPuzzleReady){
            return null;
        }
        StringBuilder result = new StringBuilder();

        for(List<Character> charList:puzzleGrid){
            for(Character letter: charList){
                result.append(letter);
            }
            result.append("\n");
        }
        return result.toString();
    }

}

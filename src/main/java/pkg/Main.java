package pkg;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        long startTime = System.nanoTime();
        try{
            BufferedReader stream1 = new BufferedReader(new FileReader(args[0]));
            Boggle boggle = new Boggle();
            if(boggle.getDictionary(stream1)){
                System.out.println("Words ready for puzzle-solving");
            } else {
                System.out.println("Error while reading dictionary");
            }
            try{
                BufferedReader stream2 = new BufferedReader(new FileReader(args[1]));
                if(boggle.getPuzzle(stream2)){
                    System.out.println("Puzzle Grid is ready!");
                    System.out.println(boggle.print());
                } else {
                    System.out.println("Error while reading puzzle");
                }
            } catch (FileNotFoundException fne){
                System.out.println("File Not Found for scanning Puzzle!");
            }
            List<String> strList=boggle.solve();
            System.out.println(strList);
            System.out.println(strList.size());
        } catch (FileNotFoundException fne){
            System.out.println("File Not Found for scanning Dictionary!");
        }
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);



    }
}

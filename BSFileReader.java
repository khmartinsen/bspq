/*
Kevin Martinsen
CSCI 1112 - OOP 2

BSFileReader is responsible for reading in BS(p,q) files given a p,q, and Cayley graph path into an array

File structure ideas
BSp_q/BTTBT (more organized)
BTTBT.bsp_q

 */

import java.io.*;
import java.util.ArrayList;

public final class BSFileReader {
    private BSFileReader(){}

    public static int[] fileToArray(final int p, final int q, final String movesString, String directory) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        try (
                BufferedReader input = new BufferedReader(new FileReader(directory + "BS" + p + "_" + q + "/" + movesString));
        ) {
            String line = input.readLine();
            while (line != null) {
                arrayList.add(Integer.parseInt(line));
                line = input.readLine();
            }
        }
        catch (FileNotFoundException ex){
            System.out.println("File " + movesString + " does not exist in folder BS" + p + "_" + q + ".");
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return arrayList.stream().mapToInt(i->i).toArray();
    }

    public static int[] fileToArray(String filePath) { // throws file not found exception?
        ArrayList<Integer> arrayList = new ArrayList<>();
        try (
                BufferedReader input = new BufferedReader(new FileReader(filePath));
        ) {
            String line = input.readLine();
            while (line != null) {
                arrayList.add(Integer.parseInt(line));
                line = input.readLine();
            }
        }
        catch (FileNotFoundException ex){
            System.out.println("File " + filePath + " does not exist.");
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return arrayList.stream().mapToInt(i->i).toArray();
    }

}

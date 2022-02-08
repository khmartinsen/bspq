package bspq.bspqtools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Coset {
    final int firstZero;
    final int lastZero;
    public final int[] coordinates;
    String path;
    ArrayList<String> moves;
    int lastMoveOffset = 0;


    public Coset(int[] array, String path) {
        firstZero = array[0];
        lastZero = array[1];
        coordinates = Arrays.copyOfRange(array, 2, array.length);
        this.path = path;
        moves = parsePathString(path);

        String lastMove = moves.get(moves.size() - 1);

        for (int i = 0; i < lastMove.length(); i++) {
            if (lastMove.charAt(i) == 'B') lastMoveOffset--;
            else if (lastMove.charAt(i) == 'b') lastMoveOffset++;
        }

    }

    public int getFirstZero() {
        return firstZero;
    }

    public int getLastZero() {
        return lastZero;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public String getPath() {
        return path;
    }

    public static ArrayList<String> parsePathString(String path) {
        ArrayList<String> moves = new ArrayList<>();
        int start = 0;
        for(int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == 't' || path.charAt(i) == 'T') {
                moves.add(path.substring(start, i));
                start = i + 1;
            }
        }
        return moves;
    }
}




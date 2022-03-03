package bspq.tools;

import java.util.ArrayList;
import java.util.Arrays;

public class Coset {
    final int firstZero;
    final int[] coordinates;
    String path;
    ArrayList<String> moves = new ArrayList<>();
    int lastMoveOffset = 0;
    boolean direction;


    public Coset(int[] array, String path) {
        firstZero = array[0];
        coordinates = Arrays.copyOfRange(array, 1, array.length);
        this.path = path;

        if (path.equals("mainline")) {
            moves.add("mainline");
        }
        else {
            moves = parsePathString(path);

            String lastMove = moves.get(moves.size() - 1);

            for (int i = 0; i < lastMove.length(); i++) {
                if (lastMove.charAt(i) == 'B') lastMoveOffset--;
                else if (lastMove.charAt(i) == 'b') lastMoveOffset++;

                if (lastMove.charAt(i) == 't') direction = true;
                else if (lastMove.charAt(i) == 'T') direction = false;
            }
        }
    }

    public int getFirstZero() {
        return firstZero;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<String> getMoves() {
        return moves;
    }

    public int getLastMoveOffset() {
        return lastMoveOffset;
    }

    public boolean getDirection() {
        return direction;
    }

    public static ArrayList<String> parsePathString(String path) {
        ArrayList<String> moves = new ArrayList<>();
        int start = 0;
        for(int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == 't' || path.charAt(i) == 'T') {
                moves.add(path.substring(start, i + 1));
                start = i + 1;
            }
        }
        return moves;
    }
}




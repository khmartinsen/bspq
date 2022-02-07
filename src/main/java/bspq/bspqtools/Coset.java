package bspq.bspqtools;

import java.util.Arrays;

public class Coset {
    final int firstZero;
    final int lastZero;
    int[] coordinates;
    String path;

    Coset(int[] array) {
        firstZero = array[0];
        lastZero = array[1];
        coordinates = Arrays.copyOfRange(array, 2, array.length);
    }
}

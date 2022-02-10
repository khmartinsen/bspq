/*
Kevin Martinsen
Date: 08/10/2021

Final Project
Baumslag-Solitar groups: Brick Laying Algorithm for the Horocyclic Subgroup (mainline)

Let 0 < p < q.

This version includes lastRadiusPosition function at the end
*/
package bspq.bspqtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BS_Bricklaying {
    public static void main(String[] args) {
        // Add system args option and default to userMenu

        userMenu();

        //accumulate and print
        //int[] count = accumulate(mainline, findLargestRadius(mainline, q, countToPosition));
        //System.out.print(java.util.Arrays.toString(mainline));
        //System.out.println("");
    }

    public static void userMenu() {
        // Guide the user through different choices
        Scanner input = new Scanner(System.in);

        System.out.println("Baumslag-Solitar Brick Laying Algorithm for the Horocyclic Subgroup");
        System.out.print("Enter p: ");
        final int p = input.nextInt();
        System.out.print("Enter q: ");
        final int q = input.nextInt();

        if (p < 1 || q <= p) {
            System.out.println("Make sure 0 < p < q.");
            return;
        }

        System.out.println("1) Print accumulate output");
        System.out.println("2) Save the mainline to a file");
        System.out.println("3) Find the last instance of r (when p|q)");
        System.out.print("Choose a selection: ");
        int userInput = input.nextInt();

        int distance = 0;
        int[] mainline;

        switch (userInput) {
            case 1:
                System.out.print("Enter a distance to calculate to: ");
                distance = input.nextInt();
                mainline = brickLaying(p, q, distance);
                printAccumulate(mainline, q);
                break;
            case 2:
                System.out.print("Enter a distance to calculate to: ");
                distance = input.nextInt();
                input.nextLine(); // burn an input due
                System.out.println("Default location is BS" + p + "_" + q + "/ in " + System.getProperty("user.dir"));
                System.out.print("Enter a directory (without /) or press enter for default: ");
                String directory = input.nextLine().trim();

                if (directory.isEmpty()) {
                    directory = "BS" + p + "_" + q;
                }

                File file = new File(directory + "/" + "mainline");

                if (file.exists()) {
                    System.out.println("File " + file.getName() + " already exists.");
                    System.out.print("Would you like to override? (y/N): ");
                    if ((input.next().trim().compareTo("y")) < 1) {
                        System.out.println("Stopping operation.");
                        System.exit(1);
                    }
                }

                mainline = brickLaying(p, q, distance);
                writeToFile(mainline, file);
                System.out.println("Saved to file: " + file.getName());
                break;
            case 3:
                System.out.print("Enter the radius: ");
                int radius = input.nextInt();
                System.out.printf("The last instance of %d is at %d. \n", radius, lastRadiusPosition(p, q, radius));
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }

    public static int[] brickLaying(final int p, final int q, final int distance) {
        // The main algorithm for building the mainline/horocyclic subgroup for BS(p,q)
        // returns the main line up to given distance

        // Round up to the next brick past maxPosition
        // This helps ensure we count correctly up to MaxPosition
        int maxPosition = ((distance / q) + 1) * q;

        int[] mainline = new int[maxPosition + 1];
        mainline[0] = 0;

        // should this be just i or q * i?
        int initialZone = q * (int)Math.ceil(p / (double)(q - p));

        // build the brick from b^0 to b^q
        mainline[q] = Math.min(p + 2, q);
        interpolateWhile(mainline, 0, q);

        // build from b^q to b^ initialZone
        for (int i = q; i < initialZone && (i + q) < maxPosition; i = i + q) {
            int nextValue = mainline[i] - i + (p * ((i / q) + 1)) + 2;
            mainline[i + q] = nextValue;
            interpolateWhile(mainline, i, i + q);
        }

        // Then a final for loop until the end of the required maxPosition
        // calculate the edges of the next brick and then interpolate
        // Starts at ceiling of p / (q - p)
        for (int i = initialZone; (i + q) <= maxPosition; i = i + q) {
            int x = ((i+q) / q) * p;
            mainline[i + q] = mainline[x] + 2;
            interpolateWhile(mainline, i, i + q);
        }

        int[] outputArray = new int[distance + 1];
        System.arraycopy(mainline, 0, outputArray, 0, outputArray.length);

        return outputArray;
    }

    public static void interpolateWhile(int[] mainline, final int start, final int end) {
        // accidental logical error and we look at the empty mainline where the entries are 0
        if (start + 1 >= end) return;

        int moveLeft = 0;
        int moveRight = 0;

        while (start + moveLeft + 1 < end - moveRight) {
            if (mainline[start + moveLeft] < mainline[end - moveRight]) {
                mainline[start + moveLeft + 1] = mainline[start + moveLeft] + 1;
                moveLeft++;
            }
            else {
                mainline[end - moveRight - 1] = mainline[end - moveRight] + 1;
                moveRight++;
            }
            //System.out.println(start + moveLeft + 1 + " " + (end - moveRight));
        }
    }

    public static int findLargestRadius(int[] mainline, final int q) {
        // Find the largest radius in the main line by looking at the largest qth position pair sums
        // see proof (X)
        int largestRadius = 0;
        int largestPairSum = 0;
        int largestQPosition = 0;

        // search from 0 and q up to the final full brick before any left over from q^k up to mainline.length
        for (int i = q; i < mainline.length; i = i + q) {
            if (mainline[i - q] + mainline[i] > largestPairSum) {
                largestPairSum = mainline[i - q] + mainline[q];
                largestQPosition = i;
            }
        }

        // largestQPosition <= mainline.legnth
        for (int i = largestQPosition - q; i <= largestQPosition; i++) {
            if (mainline[i] > largestRadius) largestRadius = mainline[i];
        }

        // Now we need to compare the largestRadius for numbers between the left over
        // between q^k and the end of the mainline array
        int endOfLastBrick = q * (mainline.length / q);
        for (int i = endOfLastBrick; i < mainline.length; i++) {
            if (mainline[i] > largestRadius) largestRadius = mainline[i];
        }

        return largestRadius;
    }

    public static int[] accumulate(int[] mainline, final int largestRadius) {
        // counts the number of r's and outputs the results in an array (only on the right side)
        int [] countArray = new int[largestRadius + 1];

        for (int e: mainline) {
            if (e <= largestRadius)	countArray[e]++;
        }

        // Double for the right symmetrical side

        for (int e: mainline) {
            if (e <= largestRadius) countArray[e] *= 2;
        }

        // Manually set 0 to count 1 incase there are extra 0's at the end of the array
        countArray[0] = 1;

        return countArray;
    }

    public static void printAccumulate(int[] mainline, final int q) {
        int[] countArray = accumulate(mainline, findLargestRadius(mainline, q));
        for (int i = 0; i < countArray.length; i++) {
            System.out.println(i + " " + countArray[i]);
        }
    }

    public static int lastRadiusPosition(final int p, final int q, final int r) {
        // outputs the where the furthest instance of r

        if (r < q) {
            System.out.println("Radius is in the first horobrick.");
            return 0;
        }

        boolean sameParity = (r - p) % 2 == 0;

        if (sameParity) {
            int height = (r - p) / 2;
            return (int)Math.pow(q, height);
        }
        else {
            int closeRadius = r - 1;
            int height = (closeRadius - p) / 2;
            return (int)Math.pow(q, height) + 1;
        }
    }

    private static void writeToFile(int[] mainline, File file) {
        try(
                PrintWriter output = new PrintWriter(file);
                )
        {
            // first and last zero are at index 0 in the array
            output.println(0);
            output.println(0);
            for (int i : mainline) {
                output.println(i);
            }
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
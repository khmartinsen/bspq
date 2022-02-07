package bspq.bsviewer;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class BSPane extends BorderPane {
    final int p;
    final int q;

    double firstTickSpacing = 50; // since q > p, this is the minimal for tick spacing
    double tickSpacingScaling; // q/p when going up and p/q going down
    double firstYLocation;
    double tickSize = 5; // height above line in pixels
    double lineSpacing = 100; // space between lines (maybe should be negative for when going up
    boolean showVerticalLine;
    boolean visibleIndex = false; // display absolute count with relative count

    // array and p,q modulus values
    int indexStart = 0;
    boolean direction = false; // true up, false down (change to enum)
    int currentMod;
    int nextMod; // the mod of the next line (replace with an if statement in the drawBS method?

    ArrayList<int[]> bsArrays = new ArrayList<>();
    ArrayList<Pair<Integer,Integer>> zeroLocation = new ArrayList<>();
    ArrayList<String> movesString = new ArrayList<>(); // will be bsArray.length - 1
    ArrayList<Integer> movesOffset = new ArrayList<>(); // will be bsArray.length - 1

    Pane centerPane = new Pane();

    public BSPane(final int p, final int q, boolean direction) {
        this.p = p;
        this.q = q;
        this.direction = direction;

        if (direction) {
            lineSpacing = -100;
            currentMod = p; // if the sheet is going up we draw lines down every p
            nextMod = q;
            tickSpacingScaling = (double) q / p;
        }
        else {
            currentMod = q;
            nextMod = p;
            tickSpacingScaling = (double) p / q;
        }

        HBox topMenu = new HBox();

        setTop(topMenu);
        setCenter(centerPane);

        Text bsInfo = new Text("p: " + String.valueOf(p) + "\nq: " + String.valueOf(q));
        CheckBox indicesBox = new CheckBox("Show Indices");
        TextField indexField = new TextField("Jump to index x");

        topMenu.setSpacing(30);
        topMenu.setAlignment(Pos.CENTER);
        topMenu.getChildren().addAll(bsInfo, indicesBox, indexField);

        // handlers and listeners

        indicesBox.setOnAction(event -> {
            setVisibleIndex(indicesBox.isSelected());
            this.requestFocus();
        });

        indexField.setOnAction(e -> {
            try {
                setIndex(Integer.parseInt(indexField.getText()));
                requestFocus();
            }
            catch (NumberFormatException ex) {
                indexField.setText("");
                indexField.setPromptText("Invalid Number");
                requestFocus();
            }
        });

        // zoom in by changing the tick spacing with scroll wheel
        setOnScroll(event -> {
            if (event.getDeltaY() > 0) incSpacing();
            else if (event.getDeltaY() < 0) decSpacing();
        });

        setOnKeyPressed(event -> {
            //bsPane.requestFocus();
            switch (event.getCode()) {
                case RIGHT:
                    incIndex();
                    break;
                case LEFT:
                    decIndex();
                    break;
                case R:
                    resetSpacing();
                    break;
            }
        });

        widthProperty().addListener(e -> drawBS());
        drawBS();
    }

    public void drawBS() {
        centerPane.getChildren().clear();

        setTranslateX(30); // change based on max label size like BBT

        if (direction) {
            // if up we start drawing the line towards the bottom
            firstYLocation = getHeight() - 100;
        }
        else {
            firstYLocation = 100;
        }


        double lineY = firstYLocation;
        double tickSpacing = firstTickSpacing;
        int arrayIndex = indexStart + zeroLocation.get(0).getKey();
        double tickOffsetX = widthProperty().getValue() / 2;

        showVerticalLine = false;

        for (int[] coordinates: bsArrays) { // change to int i and use zeroLocation and other arrays
            drawLines(lineY, tickOffsetX, tickSpacing, coordinates, arrayIndex);



            double currentTickOffset = (arrayIndex % nextMod) * tickSpacing; // previous line spacing for a horobrick in pixels
            arrayIndex = (arrayIndex / nextMod) * currentMod + (int) Math.ceil(((arrayIndex % nextMod) * currentMod) / (double) nextMod);
            lineY += lineSpacing;
            tickSpacing *= tickSpacingScaling;
            tickOffsetX = tickOffsetX + (arrayIndex % currentMod) * tickSpacing - currentTickOffset;
            showVerticalLine = true; // true after the first line is drawn
        }

        /*
                for (int[] coordinates: bsArrays) { // change to int i and use zeroLocation and other arrays
            drawLines(lineY, tickOffsetX, tickSpacing, coordinates, arrayIndex);

            double currentTickOffset = (arrayIndex % nextMod) * tickSpacing; // previous line spacing for a horobrick in pixels
            arrayIndex = (arrayIndex / nextMod) * currentMod + (int) Math.ceil(((arrayIndex % nextMod) * currentMod) / (double) nextMod);
            lineY += lineSpacing;
            tickSpacing *= tickSpacingScaling;
            tickOffsetX = tickOffsetX + (arrayIndex % currentMod) * tickSpacing - currentTickOffset;
            showVerticalLine = true; // true after the first line is drawn
        }
         */

    }

    private void drawLines(double lineY, double tickOffsetX, double tickSpacing, int[] coordinates, int arrayIndex) {
        Line line = new Line(0, lineY, widthProperty().doubleValue(), lineY);
        //Text move = new Text(movesString.get(0));
        //move.setY(lineY - lineSpacing / 2);
        // movesString[]
        centerPane.getChildren().add(line);

        for (double i = 0; i < widthProperty().doubleValue(); i += tickSpacing) {
            Line tick = new Line(i + tickOffsetX, lineY - tickSize, i + tickOffsetX, lineY + tickSize);
            Text number = new Text(String.valueOf(coordinates[arrayIndex]));
            number.setX(i + tickOffsetX - number.getLayoutBounds().getWidth() / 2);
            number.setY(lineY - 10);
            centerPane.getChildren().addAll(tick, number);

            // throw everything above in this if statement and offset the numbers to the left or right for visibility?
            if (arrayIndex % currentMod == 0 && showVerticalLine) { // connecting vertical lines
                Line edge = new Line(i + tickOffsetX, lineY, i + tickOffsetX, lineY - lineSpacing); // line goes the opposite way the lines are being built
                edge.setOpacity(.6);
                centerPane.getChildren().add(edge);
            }

            if (visibleIndex) {
                Text absCount = new Text(String.valueOf(arrayIndex));
                absCount.setX(i + tickOffsetX - absCount.getLayoutBounds().getWidth() / 2);
                absCount.setY(lineY + 20); // put index under the tick marks
                centerPane.getChildren().add(absCount);
            }
            arrayIndex++;
        }
    }

    // ** Visual change control methods **

    public void incSpacing() {
        firstTickSpacing += 5;
        drawBS();
    }

    public void decSpacing() {
        if (firstTickSpacing - 5 > 5) {
            firstTickSpacing -= 5;
            drawBS();
        }
    }

    public void resetSpacing() {
        firstTickSpacing = 50;
        drawBS();
    }

    public void setVisibleIndex(boolean visibleIndex) {
        this.visibleIndex = visibleIndex;
        drawBS();
    }

    public void incIndex() {
        indexStart += 1;
        drawBS();
    }

    public void decIndex() {
        if (indexStart - 1 >= 0) {
            indexStart -= 1;
            drawBS();
        }
    }

    public void setIndex(int x) {
        if (x >= 0 && x < bsArrays.get(0).length) {
            indexStart = x;
            drawBS();
        }
    }

    public void resetStart() {
        indexStart = 0;
        drawBS();
    }

    public void addArray(int[] array) {
        // first two integers in the array are the location of the first and last zero
        zeroLocation.add(new Pair<Integer,Integer>(array[0],array[1]));
        bsArrays.add(Arrays.copyOfRange(array, 2, array.length));
    }
}
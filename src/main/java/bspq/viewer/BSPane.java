package bspq.viewer;

import bspq.tools.Coset;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;

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
    int startIndex = 0;
    boolean direction = false; // true up, false down (change to enum)
    int currentMod;
    int nextMod; // the mod of the next line (replace with an if statement in the drawBS method?

    ArrayList<Coset> cosets = new ArrayList<>();

    /*
    ArrayList<int[]> bsArrays = new ArrayList<>();
    ArrayList<Pair<Integer,Integer>> zeroIndex = new ArrayList<>();
    ArrayList<String> movesString = new ArrayList<>(); // will be bsArray.length - 1
    ArrayList<Integer> movesOffset = new ArrayList<>(); // will be bsArray.length - 1\
    */

    Pane centerPane = new Pane();

    public BSPane(final int p, final int q, boolean direction) {
        this.p = p;
        this.q = q;
        this.direction = direction;

        if (direction) {
            lineSpacing = -100;
            currentMod = q; // if the sheet is going up we draw lines down every p
            nextMod = p;
            tickSpacingScaling = (double) q / p;
        }
        else {
            currentMod = p;
            nextMod = q;
            tickSpacingScaling = (double) p / q;
        }

        HBox topMenu = new HBox();

        setTop(topMenu);
        setCenter(centerPane);

        Text bsInfo = new Text("p: " + p + "\nq: " + q);
        CheckBox indicesBox = new CheckBox("Show Indices");
        TextField indexField = new TextField();
        indexField.setPromptText("Jump to index x");

        topMenu.setSpacing(30);
        topMenu.setAlignment(Pos.CENTER);
        topMenu.getChildren().addAll(bsInfo, indicesBox, indexField);

        // handlers and listeners

        indicesBox.setOnAction(event -> {
            setVisibleIndex(indicesBox.isSelected());
            requestFocus();
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
        //drawBS();
    }

    public void drawBS() {
        centerPane.getChildren().clear();

        setTranslateX(30); // change based on max label size like BBT

        if (direction) {
            // if up we start drawing the line towards the bottom
            firstYLocation = getHeight() - 150;
        }
        else {
            firstYLocation = 100;
        }

        showVerticalLine = false;

        double lineY = firstYLocation;
        double tickSpacing = firstTickSpacing;
        double oldTickSpacing = tickSpacing;
        double tickOffsetX = 0.0;
        double indexScaling = 1.0;
        int relativeIndex = startIndex;
        double lengthToEdge = 0.0;

        // draw the first line
        drawLines(lineY, tickSpacing, tickOffsetX, relativeIndex + cosets.get(0).getFirstZero(), cosets.get(0));

        // draw the next lines
        for (int i = 1; i < cosets.size(); i++) { // change to int i and use zeroLocation and other arrays
            lineY += lineSpacing;
            oldTickSpacing = tickSpacing;
            tickSpacing *= tickSpacingScaling;
            showVerticalLine = true; // true after the first line is drawn
            indexScaling = (double)nextMod / currentMod;

            // go the the next right edge
            int indexOffset = (((relativeIndex - cosets.get(i).getLastMoveOffset()) % currentMod) + currentMod) % currentMod; // non-negative value, distance to right horobrick edge
            relativeIndex = (int)((relativeIndex + indexOffset) * indexScaling); // relative starting point for the next line
            tickOffsetX += (indexOffset + cosets.get(0).getLastMoveOffset()) * oldTickSpacing; // distance for that relative index to start at

            drawLines(lineY, tickSpacing, tickOffsetX, relativeIndex + cosets.get(i).getFirstZero(), cosets.get(i));
        }
    }

    private void drawLines(final double lineY, final double tickSpacing, double tickOffsetX,int relativeIndex, Coset coset) {
        int[] coordinates = coset.getCoordinates();

        // move index and tickOffsetX over to the left
        while (tickOffsetX - tickSpacing >= 0.0 || tickOffsetX < 0) {
            if (tickOffsetX < 0) {
                tickOffsetX += tickSpacing;
                relativeIndex++;
            }
            else {
                tickOffsetX -= tickSpacing;
                relativeIndex--;
            }
        }
        int arrayIndex = relativeIndex; // used so we don't increment relativeIndex or tickOffsetX


        Line line = new Line(0, lineY, widthProperty().doubleValue(), lineY);

        Text moveText = new Text(coset.getMoves().get(coset.getMoves().size() - 1));
        moveText.setY(lineY - (lineSpacing / 2));
        centerPane.getChildren().addAll(line, moveText);

        for (double i = tickOffsetX; i < widthProperty().doubleValue(); i += tickSpacing) {
            if (arrayIndex >= 0 && arrayIndex < coordinates.length) {
                Line tick = new Line(i, lineY - tickSize, i, lineY + tickSize);
                Text number = new Text(String.valueOf(coordinates[arrayIndex]));
                number.setX(i - number.getLayoutBounds().getWidth() / 2);
                number.setY(lineY - 10);
                centerPane.getChildren().addAll(tick, number);

                // throw everything above in this if statement and offset the numbers to the left or right for visibility?
                if ((arrayIndex - coset.getFirstZero()) % nextMod == 0 && showVerticalLine) { // connecting vertical lines
                    Line edge = new Line(i, lineY, i, lineY - lineSpacing); // line goes the opposite way the lines are being built
                    edge.setOpacity(.6);
                    centerPane.getChildren().add(edge);
                }

                if (visibleIndex) {
                    Text absCount = new Text(String.valueOf(arrayIndex - coset.getFirstZero()));
                    absCount.setX(i - absCount.getLayoutBounds().getWidth() / 2);
                    absCount.setY(lineY + 20); // put index under the tick marks
                    centerPane.getChildren().add(absCount);
                }
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
        startIndex += 1;
        drawBS();
    }

    public void decIndex() {
        //if (indexStart - 1 >= cosets.get(0).getFirstZero()) {
            startIndex -= 1;
            drawBS();
        //}
    }

    public void setIndex(int x) {
        int indexOffset = cosets.get(0).getFirstZero();
        if (indexOffset + x >= 0 && x < cosets.get(0).getCoordinates().length - indexOffset) {
            startIndex = x;
            drawBS();
        }
    }

    public void resetStart() {
        startIndex = 0;
        drawBS();
    }

    public void addCosets(ArrayList<Coset> cosets) {
        this.cosets.addAll(cosets);
    }
}